package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.BehaviorArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.FunctionArg
import org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.TitleArg
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
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.owner
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
     * #### Flavors of All:
     *
     * - [all][SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][SingleColumn.allUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     */
    private interface AllFlavors

    // region all

    /**
     * ## All
     *
     * Creates a new [ColumnSet] that contains all columns from the current [ColumnSet].
     *
     * If the current [ColumnSet] is a [SingleColumn] and consists of only one [column group][ColumnGroup],
     * then `all` will create a new [ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][ColumnsSelectionDsl.cols].
     *
     * #### For example:
     * `df.`[move][DataFrame.move]` { `[all][SingleColumn.all]`().`[recursively][ColumnsSelectionDsl.recursively]`() }.`[under][MoveClause.under]`("info")`
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[all][SingleColumn.all]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonAllDocs.Examples]}
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`() }`
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
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun <C> ColumnSet<C>.all(): TransformableColumnSet<C> = allColumnsInternal() as TransformableColumnSet<C>

    /**
     * ## All
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`() }`
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[all][SingleColumn.all]`() }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun SingleColumn<DataRow<*>>.all(): TransformableColumnSet<*> = this.ensureIsColumnGroup().allColumnsInternal()

    public fun ColumnsSelectionDsl<*>.all(): TransformableColumnSet<*> = this.asSingleColumn().allColumnsInternal()

    /**
     * ## All
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[all][String.all]`() }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun String.all(): TransformableColumnSet<*> = columnGroup(this).all()

    /**
     * ## All
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::columnGroup).`[all][SingleColumn.all]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::columnGroup.`[all][KProperty.all]`() }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun KProperty<DataRow<*>>.all(): TransformableColumnSet<*> = columnGroup(this).all()

    /**
     * ## All
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all columns from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `all` will create a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] consisting of its children.
     *
     * This makes the function equivalent to [cols()][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols].
     *
     * #### For example:
     * `df.`[move][org.jetbrains.kotlinx.dataframe.DataFrame.move]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }.`[under][org.jetbrains.kotlinx.dataframe.api.MoveClause.under]`("info")`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroup.`[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroup"].`[all][ColumnPath.all]`() }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
     *     All columns up to the specified column, including that column
     *
     * @see [ColumnsSelectionDsl.rangeTo]
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     * @see [ColumnsSelectionDsl.cols]
     */
    public fun ColumnPath.all(): TransformableColumnSet<*> = columnGroup(this).all()

    // endregion

    /**
     * ## {@getArg [TitleArg]}
     *
     * Creates a new [ColumnSet] that contains a subset from the current [ColumnSet],
     * containing all columns {@getArg [BehaviorArg]}.
     *
     * If the current [ColumnSet] is a [SingleColumn] and consists of only one [column group][ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[{@getArg [FunctionArg]}][SingleColumn.{@getArg [FunctionArg]}]`("someColumn") }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[{@getArg [FunctionArg]}][SingleColumn.{@getArg [FunctionArg]}]`(someColumn) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[{@getArg [FunctionArg]}][SingleColumn.{@getArg [FunctionArg]}]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg]}
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

        /** The title of the function, a.k.a "All After" */
        interface TitleArg

        /** The exact name of the function, a.k.a "allAfter" */
        interface FunctionArg

        /**
         * Small line of text explaining the behavior of the function,
         * a.k.a "after [column\], excluding [column\]"
         */
        interface BehaviorArg

        /** Example argument */
        interface ExampleArg
    }

    // region allAfter

    /**
     * ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself
     * @param [column] The specified column after which all columns should be taken..
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allAfter][ColumnSet.allAfter]`({@getArg [ColumnSetAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`({@getArg [SingleColumnAllAfterDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allAfter][SingleColumn.allAfter]`({@getArg [SingleColumnAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allAfter(column: ColumnPath): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allAfter(column: String): ColumnSet<*> = allAfter(pathOf(column))

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allAfter(column: AnyColumnReference): ColumnSet<*> = allAfter(column.path())

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allAfter(column: KProperty<*>): ColumnSet<*> =
        allAfter(column.toColumnAccessor().path())

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allAfter(column: ColumnPath): ColumnSet<*> =
        this.asColumnSet().allAfter(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allAfter(column: String): ColumnSet<*> = allAfter(pathOf(column))

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allAfter(column: AnyColumnReference): ColumnSet<*> = allAfter(column.path())

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allAfter(column: KProperty<*>): ColumnSet<*> =
        allAfter(column.toColumnAccessor().path())

    /**
     * ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allAfter][String.allAfter]`({@getArg [StringAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allAfter][kotlin.String.allAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allAfter][kotlin.String.allAfter]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allAfter(column: String): ColumnSet<*> = columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allAfter][kotlin.String.allAfter]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allAfter][kotlin.String.allAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allAfter(column)

    /**
     * ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::myColGroup).`[allAfter][SingleColumn.allAfter]`({@getArg [KPropertyAllAfterDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[allAfter][KProperty.allAfter]`({@getArg [KPropertyAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::myColGroup).`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allAfter][kotlin.reflect.KProperty.allAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::myColGroup).`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allAfter][kotlin.reflect.KProperty.allAfter]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allAfter(column: String): ColumnSet<*> = columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::myColGroup).`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allAfter][kotlin.reflect.KProperty.allAfter]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::myColGroup).`[allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[allAfter][kotlin.reflect.KProperty.allAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /**
     * ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allAfter][ColumnPath.allAfter]`({@getArg [ColumnPathAllAfterDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allAfter]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allAfter]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allAfter(column: String): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allAfter]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** ## All After
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns after [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allAfter][SingleColumn.allAfter]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allAfter][SingleColumn.allAfter]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allAfter][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allAfter]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    // endregion

    // region allFrom

    /**
     * ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself
     * @param [column] The specified column from which all columns should be taken..
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allFrom][ColumnSet.allFrom]`({@getArg [ColumnSetAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`({@getArg [SingleColumnAllFromDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allFrom][SingleColumn.allFrom]`({@getArg [SingleColumnAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allFrom(column: ColumnPath): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allFrom(column: String): ColumnSet<*> = allFrom(pathOf(column))

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allFrom(column: AnyColumnReference): ColumnSet<*> = allFrom(column.path())

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allFrom(column: KProperty<*>): ColumnSet<*> =
        allFrom(column.toColumnAccessor().path())

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allFrom(column: ColumnPath): ColumnSet<*> =
        this.asSingleColumn().allFrom(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allFrom(column: String): ColumnSet<*> =
        this.asSingleColumn().allFrom(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        this.asSingleColumn().allFrom(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allFrom(column: KProperty<*>): ColumnSet<*> =
        this.asSingleColumn().allFrom(column)

    /**
     * ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allFrom][String.allFrom]`({@getArg [StringAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allFrom][kotlin.String.allFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allFrom][kotlin.String.allFrom]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allFrom(column: String): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allFrom][kotlin.String.allFrom]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allFrom][kotlin.String.allFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allFrom(column)

    /**
     * ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allFrom][SingleColumn.allFrom]`({@getArg [KPropertyAllFromDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allFrom][KProperty.allFrom]`({@getArg [KPropertyAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allFrom][kotlin.reflect.KProperty.allFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allFrom(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allFrom][kotlin.reflect.KProperty.allFrom]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allFrom(column: String): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allFrom][kotlin.reflect.KProperty.allFrom]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allFrom][kotlin.reflect.KProperty.allFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allFrom(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /**
     * ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][ColumnPath.allFrom]`({@getArg [ColumnPathAllFromDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allFrom]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allFrom]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allFrom(column: String): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allFrom]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allFrom(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** ## All From
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns from [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allFrom][SingleColumn.allFrom]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allFrom][SingleColumn.allFrom]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allFrom]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allFrom(column)

    // endregion

    // region allBefore

    /**
     * ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself
     * @param [column] The specified column before which all columns should be taken.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allBefore][ColumnSet.allBefore]`({@getArg [ColumnSetAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`({@getArg [SingleColumnAllBeforeDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allBefore][SingleColumn.allBefore]`({@getArg [SingleColumnAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allBefore(column: ColumnPath): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allBefore(column: String): ColumnSet<*> = allBefore(pathOf(column))

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allBefore(column: AnyColumnReference): ColumnSet<*> = allBefore(column.path())

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allBefore(column: KProperty<*>): ColumnSet<*> =
        allBefore(column.toColumnAccessor().path())

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allBefore(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allBefore(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allBefore(column: String): ColumnSet<*> = allBefore(pathOf(column))

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allBefore(column: AnyColumnReference): ColumnSet<*> = allBefore(column.path())

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allBefore(column: KProperty<*>): ColumnSet<*> =
        allBefore(column.toColumnAccessor().path())

    /**
     * ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allBefore][String.allBefore]`({@getArg [StringAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allBefore][kotlin.String.allBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allBefore][kotlin.String.allBefore]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allBefore(column: String): ColumnSet<*> = columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allBefore][kotlin.String.allBefore]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allBefore][kotlin.String.allBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /**
     * ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allBefore][SingleColumn.allBefore]`({@getArg [KPropertyAllBeforeDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allBefore][KProperty.allBefore]`({@getArg [KPropertyAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allBefore][kotlin.reflect.KProperty.allBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allBefore][kotlin.reflect.KProperty.allBefore]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allBefore(column: String): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allBefore][kotlin.reflect.KProperty.allBefore]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allBefore][kotlin.reflect.KProperty.allBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /**
     * ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allBefore][ColumnPath.allBefore]`({@getArg [ColumnPathAllBeforeDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allBefore]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allBefore]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allBefore(column: String): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allBefore]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** ## All Before
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns before [column], excluding [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allBefore][SingleColumn.allBefore]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allBefore][SingleColumn.allBefore]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allBefore][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allBefore]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    // endregion

    // region allUpTo

    /**
     * ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself
     * @param [column] The specified column up to which all columns should be taken..
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.CommonAllSubsetDocs.ExampleArg]}
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][ColumnSet.allUpTo]`({@getArg [ColumnSetAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.allUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
     * ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`({@getArg [SingleColumnAllUpToDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allUpTo][SingleColumn.allUpTo]`({@getArg [SingleColumnAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allUpTo(column: ColumnPath): ColumnSet<*> =
        this.ensureIsColumnGroup().asColumnSet().allUpTo(column)

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allUpTo(column: String): ColumnSet<*> = allUpTo(pathOf(column))

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allUpTo(column: AnyColumnReference): ColumnSet<*> = allUpTo(column.path())

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { someColumnGroup.`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun SingleColumn<DataRow<*>>.allUpTo(column: KProperty<*>): ColumnSet<*> =
        allUpTo(column.toColumnAccessor().path())

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: ColumnPath): ColumnSet<*> =
        this.asSingleColumn().allUpTo(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: String): ColumnSet<*> =
        this.asSingleColumn().allUpTo(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        this.asSingleColumn().allUpTo(column)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: KProperty<*>): ColumnSet<*> =
        this.asSingleColumn().allUpTo(column)

    /**
     * ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allUpTo][String.allUpTo]`({@getArg [StringAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allUpTo][kotlin.String.allUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allUpTo][kotlin.String.allUpTo]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allUpTo(column: String): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allUpTo][kotlin.String.allUpTo]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someColGroup".`[allUpTo][kotlin.String.allUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun String.allUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /**
     * ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allUpTo][SingleColumn.allUpTo]`({@getArg [KPropertyAllUpToDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allUpTo][KProperty.allUpTo]`({@getArg [KPropertyAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`("pathTo"["myColumn"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allUpTo][kotlin.reflect.KProperty.allUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allUpTo(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`("myColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allUpTo][kotlin.reflect.KProperty.allUpTo]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allUpTo(column: String): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allUpTo][kotlin.reflect.KProperty.allUpTo]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(Type::myColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::someColGroup.`[allUpTo][kotlin.reflect.KProperty.allUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun KProperty<DataRow<*>>.allUpTo(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /**
     * ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allUpTo][ColumnPath.allUpTo]`({@getArg [ColumnPathAllUpToDocs.Arg]}) }`
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allUpTo]`("pathTo"["myColumn"]) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allUpTo(column: ColumnPath): ColumnSet<*> =
        allUpTo(column.toColumnAccessor().path())

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allUpTo]`("myColumn") }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allUpTo(column: String): ColumnSet<*> = allUpTo(pathOf(column))

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allUpTo]`(myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allUpTo(column: AnyColumnReference): ColumnSet<*> = allUpTo(column.path())

    /** ## All Up To
     *
     * Creates a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains a subset from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * containing all columns up to [column], including [column] itself.
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of only one [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`("someColumn") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[allUpTo][SingleColumn.allUpTo]`(someColumn) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[allUpTo][SingleColumn.allUpTo]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["someColGroup"].`[allUpTo][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.allUpTo]`(Type::myColumn) }` 
     *
     * #### Flavors of All:
     *
     * - [all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`:
     *     All columns
     *
     * - [allBefore][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allBefore]`(column)`:
     *     All columns before the specified column, excluding that column
     *
     * - [allAfter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allAfter]`(column)`:
     *     All columns after the specified column, excluding that column
     *
     * - [allFrom][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allFrom]`(column)`:
     *     All columns from the specified column, including that column
     *
     * - [allUpTo][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allUpTo]`(column)`:
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
    public fun ColumnPath.allUpTo(column: KProperty<*>): ColumnSet<*> = allUpTo(column.toColumnAccessor().path())

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
