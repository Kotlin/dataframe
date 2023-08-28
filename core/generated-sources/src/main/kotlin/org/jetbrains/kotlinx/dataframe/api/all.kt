package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.BehaviorArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ColumnDoesNotExistArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.FunctionArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.FunctionColsArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.TitleArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage.After
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage.Before
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage.From
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage.UpTo
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.isSingleColumnWithGroup
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.performCheck
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_AFTER
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_AFTER_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_BEFORE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_BEFORE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_FROM
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_FROM_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_UP_TO
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_COLS_UP_TO_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_FROM
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_FROM_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_UP_TO
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_ALL_UP_TO_REPLACE
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
public fun <T> DataFrame<T>.all(predicate: RowFilter<T>): Boolean = rows().all { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

public interface AllColumnsSelectionDsl {

    /**
     * ## Usage of all Flavors of All (Cols):
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][KProperty]`<*>` | `[ColumnPath][ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]` | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][KProperty]`<*> | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colSelector: `[ColumnSelector][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the plain DSL:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**all**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**` [ `.[**recursively**][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`()` ` ]`
     *
     *  `|` **`all`**`(`[**Before**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[**After**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[**From**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[**UpTo**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`)` `(` **`(`**[column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef]**`)`** `|` **`{`** [colSelector][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSelectorDef] **`}`** `)`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**all**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**` [ `.[**recursively**][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`()` ` ]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .**`all`**`(`[**Before**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[**After**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[**From**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[**UpTo**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`)` `(` **`(`**[column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef]**`)`** `|` **`{`** [colSelector][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSelectorDef] **`}`** `)`
     *  TODO debate whether these overloads make sense. They didn't exist in 0.9.0
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;.[**allCols**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]**`()`**` [ `.[**recursively**][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`()` ` ]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .**`allCols`**`(`[**Before**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[**After**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[**From**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[**UpTo**][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`)` `(` **`(`**[column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef]**`)`** `|` **`{`** [colSelector][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnSelectorDef] **`}`** `)`
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
    public interface Usage {
        /** [**all**][ColumnsSelectionDsl.all] */
        public interface PlainDslName

        /** .[**all**][ColumnsSelectionDsl.all] */
        public interface ColumnSetName

        /** .[**allCols**][ColumnsSelectionDsl.allCols] */
        public interface ColumnGroupName

        /** [**Before**][ColumnsSelectionDsl.allColsBefore] */
        public interface Before

        /** [**After**][ColumnsSelectionDsl.allAfter] */
        public interface After

        /** [**From**][ColumnsSelectionDsl.allColsFrom] */
        public interface From

        /** [**UpTo**][ColumnsSelectionDsl.allColsUpTo] */
        public interface UpTo
    }

    /**
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][ColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][ColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][ColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][ColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][ColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     */
    private interface AllFlavors

    // region all

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet] that contains all columns from the current [ColumnsResolver].
     *
     * If the current [ColumnsResolver] is a [SingleColumn] and consists of only one [column group][ColumnGroup],
     * then `all` will create a new [ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][ColumnsSelectionDsl.cols].
     *
     * NOTE: For [column groups][ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * Check out [Usage] for how to use [all]/[allCols].
     *
     * #### For example:
     * `df.`[move][DataFrame.move]` { `[all][ColumnsSelectionDsl.all]`().`[recursively][ColumnsSelectionDsl.recursively]`() }.`[under][MoveClause.under]`("info")`
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[allCols][SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonAllDocs.Examples]}
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo\]
     * @see [ColumnsSelectionDsl.allBefore\]
     * @see [ColumnsSelectionDsl.allAfter\]
     * @see [ColumnsSelectionDsl.allFrom\]
     * @see [ColumnsSelectionDsl.allUpTo\]
     * @see [ColumnsSelectionDsl.cols\]
     */
    private interface CommonAllDocs {

        /** Example argument */
        interface Examples
    }

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage] for how to use [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]/[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "a" in `[name][ColumnWithPath.name]` }.`[all][ColumnSet.all]`() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: This is an identity call and can be omitted in most cases. However, it can still prove useful
     * for readability or in combination with [recursively][ColumnsSelectionDsl.recursively].
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.all(): TransformableColumnSet<C> =
        allColumnsInternal() as TransformableColumnSet<C>

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage] for how to use [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]/[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[all][ColumnsSelectionDsl.all]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnsSelectionDsl<*>.all(): TransformableColumnSet<*> =
        this.asSingleColumn().allColumnsInternal()

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage] for how to use [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]/[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[allCols][SingleColumn.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun SingleColumn<DataRow<*>>.allCols(): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().allColumnsInternal()

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage] for how to use [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]/[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[allCols][String.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.allCols(): TransformableColumnSet<*> = columnGroup(this).allCols()

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage] for how to use [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]/[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::columnGroup.`[allCols][KProperty.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun KProperty<*>.allCols(): TransformableColumnSet<*> = columnGroup(this).allCols()

    /**
     * ## All (Cols)
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `all` is named `allCols` instead to avoid confusion.
     *
     * Check out [Usage][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.Usage] for how to use [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]/[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[allCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroup"].`[allCols][ColumnPath.allCols]`() }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.allCols(): TransformableColumnSet<*> = columnGroup(this).allCols()

    // endregion

    /**
     * ## {@getArg [TitleArg]}
     *
     * Creates a new [ColumnSet] that contains a subset from the current [ColumnsResolver],
     * containing all columns {@getArg [BehaviorArg]}.
     *
     * If the current [ColumnsResolver] is a [SingleColumn] and consists of only one [column group][ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column\] does not exist, {@getArg [ColumnDoesNotExistArg]}.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[{@getArg [FunctionArg]}][ColumnsSelectionDsl.{@getArg [FunctionArg]}]`("someColumn") }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[{@getArg [FunctionColsArg]}][SingleColumn.{@getArg [FunctionColsArg]}]`(someColumn) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[{@getArg [FunctionArg]}][ColumnSet.{@getArg [FunctionArg]}]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg]}
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet] containing all columns {@getArg [BehaviorArg]}.
     * @see [allBefore\]
     * @see [allAfter\]
     * @see [allFrom\]
     * @see [allUpTo\]
     * @see [all\]
     * @see [cols\]
     */
    private interface CommonAllSubsetDocs {

        /* The title of the function, a.k.a. "All (Cols) After" */
        interface TitleArg

        /* The exact name of the function, a.k.a. "allAfter" */
        interface FunctionArg

        /* The exact name of the function, a.k.a. "allColsAfter" */
        interface FunctionColsArg

        /*
         * Small line of text explaining the behavior of the function,
         * a.k.a. "after [column\], excluding [column\]"
         */
        interface BehaviorArg

        /*
         * Small line of text explaining what happens if `column` does not exist.
         */
        interface ColumnDoesNotExistArg

        /* Example argument */
        interface ExampleArg
    }

    // region allAfter

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself
     * @param [column] The specified column after which all columns should be taken..
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself
     * @param [column] The specified column after which all columns should be taken..
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [all]
     * @see [cols]
     */
    private interface AllAfterDocs

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allAfter][ColumnSet.allAfter]`({@getArg [ColumnSetAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    private interface ColumnSetAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`({ myColumn }) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: ColumnSelector<*, *>): ColumnSet<C> {
        var resolvedColumn: DataColumn<C>? = null
        val set = performCheck {
            resolvedColumn = it.toColumnGroup("").getColumn(column) as DataColumn<C>
        }

        var take = false
        return set.colsInternal {
            if (take) {
                true
            } else {
                take = it.data == resolvedColumn!!
                false
            }
        } as ColumnSet<C>
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: ColumnPath): ColumnSet<C> {
        var take = false
        return colsInternal {
            if (take) {
                true
            } else {
                take = column == it.path
                false
            }
        } as ColumnSet<C>
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allAfter(column: String): ColumnSet<C> = allAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allAfter(column: AnyColumnReference): ColumnSet<C> = allAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allAfter(column: KProperty<*>): ColumnSet<C> =
        allAfter(column.toColumnAccessor().path())

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`({@getArg [ColumnsSelectionDslAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    private interface ColumnsSelectionDslAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`({ myColumn }) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun <T> ColumnsSelectionDsl<T>.allAfter(column: ColumnSelector<T, *>): ColumnSet<*> =
        this.asSingleColumn().allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: ColumnPath): ColumnSet<*> =
        this.asSingleColumn().allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: String): ColumnSet<*> = allAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: AnyColumnReference): ColumnSet<*> = allAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allAfter(column: KProperty<*>): ColumnSet<*> =
        allAfter(column.toColumnAccessor().path())

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsAfter][SingleColumn.allColsAfter]`({@getArg [SingleColumnAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    private interface SingleColumnAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`({ myColumn }) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun <T> SingleColumn<DataRow<T>>.allColsAfter(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        var take = false
        return ensureIsColumnGroup()
            .performCheck { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .colsInternal {
                if (take) {
                    true
                } else {
                    take = it.data == resolvedCol
                    false
                }
            }
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: ColumnPath): ColumnSet<*> {
        var path: ColumnPath? = null
        var take = false
        return ensureIsColumnGroup()
            .performCheck { path = it!!.path }
            .colsInternal {
                if (take) {
                    true
                } else {
                    // accept both relative and full column path
                    take = it.path == path!! + column || it.path == column
                    false
                }
            }
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: String): ColumnSet<*> = allColsAfter(pathOf(column))

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        allColsAfter(column.path())

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        allColsAfter(column.toColumnAccessor().path())

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsAfter][String.allColsAfter]`({@getArg [StringAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    private interface StringAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`({ myColumn }) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun String.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun String.allColsAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun String.allColsAfter(column: String): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun String.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsAfter][kotlin.String.allColsAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun String.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][KProperty.allColsAfter]`({@getArg [KPropertyAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    private interface KPropertyAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`({ myColumn }) }` 
     *
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun KProperty<*>.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun KProperty<*>.allColsAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun KProperty<*>.allColsAfter(column: String): ColumnSet<*> = columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun KProperty<*>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][kotlin.reflect.KProperty.allColsAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun KProperty<*>.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /**
     * ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][ColumnPath.allColsAfter]`({@getArg [ColumnPathAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    private interface ColumnPathAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`({ myColumn }) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnPath.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnPath.allColsAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnPath.allColsAfter(column: String): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnPath.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** ## All (Cols) After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsAfter][SingleColumn.allColsAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allAfter][ColumnSet.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns after [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column after which all columns should be taken.
     */
    public fun ColumnPath.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    // endregion

    // region allFrom

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself
     * @param [column] The specified column from which all columns should be taken..
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column] does not exist, the function will return an empty [ColumnSet][ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself
     * @param [column] The specified column from which all columns should be taken..
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [all]
     * @see [cols]
     */
    private interface AllFromDocs

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allFrom][ColumnSet.allFrom]`({@getArg [ColumnSetAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    private interface ColumnSetAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allFrom(column: ColumnPath): ColumnSet<C> {
        var take = false
        return colsInternal {
            if (take) {
                true
            } else {
                take = column == it.path
                take
            }
        } as ColumnSet<C>
    }

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allFrom(column: String): ColumnSet<C> = allFrom(pathOf(column))

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allFrom(column: AnyColumnReference): ColumnSet<C> = allFrom(column.path())

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allFrom(column: KProperty<*>): ColumnSet<C> =
        allFrom(column.toColumnAccessor().path())

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`({@getArg [ColumnsSelectionDslAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    private interface ColumnsSelectionDslAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: ColumnPath): ColumnSet<*> =
        this.asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: String): ColumnSet<*> =
        this.asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        this.asSingleColumn().allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allFrom(column: KProperty<*>): ColumnSet<*> =
        this.asSingleColumn().allColsFrom(column)

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsFrom][SingleColumn.allColsFrom]`({@getArg [SingleColumnAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    private interface SingleColumnAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: ColumnPath): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().allFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: String): ColumnSet<*> = allColsFrom(pathOf(column))

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        allColsFrom(column.path())

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        allColsFrom(column.toColumnAccessor().path())

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsFrom][String.allColsFrom]`({@getArg [StringAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    private interface StringAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun String.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun String.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun String.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsFrom][kotlin.String.allColsFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun String.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][KProperty.allColsFrom]`({@getArg [KPropertyAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    private interface KPropertyAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun KProperty<*>.allColsFrom(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun KProperty<*>.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun KProperty<*>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][kotlin.reflect.KProperty.allColsFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun KProperty<*>.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /**
     * ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][ColumnPath.allColsFrom]`({@getArg [ColumnPathAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    private interface ColumnPathAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnPath.allColsFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnPath.allColsFrom(column: String): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnPath.allColsFrom(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    /** ## All (Cols) From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return an empty [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsFrom][SingleColumn.allColsFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allFrom][ColumnSet.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column from which all columns should be taken.
     */
    public fun ColumnPath.allColsFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsFrom(column)

    // endregion

    // region allBefore

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself
     * @param [column] The specified column before which all columns should be taken.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column] does not exist, the function will return a [ColumnSet][ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself
     * @param [column] The specified column before which all columns should be taken.
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [all]
     * @see [cols]
     */
    private interface AllBeforeDocs

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allBefore][ColumnSet.allBefore]`({@getArg [ColumnSetAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    private interface ColumnSetAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allBefore(column: ColumnPath): ColumnSet<C> {
        var take = true
        return colsInternal {
            if (!take) {
                false
            } else {
                take = column != it.path
                take
            }
        } as ColumnSet<C>
    }

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun <C> ColumnSet<C>.allBefore(column: String): ColumnSet<C> = allBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun <C> ColumnSet<C>.allBefore(column: AnyColumnReference): ColumnSet<C> = allBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun <C> ColumnSet<C>.allBefore(column: KProperty<*>): ColumnSet<C> =
        allBefore(column.toColumnAccessor().path())

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`({@getArg [ColumnsSelectionDslAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    private interface ColumnsSelectionDslAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: String): ColumnSet<*> = allBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: AnyColumnReference): ColumnSet<*> = allBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnsSelectionDsl<*>.allBefore(column: KProperty<*>): ColumnSet<*> =
        allBefore(column.toColumnAccessor().path())

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsBefore][SingleColumn.allColsBefore]`({@getArg [SingleColumnAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    private interface SingleColumnAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: ColumnPath): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().allBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: String): ColumnSet<*> = allColsBefore(pathOf(column))

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        allColsBefore(column.path())

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        allColsBefore(column.toColumnAccessor().path())

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsBefore][String.allColsBefore]`({@getArg [StringAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    private interface StringAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun String.allColsBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun String.allColsBefore(column: String): ColumnSet<*> = columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun String.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsBefore][kotlin.String.allColsBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun String.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][KProperty.allColsBefore]`({@getArg [KPropertyAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    private interface KPropertyAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun KProperty<*>.allColsBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun KProperty<*>.allColsBefore(column: String): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun KProperty<*>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][kotlin.reflect.KProperty.allColsBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun KProperty<*>.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /**
     * ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][ColumnPath.allColsBefore]`({@getArg [ColumnPathAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    private interface ColumnPathAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnPath.allColsBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnPath.allColsBefore(column: String): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnPath.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** ## All (Cols) Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsBefore][SingleColumn.allColsBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allBefore][ColumnSet.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns before [column], excluding [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column before which all columns should be taken
     */
    public fun ColumnPath.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    // endregion

    // region allUpTo

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself
     * @param [column] The specified column up to which all columns should be taken..
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column] does not exist, the function will return a [ColumnSet][ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself
     * @param [column] The specified column up to which all columns should be taken..
     * @see [allBefore]
     * @see [allAfter]
     * @see [allFrom]
     * @see [allUpTo]
     * @see [all]
     * @see [cols]
     */
    private interface AllUpToDocs

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][ColumnSet.allUpTo]`({@getArg [ColumnSetAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    private interface ColumnSetAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allUpTo(column: ColumnPath): ColumnSet<C> {
        var take = true
        return colsInternal {
            if (!take) {
                false
            } else {
                take = column != it.path
                true
            }
        } as ColumnSet<C>
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allUpTo(column: String): ColumnSet<C> = allUpTo(pathOf(column))

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allUpTo(column: AnyColumnReference): ColumnSet<C> = allUpTo(column.path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun <C> ColumnSet<C>.allUpTo(column: KProperty<*>): ColumnSet<C> =
        allUpTo(column.toColumnAccessor().path())

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allColsUpTo]`({@getArg [ColumnsSelectionDslAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    private interface ColumnsSelectionDslAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: ColumnPath): ColumnSet<*> =
        this.asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: String): ColumnSet<*> =
        this.asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        this.asSingleColumn().allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: KProperty<*>): ColumnSet<*> =
        this.asSingleColumn().allColsUpTo(column)

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsUpTo][SingleColumn.allColsUpTo]`({@getArg [SingleColumnAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    private interface SingleColumnAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: ColumnPath): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().allUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: String): ColumnSet<*> = allColsUpTo(pathOf(column))

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        allColsUpTo(column.path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        allColsUpTo(column.toColumnAccessor().path())

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsUpTo][String.allColsUpTo]`({@getArg [StringAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    private interface StringAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun String.allColsUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun String.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun String.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allColsUpTo][kotlin.String.allColsUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun String.allColsUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][KProperty.allColsUpTo]`({@getArg [KPropertyAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    private interface KPropertyAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun KProperty<*>.allColsUpTo(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun KProperty<*>.allColsUpTo(column: String): ColumnSet<*> = columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun KProperty<*>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][kotlin.reflect.KProperty.allColsUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun KProperty<*>.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /**
     * ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][ColumnPath.allColsUpTo]`({@getArg [ColumnPathAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    private interface ColumnPathAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnPath.allColsUpTo(column: ColumnPath): ColumnSet<*> =
        allColsUpTo(column.toColumnAccessor().path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`("myColumn") }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnPath.allColsUpTo(column: String): ColumnSet<*> = allColsUpTo(pathOf(column))

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`(myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnPath.allColsUpTo(column: AnyColumnReference): ColumnSet<*> = allColsUpTo(column.path())

    /** ## All (Cols) Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * If [column][org.jetbrains.kotlinx.dataframe.api.column] does not exist, the function will return a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allColsUpTo][SingleColumn.allColsUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[allUpTo][ColumnSet.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allColsUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All (Cols):
     *
     * - [all(Cols)][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`:
     *     All columns
     *
     * - [all(Cols)Before][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [all(Cols)After][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [all(Cols)From][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [all(Cols)UpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @return A new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns up to [column], including [column] itself.
     * @see [allBefore][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allBefore]
     * @see [allAfter][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allAfter]
     * @see [allFrom][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allFrom]
     * @see [allUpTo][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allUpTo]
     * @see [all][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.all]
     * @see [cols]
     * @param [column] The specified column up to which all columns should be taken.
     */
    public fun ColumnPath.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        allColsUpTo(column.toColumnAccessor().path())

    // endregion

    // region deprecated

    @Deprecated(COL_SELECT_DSL_ALL_COLS, ReplaceWith(COL_SELECT_DSL_ALL_COLS_REPLACE))
    public fun SingleColumn<DataRow<*>>.all(): TransformableColumnSet<*> = allCols()

    @Deprecated(COL_SELECT_DSL_ALL_COLS, ReplaceWith(COL_SELECT_DSL_ALL_COLS_REPLACE))
    public fun String.all(): TransformableColumnSet<*> = allCols()

    @Deprecated(COL_SELECT_DSL_ALL_COLS_AFTER, ReplaceWith(COL_SELECT_DSL_ALL_COLS_AFTER_REPLACE))
    public fun SingleColumn<DataRow<*>>.allAfter(column: ColumnPath): ColumnSet<*> = allColsAfter(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_AFTER, ReplaceWith(COL_SELECT_DSL_ALL_COLS_AFTER_REPLACE))
    public fun SingleColumn<DataRow<*>>.allAfter(column: String): ColumnSet<*> = allColsAfter(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_AFTER, ReplaceWith(COL_SELECT_DSL_ALL_COLS_AFTER_REPLACE))
    public fun SingleColumn<DataRow<*>>.allAfter(column: AnyColumnReference): ColumnSet<*> = allColsAfter(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_BEFORE, ReplaceWith(COL_SELECT_DSL_ALL_COLS_BEFORE_REPLACE))
    public fun SingleColumn<DataRow<*>>.allBefore(column: ColumnPath): ColumnSet<*> = allColsBefore(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_BEFORE, ReplaceWith(COL_SELECT_DSL_ALL_COLS_BEFORE_REPLACE))
    public fun SingleColumn<DataRow<*>>.allBefore(column: String): ColumnSet<*> = allColsBefore(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_BEFORE, ReplaceWith(COL_SELECT_DSL_ALL_COLS_BEFORE_REPLACE))
    public fun SingleColumn<DataRow<*>>.allBefore(column: AnyColumnReference): ColumnSet<*> = allColsBefore(column)

    @Deprecated(COL_SELECT_DSL_ALL_FROM, ReplaceWith(COL_SELECT_DSL_ALL_FROM_REPLACE))
    public fun ColumnsSelectionDsl<*>.allSince(column: ColumnPath): ColumnSet<*> = allFrom(column)

    @Deprecated(COL_SELECT_DSL_ALL_FROM, ReplaceWith(COL_SELECT_DSL_ALL_FROM_REPLACE))
    public fun ColumnsSelectionDsl<*>.allSince(column: String): ColumnSet<*> = allFrom(column)

    @Deprecated(COL_SELECT_DSL_ALL_FROM, ReplaceWith(COL_SELECT_DSL_ALL_FROM_REPLACE))
    public fun ColumnsSelectionDsl<*>.allSince(column: AnyColumnReference): ColumnSet<*> = allFrom(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_FROM, ReplaceWith(COL_SELECT_DSL_ALL_COLS_FROM_REPLACE))
    public fun SingleColumn<DataRow<*>>.allSince(column: ColumnPath): ColumnSet<*> = allColsFrom(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_FROM, ReplaceWith(COL_SELECT_DSL_ALL_COLS_FROM_REPLACE))
    public fun SingleColumn<DataRow<*>>.allSince(column: String): ColumnSet<*> = allColsFrom(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_FROM, ReplaceWith(COL_SELECT_DSL_ALL_COLS_FROM_REPLACE))
    public fun SingleColumn<DataRow<*>>.allSince(column: AnyColumnReference): ColumnSet<*> = allColsFrom(column)

    @Deprecated(COL_SELECT_DSL_ALL_UP_TO, ReplaceWith(COL_SELECT_DSL_ALL_UP_TO_REPLACE))
    public fun ColumnsSelectionDsl<*>.allUntil(column: ColumnPath): ColumnSet<*> = allUpTo(column)

    @Deprecated(COL_SELECT_DSL_ALL_UP_TO, ReplaceWith(COL_SELECT_DSL_ALL_UP_TO_REPLACE))
    public fun ColumnsSelectionDsl<*>.allUntil(column: String): ColumnSet<*> = allUpTo(column)

    @Deprecated(COL_SELECT_DSL_ALL_UP_TO, ReplaceWith(COL_SELECT_DSL_ALL_UP_TO_REPLACE))
    public fun ColumnsSelectionDsl<*>.allUntil(column: AnyColumnReference): ColumnSet<*> = allUpTo(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_UP_TO, ReplaceWith(COL_SELECT_DSL_ALL_COLS_UP_TO_REPLACE))
    public fun SingleColumn<DataRow<*>>.allUntil(column: ColumnPath): ColumnSet<*> = allColsUpTo(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_UP_TO, ReplaceWith(COL_SELECT_DSL_ALL_COLS_UP_TO_REPLACE))
    public fun SingleColumn<DataRow<*>>.allUntil(column: String): ColumnSet<*> = allColsUpTo(column)

    @Deprecated(COL_SELECT_DSL_ALL_COLS_UP_TO, ReplaceWith(COL_SELECT_DSL_ALL_COLS_UP_TO_REPLACE))
    public fun SingleColumn<DataRow<*>>.allUntil(column: AnyColumnReference): ColumnSet<*> = allColsUpTo(column)

    // endregion
}

/**
 * If [this] is a [SingleColumn] containing a single [ColumnGroup], it
 * returns a [(transformable) ColumnSet][TransformableColumnSet] containing the children of this [ColumnGroup],
 * else it simply returns a [(transformable) ColumnSet][TransformableColumnSet] from [this].
 */
internal fun ColumnsResolver<*>.allColumnsInternal(): TransformableColumnSet<*> =
    transform {
        if (this.isSingleColumnWithGroup(it)) {
            it.single().children()
        } else {
            it
        }
    }

// endregion
