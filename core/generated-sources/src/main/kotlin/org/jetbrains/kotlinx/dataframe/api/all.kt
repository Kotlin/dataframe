package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.Issues
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.onResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.nullableNothingType
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/**
 * Returns `true` if all [<code>values</code>][values] match the given [<code>predicate</code>][predicate] or [<code>values</code>][values] is empty.
 *
 * For more information: [See `all` on the documentation website.](https://kotlin.github.io/dataframe/all.html)
 */
public fun <T> DataColumn<T>.all(predicate: Predicate<T>): Boolean = values.all(predicate)

/**
 * Returns `true` if all [<code>values</code>][values] are `null` or [<code>values</code>][values] is empty.
 *
 * For more information: [See `all` on the documentation website.](https://kotlin.github.io/dataframe/all.html)
 */
public fun <C> DataColumn<C>.allNulls(): Boolean =
    size == 0 ||
        type() == nullableNothingType ||
        all { it == null }

// endregion

// region DataRow

public fun DataRow<*>.allNA(): Boolean = owner.columns().all { it[index()].isNA }

// endregion

// region DataFrame

/**
 * Returns `true` if all [<code>rows</code>][rows] match the given [<code>predicate</code>][predicate] or [<code>rows</code>][rows] is empty.
 *
 * For more information: [See `all` on the documentation website.](https://kotlin.github.io/dataframe/all.html)
 */
public inline fun <T> DataFrame<T>.all(predicate: RowFilter<T>): Boolean = rows().all { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## All Flavors of All (Cols) [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface AllColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Grammar of All Flavors of All (Cols):
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
     *  `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colSelector: `[<code>`ColumnSelector`</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
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
     *  [<code>**`all`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**
     *
     *  `| `**`all`**`(`[<code>**`Before`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[<code>**`After`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[<code>**`From`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[<code>**`UpTo`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[<code>`colSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSelectorDef]**` }`**` )`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`all`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.all`**`(`[<code>**`Before`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[<code>**`After`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[<code>**`From`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[<code>**`UpTo`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` )`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`allCols`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]**`()`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.allCols`**`(`[<code>**`Before`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[<code>**`After`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[<code>**`From`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[<code>**`UpTo`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[<code>`colSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSelectorDef]**` }`**` )`
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

        /** [<code>**`all`**</code>][ColumnsSelectionDsl.all] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`all`**</code>][ColumnsSelectionDsl.all] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`allCols`**</code>][ColumnsSelectionDsl.allCols] */
        public typealias ColumnGroupName = Nothing

        /** [<code>**`Before`**</code>][ColumnsSelectionDsl.allColsBefore] */
        public typealias Before = Nothing

        /** [<code>**`After`**</code>][ColumnsSelectionDsl.allAfter] */
        public typealias After = Nothing

        /** [<code>**`From`**</code>][ColumnsSelectionDsl.allColsFrom] */
        public typealias From = Nothing

        /** [<code>**`UpTo`**</code>][ColumnsSelectionDsl.allColsUpTo] */
        public typealias UpTo = Nothing
    }

    // region all

    /**
     * ## All (Cols)
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [<code>none</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [<code>cols()</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * For more information: [See `all`/`allCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols)
     *
     * #### For example:
     * `df.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]`  { "a" in  `[<code>name</code>][ColumnWithPath.name]` }.`[<code>all</code>][ColumnSet.all]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: This is an identity call and can be omitted in most cases.
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("All0")
    public fun <C> ColumnSet<C>.all(): ColumnSet<C> = allColumnsInternal().cast()

    /**
     * ## All (Cols)
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [<code>none</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [<code>cols()</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * For more information: [See `all`/`allCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols)
     *
     * #### For example:
     * `df.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>all</code>][ColumnsSelectionDsl.all]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("All1")
    public fun ColumnsSelectionDsl<*>.all(): ColumnSet<*> = asSingleColumn().allColumnsInternal()

    /**
     * ## All (Cols)
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [<code>none</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [<code>cols()</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * For more information: [See `all`/`allCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols)
     *
     * #### For example:
     * `df.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myGroup.`[<code>allCols</code>][SingleColumn.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Interpretable("All2")
    public fun SingleColumn<DataRow<*>>.allCols(): ColumnSet<*> = ensureIsColumnGroup().allColumnsInternal()

    /**
     * ## All (Cols)
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [<code>none</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [<code>cols()</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * For more information: [See `all`/`allCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols)
     *
     * #### For example:
     * `df.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myGroupCol".`[<code>allCols</code>][String.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.allCols(): ColumnSet<*> = columnGroup(this).allCols()

    /**
     * ## All (Cols)
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [<code>none</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [<code>cols()</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * For more information: [See `all`/`allCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols)
     *
     * #### For example:
     * `df.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::columnGroup.`[<code>allCols</code>][KProperty.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allCols(): ColumnSet<*> = columnGroup(this).allCols()

    /**
     * ## All (Cols)
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [<code>none</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [<code>cols()</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * For more information: [See `all`/`allCols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols)
     *
     * #### For example:
     * `df.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myGroup"].`[<code>allCols</code>][ColumnPath.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.allCols(): ColumnSet<*> = columnGroup(this).allCols()

    // endregion

    // region allAfter

    /**
     * ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][ColumnsResolver] and absolutely.
     */
    private typealias AllAfterDocs = Nothing

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]` { myColumn `[<code>in</code>][String.contains]` it.`[<code>name</code>][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        allAfterInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: ColumnPath): ColumnSet<C> =
        allAfterInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allAfter(column: String): ColumnSet<C> = allAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllAfter0")
    public fun <C> ColumnSet<C>.allAfter(column: AnyColumnReference): ColumnSet<C> = allAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnSet<C>.allAfter(column: KProperty<*>): ColumnSet<C> =
        allAfter(column.toColumnAccessor().path())

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllAfter1")
    public fun <T> ColumnsSelectionDsl<T>.allAfter(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: ColumnPath): ColumnSet<*> = asSingleColumn().allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: String): ColumnSet<*> = allAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllAfter2")
    public fun ColumnsSelectionDsl<*>.allAfter(column: AnyColumnReference): ColumnSet<*> = allAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnsSelectionDsl<*>.allAfter(column: KProperty<*>): ColumnSet<*> =
        allAfter(column.toColumnAccessor().path())

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <T> SingleColumn<DataRow<T>>.allColsAfter(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allAfterInternal { it.data == resolvedCol!! }
    }

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: ColumnPath): ColumnSet<*> {
        var path: ColumnPath? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { path = it!!.path }
            .allAfterInternal {
                // accept both relative and full column path
                it.path == path!! + column || it.path == column
            }
    }

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: String): ColumnSet<*> = allColsAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        allColsAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        allColsAfter(column.toColumnAccessor().path())

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsAfter</code>][kotlin.String.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsAfter</code>][kotlin.String.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsAfter</code>][kotlin.String.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: String): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsAfter</code>][kotlin.String.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsAfter</code>][kotlin.String.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.allColsAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /**
     * ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[<code>allColsAfter</code>][kotlin.reflect.KProperty.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsAfter(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[<code>allColsAfter</code>][kotlin.reflect.KProperty.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[<code>allColsAfter</code>][kotlin.reflect.KProperty.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: String): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[<code>allColsAfter</code>][kotlin.reflect.KProperty.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[<code>allColsAfter</code>][kotlin.reflect.KProperty.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: String): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allAfter</code>][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsAfter</code>][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allAfter</code>][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsAfter</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnPath.allColsAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    // endregion

    // region allFrom

    /**
     * ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][ColumnsResolver] and absolutely.
     */
    private typealias AllFromDocs = Nothing

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]` { myColumn `[<code>in</code>][String.contains]` it.`[<code>name</code>][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allFrom(column: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        allFromInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allFrom(column: ColumnPath): ColumnSet<C> =
        allFromInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allFrom(column: String): ColumnSet<C> = allFrom(pathOf(column))

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllFrom0")
    public fun <C> ColumnSet<C>.allFrom(column: AnyColumnReference): ColumnSet<C> = allFrom(column.path())

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnSet<C>.allFrom(column: KProperty<*>): ColumnSet<C> = allFrom(column.toColumnAccessor().path())

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllFrom1")
    public fun <T> ColumnsSelectionDsl<T>.allFrom(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: ColumnPath): ColumnSet<*> = asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: String): ColumnSet<*> = asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllFrom2")
    public fun ColumnsSelectionDsl<*>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnsSelectionDsl<*>.allFrom(column: KProperty<*>): ColumnSet<*> = asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsFrom</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <T> SingleColumn<DataRow<T>>.allColsFrom(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allFromInternal { it.data == resolvedCol!! }
    }

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsFrom</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: ColumnPath): ColumnSet<*> {
        var path: ColumnPath? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { path = it!!.path }
            .allFromInternal {
                // accept both relative and full column path
                it.path == path!! + column || it.path == column
            }
    }

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsFrom</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: String): ColumnSet<*> = allColsFrom(pathOf(column))

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsFrom</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        allColsFrom(column.path())

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsFrom</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        allColsFrom(column.toColumnAccessor().path())

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsFrom</code>][kotlin.String.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: ColumnSelector<*, *>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsFrom</code>][kotlin.String.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsFrom</code>][kotlin.String.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsFrom</code>][kotlin.String.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsFrom</code>][kotlin.String.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /**
     * ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsFrom</code>][kotlin.reflect.KProperty.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsFrom(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsFrom</code>][kotlin.reflect.KProperty.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsFrom</code>][kotlin.reflect.KProperty.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsFrom</code>][kotlin.reflect.KProperty.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsFrom</code>][kotlin.reflect.KProperty.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return an empty [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allFrom</code>][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsFrom</code>][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allFrom</code>][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allFrom</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnPath.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    // endregion

    // region allBefore

    /**
     * ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][ColumnsResolver] and absolutely.
     */
    private typealias AllBeforeDocs = Nothing

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]` { myColumn `[<code>in</code>][String.contains]` it.`[<code>name</code>][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allBefore(column: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        allBeforeInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allBefore(column: ColumnPath): ColumnSet<C> =
        allBeforeInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allBefore(column: String): ColumnSet<C> = allBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllBefore0")
    public fun <C> ColumnSet<C>.allBefore(column: AnyColumnReference): ColumnSet<C> = allBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnSet<C>.allBefore(column: KProperty<*>): ColumnSet<C> =
        allBefore(column.toColumnAccessor().path())

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllBefore1")
    public fun <T> ColumnsSelectionDsl<T>.allBefore(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: String): ColumnSet<*> = allBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllBefore2")
    public fun ColumnsSelectionDsl<*>.allBefore(column: AnyColumnReference): ColumnSet<*> = allBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnsSelectionDsl<*>.allBefore(column: KProperty<*>): ColumnSet<*> =
        allBefore(column.toColumnAccessor().path())

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <T> SingleColumn<DataRow<T>>.allColsBefore(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allBeforeInternal { it.data == resolvedCol }
    }

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: ColumnPath): ColumnSet<*> {
        var path: ColumnPath? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { path = it!!.path }
            .allBeforeInternal { it.path == path!! + column || it.path == column }
    }

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: String): ColumnSet<*> = allColsBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        allColsBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        allColsBefore(column.toColumnAccessor().path())

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsBefore</code>][kotlin.String.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsBefore</code>][kotlin.String.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsBefore</code>][kotlin.String.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: String): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsBefore</code>][kotlin.String.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsBefore</code>][kotlin.String.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.allColsBefore(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /**
     * ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsBefore</code>][kotlin.reflect.KProperty.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsBefore(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsBefore</code>][kotlin.reflect.KProperty.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsBefore</code>][kotlin.reflect.KProperty.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: String): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsBefore</code>][kotlin.reflect.KProperty.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsBefore</code>][kotlin.reflect.KProperty.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: String): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allBefore</code>][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsBefore</code>][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allBefore</code>][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsBefore</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnPath.allColsBefore(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    // endregion

    // region allUpTo

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][ColumnsResolver] and absolutely.
     */
    private typealias AllUpToDocs = Nothing

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]` { myColumn `[<code>in</code>][String.contains]` it.`[<code>name</code>][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allUpTo(column: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        allUpToInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allUpTo(column: ColumnPath): ColumnSet<C> =
        allUpToInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allUpTo(column: String): ColumnSet<C> = allUpTo(pathOf(column))

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllUpTo0")
    public fun <C> ColumnSet<C>.allUpTo(column: AnyColumnReference): ColumnSet<C> = allUpTo(column.path())

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnSet<C>.allUpTo(column: KProperty<*>): ColumnSet<C> = allUpTo(column.toColumnAccessor().path())

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllUpTo1")
    public fun <T> ColumnsSelectionDsl<T>.allUpTo(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: ColumnPath): ColumnSet<*> = asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: String): ColumnSet<*> = asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllUpTo2")
    public fun ColumnsSelectionDsl<*>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnsSelectionDsl<*>.allUpTo(column: KProperty<*>): ColumnSet<*> = asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <T> SingleColumn<DataRow<T>>.allColsUpTo(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allUpToInternal { it.data == resolvedCol!! }
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: ColumnPath): ColumnSet<*> {
        var path: ColumnPath? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { path = it!!.path }
            .allUpToInternal {
                // accept both relative and full column path
                it.path == path!! + column || it.path == column
            }
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: String): ColumnSet<*> = allColsUpTo(pathOf(column))

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        allColsUpTo(column.path())

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        allColsUpTo(column.toColumnAccessor().path())

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsUpTo</code>][kotlin.String.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: ColumnSelector<*, *>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsUpTo</code>][kotlin.String.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsUpTo</code>][kotlin.String.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsUpTo</code>][kotlin.String.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[<code>allColsUpTo</code>][kotlin.String.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.allColsUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsUpTo</code>][kotlin.reflect.KProperty.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsUpTo(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsUpTo</code>][kotlin.reflect.KProperty.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsUpTo</code>][kotlin.reflect.KProperty.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsUpTo</code>][kotlin.reflect.KProperty.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[<code>allColsUpTo</code>][kotlin.reflect.KProperty.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
     *
     * If [column] does not exist, the function will return a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [<code>ColumnSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * For more information: [See all(Cols) After/Before/From/UpTo on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-after-before-from-up-to)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allUpTo</code>][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[<code>allColsUpTo</code>][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>allUpTo</code>][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[<code>allColsUpTo</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [<code>`all(Cols)`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [<code>`all(Cols)Before`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [<code>`all(Cols)After`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [<code>`all(Cols)From`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [<code>`all(Cols)UpTo`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnPath.allColsUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    // endregion
}

/**
 * If [<code>this</code>][this] is a [<code>SingleColumn</code>][SingleColumn] containing a single [<code>ColumnGroup</code>][ColumnGroup]
 * (like [<code>SingleColumn</code>][SingleColumn]`<`[<code>AnyRow</code>][AnyRow]`>` or [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl]), it
 * returns a [<code>(transformable) ColumnSet</code>][TransformableColumnSet] containing the children of this [<code>ColumnGroup</code>][ColumnGroup],
 * else it simply returns a [<code>(transformable) ColumnSet</code>][TransformableColumnSet] from [<code>this</code>][this]
 * (like when [<code>this</code>][this] is a [<code>ColumnSet</code>][ColumnSet]).
 */
@PublishedApi
internal fun ColumnsResolver<*>.allColumnsInternal(removePaths: Boolean = false): TransformableColumnSet<*> =
    transform { cols ->
        if (this is SingleColumn<*> && cols.singleOrNull()?.isColumnGroup() == true) {
            cols.single().let {
                if (removePaths) {
                    it.asColumnGroup().columns().map(AnyCol::addPath)
                } else {
                    it.cols()
                }
            }
        } else {
            cols
        }
    }

/**
 * Returns a new ColumnSet containing all columns after the first column that matches the given predicate.
 *
 * @param colByPredicate a function that takes a ColumnWithPath and returns true if the column matches the predicate, false otherwise
 * @return a new ColumnSet containing all columns after the first column that matches the given predicate
 */
@PublishedApi
internal inline fun ColumnsResolver<*>.allAfterInternal(crossinline colByPredicate: ColumnFilter<*>): ColumnSet<*> {
    var take = false
    return colsInternal {
        if (take) {
            true
        } else {
            take = colByPredicate(it)
            false
        }
    }
}

/**
 * Returns a column set containing all columns from the internal column set that satisfy the given predicate.
 *
 * @param colByPredicate the predicate used to determine if a column should be included in the resulting set
 * @return a column set containing all columns that satisfy the predicate
 */
@PublishedApi
internal inline fun ColumnsResolver<*>.allFromInternal(crossinline colByPredicate: ColumnFilter<*>): ColumnSet<*> {
    var take = false
    return colsInternal {
        if (take) {
            true
        } else {
            take = colByPredicate(it)
            take
        }
    }
}

/**
 * Returns a new ColumnSet containing all columns before the first column that satisfies the given predicate.
 *
 * @param colByPredicate the predicate function used to determine if a column should be included in the returned ColumnSet
 * @return a new ColumnSet containing all columns that come before the first column that satisfies the given predicate
 */
@PublishedApi
internal inline fun ColumnsResolver<*>.allBeforeInternal(crossinline colByPredicate: ColumnFilter<*>): ColumnSet<*> {
    var take = true
    return colsInternal {
        if (!take) {
            false
        } else {
            take = !colByPredicate(it)
            take
        }
    }
}

/**
 * Returns a ColumnSet containing all columns up to (and including) the first column that satisfies the given predicate.
 *
 * @param colByPredicate a predicate function that takes a ColumnWithPath and returns true if the column satisfies the desired condition.
 * @return a ColumnSet containing all columns up to the first column that satisfies the given predicate.
 */
@PublishedApi
internal inline fun ColumnsResolver<*>.allUpToInternal(crossinline colByPredicate: ColumnFilter<*>): ColumnSet<*> {
    var take = true
    return colsInternal {
        if (!take) {
            false
        } else {
            take = !colByPredicate(it)
            true
        }
    }
}

// endregion
