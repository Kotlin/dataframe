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
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.onResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/** Returns `true` if all [values] match the given [predicate] or [values] is empty. */
public fun <T> DataColumn<T>.all(predicate: Predicate<T>): Boolean = values.all(predicate)

/** Returns `true` if all [values] are `null` or [values] is empty. */
public fun <C> DataColumn<C>.allNulls(): Boolean = size == 0 || all { it == null }

// endregion

// region DataRow

public fun AnyRow.allNA(): Boolean = owner.columns().all { it[index()].isNA }

// endregion

// region DataFrame

/** Returns `true` if all [rows] match the given [predicate] or [rows] is empty. */
public inline fun <T> DataFrame<T>.all(predicate: RowFilter<T>): Boolean = rows().all { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## All Flavors of All (Cols) [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface AllColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Grammar of All Flavors of All (Cols):
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
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colSelector: `[`ColumnSelector`][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[`ColumnFilter`][org.jetbrains.kotlinx.dataframe.ColumnFilter]
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
     *  [**`all`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**
     *
     *  `| `**`all`**`(`[**`Before`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[**`After`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[**`From`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[**`UpTo`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[`colSelector`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSelectorDef]**` }`**` )`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`all`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.all`**`(`[**`Before`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[**`After`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[**`From`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[**`UpTo`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` )`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`allCols`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]**`()`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.allCols`**`(`[**`Before`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[**`After`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[**`From`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[**`UpTo`**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[`colSelector`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSelectorDef]**` }`**` )`
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

        /** [**`all`**][ColumnsSelectionDsl.all] */
        public interface PlainDslName

        /** __`.`__[**`all`**][ColumnsSelectionDsl.all] */
        public interface ColumnSetName

        /** __`.`__[**`allCols`**][ColumnsSelectionDsl.allCols] */
        public interface ColumnGroupName

        /** [**`Before`**][ColumnsSelectionDsl.allColsBefore] */
        public interface Before

        /** [**`After`**][ColumnsSelectionDsl.allAfter] */
        public interface After

        /** [**`From`**][ColumnsSelectionDsl.allColsFrom] */
        public interface From

        /** [**`UpTo`**][ColumnsSelectionDsl.allColsUpTo] */
        public interface UpTo
    }

    // region all

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [none][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]`  { "a" in  `[name][ColumnWithPath.name]` }.`[all][ColumnSet.all]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: This is an identity call and can be omitted in most cases.
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [none][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[all][ColumnsSelectionDsl.all]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [none][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[allCols][SingleColumn.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [none][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[allCols][String.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [none][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::columnGroup.`[allCols][KProperty.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from [this],
     * the opposite of [none][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none].
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] without filter.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroup"].`[allCols][ColumnPath.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver] and absolutely.
     */
    private interface AllAfterDocs

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]` { myColumn `[in][String.contains]` it.`[name][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: ColumnFilter<C>): ColumnSet<C> =
        allAfterInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: ColumnPath): ColumnSet<C> =
        allAfterInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allAfter(column: String): ColumnSet<C> = allAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllAfter0")
    public fun <C> ColumnSet<C>.allAfter(column: AnyColumnReference): ColumnSet<C> = allAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allAfter(column: KProperty<*>): ColumnSet<C> =
        allAfter(column.toColumnAccessor().path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllAfter1")
    public fun <T> ColumnsSelectionDsl<T>.allAfter(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: ColumnPath): ColumnSet<*> = asSingleColumn().allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: String): ColumnSet<*> = allAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllAfter2")
    public fun ColumnsSelectionDsl<*>.allAfter(column: AnyColumnReference): ColumnSet<*> = allAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: KProperty<*>): ColumnSet<*> =
        allAfter(column.toColumnAccessor().path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: String): ColumnSet<*> = allColsAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        allColsAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        allColsAfter(column.toColumnAccessor().path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: String): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsAfter(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: String): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: String): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns after [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    // endregion

    // region allFrom

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver] and absolutely.
     */
    private interface AllFromDocs

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]` { myColumn `[in][String.contains]` it.`[name][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allFrom(column: ColumnFilter<C>): ColumnSet<C> =
        allFromInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allFrom(column: ColumnPath): ColumnSet<C> =
        allFromInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allFrom(column: String): ColumnSet<C> = allFrom(pathOf(column))

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllFrom0")
    public fun <C> ColumnSet<C>.allFrom(column: AnyColumnReference): ColumnSet<C> = allFrom(column.path())

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allFrom(column: KProperty<*>): ColumnSet<C> = allFrom(column.toColumnAccessor().path())

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllFrom1")
    public fun <T> ColumnsSelectionDsl<T>.allFrom(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: ColumnPath): ColumnSet<*> = asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: String): ColumnSet<*> = asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllFrom2")
    public fun ColumnsSelectionDsl<*>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: KProperty<*>): ColumnSet<*> = asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: String): ColumnSet<*> = allColsFrom(pathOf(column))

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        allColsFrom(column.path())

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        allColsFrom(column.toColumnAccessor().path())

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: ColumnSelector<*, *>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsFrom(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns from [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    // endregion

    // region allBefore

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver] and absolutely.
     */
    private interface AllBeforeDocs

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]` { myColumn `[in][String.contains]` it.`[name][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allBefore(column: ColumnFilter<C>): ColumnSet<C> =
        allBeforeInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allBefore(column: ColumnPath): ColumnSet<C> =
        allBeforeInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allBefore(column: String): ColumnSet<C> = allBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllBefore0")
    public fun <C> ColumnSet<C>.allBefore(column: AnyColumnReference): ColumnSet<C> = allBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allBefore(column: KProperty<*>): ColumnSet<C> =
        allBefore(column.toColumnAccessor().path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllBefore1")
    public fun <T> ColumnsSelectionDsl<T>.allBefore(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: String): ColumnSet<*> = allBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllBefore2")
    public fun ColumnsSelectionDsl<*>.allBefore(column: AnyColumnReference): ColumnSet<*> = allBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: KProperty<*>): ColumnSet<*> =
        allBefore(column.toColumnAccessor().path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: String): ColumnSet<*> = allColsBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        allColsBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        allColsBefore(column.toColumnAccessor().path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: String): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsBefore(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsBefore(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: String): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsBefore(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: String): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns before [column], excluding [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsBefore(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    // endregion

    // region allUpTo

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver] and absolutely.
     */
    private interface AllUpToDocs

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]` { myColumn `[in][String.contains]` it.`[name][ColumnWithPath.name]` } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allUpTo(column: ColumnFilter<C>): ColumnSet<C> =
        allUpToInternal(column as ColumnFilter<*>) as ColumnSet<C>

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allUpTo(column: ColumnPath): ColumnSet<C> =
        allUpToInternal { it.path == column } as ColumnSet<C>

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allUpTo(column: String): ColumnSet<C> = allUpTo(pathOf(column))

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllUpTo0")
    public fun <C> ColumnSet<C>.allUpTo(column: AnyColumnReference): ColumnSet<C> = allUpTo(column.path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun <C> ColumnSet<C>.allUpTo(column: KProperty<*>): ColumnSet<C> = allUpTo(column.toColumnAccessor().path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllUpTo1")
    public fun <T> ColumnsSelectionDsl<T>.allUpTo(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: ColumnPath): ColumnSet<*> = asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: String): ColumnSet<*> = asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Interpretable("AllUpTo2")
    public fun ColumnsSelectionDsl<*>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: KProperty<*>): ColumnSet<*> = asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
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
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: String): ColumnSet<*> = allColsUpTo(pathOf(column))

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        allColsUpTo(column.path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        allColsUpTo(column.toColumnAccessor().path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: ColumnSelector<*, *>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun String.allColsUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsUpTo(column: ColumnSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]` { myColumn } }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`("pathTo"["myColumn"]) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`("myColumn") }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`(myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset of columns from [this],
     * containing all columns up to [column], including [column] itself.
     *
     * [column] can be specified both relative to the current [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or the outer scope and
     * can be referenced using any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * If [column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: Using the `{}` overloads of these functions requires a [ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     * in the Plain DSL and on [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] it requires a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`(Type::myColumn) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [`all(Cols)`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [`all(Cols)Before`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [`all(Cols)After`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [`all(Cols)From`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [`all(Cols)UpTo`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [ColumnsSelectionDsl.allExcept]
     * @see [all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken. This column can be referenced
     *   to both relatively to the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] and absolutely.
     */
    public fun ColumnPath.allColsUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    // endregion
}

/**
 * If [this] is a [SingleColumn] containing a single [ColumnGroup]
 * (like [SingleColumn][SingleColumn]`<`[AnyRow][AnyRow]`>` or [ColumnsSelectionDsl][ColumnsSelectionDsl]), it
 * returns a [(transformable) ColumnSet][TransformableColumnSet] containing the children of this [ColumnGroup],
 * else it simply returns a [(transformable) ColumnSet][TransformableColumnSet] from [this]
 * (like when [this] is a [ColumnSet]).
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
