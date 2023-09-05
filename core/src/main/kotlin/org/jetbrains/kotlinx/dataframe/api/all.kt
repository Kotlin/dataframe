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
import org.jetbrains.kotlinx.dataframe.columns.isSingleColumnWithGroup
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.onResolve
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

public interface AllColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {

    /**
     * ## Usage of all Flavors of All (Cols):
     *
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnSelectorDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}**`()`**` [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  `|` **`all`**`(`{@include [Before]}`|`{@include [After]}`|`{@include [From]}`|`{@include [UpTo]}`)` `(` **`(`**{@include [UsageTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [UsageTemplate.ColumnSelectorRef]} **`\\}`** `)`
     * }
     *
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}**`()`**` [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` .**`all`**`(`{@include [Before]}`|`{@include [After]}`|`{@include [From]}`|`{@include [UpTo]}`)` `(` **`(`**{@include [UsageTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [UsageTemplate.ColumnSelectorRef]} **`\\}`** `)`
     *  TODO debate whether these overloads make sense. They didn't exist in 0.9.0
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`()`**` [ `{@include [AtAnyDepthColumnsSelectionDsl.Usage.Name]} ` ]`
     *
     *  {@include [Indent]}`|` .**`allCols`**`(`{@include [Before]}`|`{@include [After]}`|`{@include [From]}`|`{@include [UpTo]}`)` `(` **`(`**{@include [UsageTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [UsageTemplate.ColumnSelectorRef]} **`\\}`** `)`
     * }
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
     * `df.`[move][DataFrame.move]` { `[all][ColumnsSelectionDsl.all]`().`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }.`[under][MoveClause.under]`("info")`
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[allCols][SingleColumn.allCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonAllDocs.Examples]}
     *
     * {@include [AllFlavors]}
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
     * @include [CommonAllDocs]
     * @setArg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "a" in `[name][ColumnWithPath.name]` }.`[all][ColumnSet.all]`() }`
     * {@include [LineBreak]}
     * NOTE: This is an identity call and can be omitted in most cases. However, it can still prove useful
     * for readability or in combination with [atAnyDepth][ColumnsSelectionDsl.atAnyDepth].
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.all(): TransformableColumnSet<C> =
        allColumnsInternal() as TransformableColumnSet<C>

    /**
     * @include [CommonAllDocs]
     * @setArg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[all][ColumnsSelectionDsl.all]`() }`
     */
    public fun ColumnsSelectionDsl<*>.all(): TransformableColumnSet<*> =
        asSingleColumn().allColumnsInternal()

    /**
     * @include [CommonAllDocs]
     * @setArg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[allCols][SingleColumn.allCols]`() }`
     */
    public fun SingleColumn<DataRow<*>>.allCols(): TransformableColumnSet<*> =
        ensureIsColumnGroup().allColumnsInternal()

    /**
     * @include [CommonAllDocs]
     * @setArg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[allCols][String.allCols]`() }`
     */
    public fun String.allCols(): TransformableColumnSet<*> =
        columnGroup(this).allCols()

    /**
     * @include [CommonAllDocs]
     * @setArg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::columnGroup.`[allCols][KProperty.allCols]`() }`
     */
    public fun KProperty<*>.allCols(): TransformableColumnSet<*> =
        columnGroup(this).allCols()

    /**
     * @include [CommonAllDocs]
     * @setArg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroup"].`[allCols][ColumnPath.allCols]`() }`
     */
    public fun ColumnPath.allCols(): TransformableColumnSet<*> =
        columnGroup(this).allCols()

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
     * {@include [AllFlavors]}
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
     * @include [CommonAllSubsetDocs]
     * @setArg [CommonAllSubsetDocs.TitleArg] All (Cols) After
     * @setArg [CommonAllSubsetDocs.FunctionArg] allAfter
     * @setArg [CommonAllSubsetDocs.FunctionColsArg] allColsAfter
     * @setArg [CommonAllSubsetDocs.BehaviorArg] after [column\], excluding [column\] itself
     * @setArg [CommonAllSubsetDocs.ColumnDoesNotExistArg] the function will return an empty [ColumnSet][ColumnSet]
     * @param [column\] The specified column after which all columns should be taken.
     */
    private interface AllAfterDocs

    /**
     * @include [AllAfterDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allAfter][ColumnSet.allAfter]`{@getArg [ColumnSetAllAfterDocs.Arg]} }`
     */
    private interface ColumnSetAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllAfterDocs] {@setArg [ColumnSetAllAfterDocs.Arg] \\ \{ myColumn \\\}} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: ColumnSelector<*, *>): ColumnSet<C> {
        var resolvedColumn: DataColumn<C>? = null
        return this
            .onResolve { resolvedColumn = it.toColumnGroup("").getColumn(column) as DataColumn<C> }
            .allAfterInternal { it.data == resolvedColumn!! } as ColumnSet<C>
    }

    /** @include [ColumnSetAllAfterDocs] {@setArg [ColumnSetAllAfterDocs.Arg] ("pathTo"["myColumn"])} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allAfter(column: ColumnPath): ColumnSet<C> =
        allAfterInternal { it.path == column } as ColumnSet<C>

    /** @include [ColumnSetAllAfterDocs] {@setArg [ColumnSetAllAfterDocs.Arg] ("myColumn")} */
    public fun <C> ColumnSet<C>.allAfter(column: String): ColumnSet<C> =
        allAfter(pathOf(column))

    /** @include [ColumnSetAllAfterDocs] {@setArg [ColumnSetAllAfterDocs.Arg] (myColumn)} */
    public fun <C> ColumnSet<C>.allAfter(column: AnyColumnReference): ColumnSet<C> =
        allAfter(column.path())

    /** @include [ColumnSetAllAfterDocs] {@setArg [ColumnSetAllAfterDocs.Arg] (Type::myColumn)} */
    public fun <C> ColumnSet<C>.allAfter(column: KProperty<*>): ColumnSet<C> =
        allAfter(column.toColumnAccessor().path())

    /**
     * @include [AllAfterDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allAfter][ColumnsSelectionDsl.allAfter]`{@getArg [ColumnsSelectionDslAllAfterDocs.Arg]} }`
     */
    private interface ColumnsSelectionDslAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnsSelectionDslAllAfterDocs] {@setArg [ColumnsSelectionDslAllAfterDocs.Arg] \{ myColumn \\}} */
    public fun <T> ColumnsSelectionDsl<T>.allAfter(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsAfter(column)

    /** @include [ColumnsSelectionDslAllAfterDocs] {@setArg [ColumnsSelectionDslAllAfterDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnsSelectionDsl<*>.allAfter(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsAfter(column)

    /** @include [ColumnsSelectionDslAllAfterDocs] {@setArg [ColumnsSelectionDslAllAfterDocs.Arg] ("myColumn")} */
    public fun ColumnsSelectionDsl<*>.allAfter(column: String): ColumnSet<*> =
        allAfter(pathOf(column))

    /** @include [ColumnsSelectionDslAllAfterDocs] {@setArg [ColumnsSelectionDslAllAfterDocs.Arg] (myColumn)} */
    public fun ColumnsSelectionDsl<*>.allAfter(column: AnyColumnReference): ColumnSet<*> =
        allAfter(column.path())

    /** @include [ColumnsSelectionDslAllAfterDocs] {@setArg [ColumnsSelectionDslAllAfterDocs.Arg] (Type::myColumn)} */
    public fun ColumnsSelectionDsl<*>.allAfter(column: KProperty<*>): ColumnSet<*> =
        allAfter(column.toColumnAccessor().path())

    /**
     * @include [AllAfterDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsAfter][SingleColumn.allColsAfter]`{@getArg [SingleColumnAllAfterDocs.Arg]} }`
     */
    private interface SingleColumnAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllAfterDocs] {@setArg [SingleColumnAllAfterDocs.Arg] \{ myColumn \\}} */
    public fun <T> SingleColumn<DataRow<T>>.allColsAfter(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allAfterInternal { it.data == resolvedCol!! }
    }

    /** @include [SingleColumnAllAfterDocs] {@setArg [SingleColumnAllAfterDocs.Arg] ("pathTo"["myColumn"])} */
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

    /** @include [SingleColumnAllAfterDocs] {@setArg [SingleColumnAllAfterDocs.Arg] ("myColumn")} */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: String): ColumnSet<*> =
        allColsAfter(pathOf(column))

    /** @include [SingleColumnAllAfterDocs] {@setArg [SingleColumnAllAfterDocs.Arg] (myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        allColsAfter(column.path())

    /** @include [SingleColumnAllAfterDocs] {@setArg [SingleColumnAllAfterDocs.Arg] (Type::myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        allColsAfter(column.toColumnAccessor().path())

    /**
     * @include [AllAfterDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsAfter][String.allColsAfter]`{@getArg [StringAllAfterDocs.Arg]} }`
     */
    private interface StringAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllAfterDocs] {@setArg [StringAllAfterDocs.Arg] \\ \{ myColumn \\\}} */
    public fun String.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [StringAllAfterDocs] {@setArg [StringAllAfterDocs.Arg] ("pathTo"["myColumn"])} */
    public fun String.allColsAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [StringAllAfterDocs] {@setArg [StringAllAfterDocs.Arg] ("myColumn")} */
    public fun String.allColsAfter(column: String): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [StringAllAfterDocs] {@setArg [StringAllAfterDocs.Arg] (myColumn)} */
    public fun String.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [StringAllAfterDocs] {@setArg [StringAllAfterDocs.Arg] (Type::myColumn)} */
    public fun String.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /**
     * @include [AllAfterDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[allColsAfter][KProperty.allColsAfter]`{@getArg [KPropertyAllAfterDocs.Arg]} }`
     */
    private interface KPropertyAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /**
     * @include [KPropertyAllAfterDocs] {@setArg [KPropertyAllAfterDocs.Arg] \\ \{ myColumn \\\}}
     * {@comment We cannot provide types here because we'd need KProperty<T> and KProperty<DataRow<T>>, which provides
     * ambiguity as receiver.}
     */
    public fun KProperty<*>.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [KPropertyAllAfterDocs] {@setArg [KPropertyAllAfterDocs.Arg] ("pathTo"["myColumn"])} */
    public fun KProperty<*>.allColsAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [KPropertyAllAfterDocs] {@setArg [KPropertyAllAfterDocs.Arg] ("myColumn")} */
    public fun KProperty<*>.allColsAfter(column: String): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [KPropertyAllAfterDocs] {@setArg [KPropertyAllAfterDocs.Arg] (myColumn)} */
    public fun KProperty<*>.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [KPropertyAllAfterDocs] {@setArg [KPropertyAllAfterDocs.Arg] (Type::myColumn)} */
    public fun KProperty<*>.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /**
     * @include [AllAfterDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allColsAfter][ColumnPath.allColsAfter]`{@getArg [ColumnPathAllAfterDocs.Arg]} }`
     */
    private interface ColumnPathAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllAfterDocs] {@setArg [ColumnPathAllAfterDocs.Arg] \\ \{ myColumn \\\}} */
    public fun ColumnPath.allColsAfter(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@setArg [ColumnPathAllAfterDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnPath.allColsAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@setArg [ColumnPathAllAfterDocs.Arg] ("myColumn")} */
    public fun ColumnPath.allColsAfter(column: String): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@setArg [ColumnPathAllAfterDocs.Arg] (myColumn)} */
    public fun ColumnPath.allColsAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@setArg [ColumnPathAllAfterDocs.Arg] (Type::myColumn)} */
    public fun ColumnPath.allColsAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsAfter(column)

    // endregion

    // region allFrom

    /**
     * @include [CommonAllSubsetDocs]
     * @setArg [CommonAllSubsetDocs.TitleArg] All (Cols) From
     * @setArg [CommonAllSubsetDocs.FunctionArg] allFrom
     * @setArg [CommonAllSubsetDocs.FunctionColsArg] allColsFrom
     * @setArg [CommonAllSubsetDocs.BehaviorArg] from [column\], including [column\] itself
     * @setArg [CommonAllSubsetDocs.ColumnDoesNotExistArg] the function will return an empty [ColumnSet][ColumnSet]
     * @param [column\] The specified column from which all columns should be taken.
     */
    private interface AllFromDocs

    /**
     * @include [AllFromDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allFrom][ColumnSet.allFrom]`{@getArg [ColumnSetAllFromDocs.Arg]} }`
     */
    private interface ColumnSetAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllFromDocs] {@setArg [ColumnSetAllFromDocs.Arg] \{ myColumns \\\}} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allFrom(column: ColumnSelector<*, *>): ColumnSet<C> {
        var resolvedColumn: DataColumn<C>? = null
        return this
            .onResolve { resolvedColumn = it.toColumnGroup("").getColumn(column) as DataColumn<C> }
            .allFromInternal { it.data == resolvedColumn!! } as ColumnSet<C>
    }

    /** @include [ColumnSetAllFromDocs] {@setArg [ColumnSetAllFromDocs.Arg] ("pathTo"["myColumn"])} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allFrom(column: ColumnPath): ColumnSet<C> =
        allFromInternal { it.path == column } as ColumnSet<C>

    /** @include [ColumnSetAllFromDocs] {@setArg [ColumnSetAllFromDocs.Arg] ("myColumn")} */
    public fun <C> ColumnSet<C>.allFrom(column: String): ColumnSet<C> =
        allFrom(pathOf(column))

    /** @include [ColumnSetAllFromDocs] {@setArg [ColumnSetAllFromDocs.Arg] (myColumn)} */
    public fun <C> ColumnSet<C>.allFrom(column: AnyColumnReference): ColumnSet<C> =
        allFrom(column.path())

    /** @include [ColumnSetAllFromDocs] {@setArg [ColumnSetAllFromDocs.Arg] (Type::myColumn)} */
    public fun <C> ColumnSet<C>.allFrom(column: KProperty<*>): ColumnSet<C> =
        allFrom(column.toColumnAccessor().path())

    /**
     * @include [AllFromDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allFrom][ColumnsSelectionDsl.allFrom]`{@getArg [ColumnsSelectionDslAllFromDocs.Arg]} }`
     */
    private interface ColumnsSelectionDslAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnsSelectionDslAllFromDocs] {@setArg [ColumnsSelectionDslAllFromDocs.Arg] \\ \{ myColumn \\\}} */
    public fun <T> ColumnsSelectionDsl<T>.allFrom(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** @include [ColumnsSelectionDslAllFromDocs] {@setArg [ColumnsSelectionDslAllFromDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnsSelectionDsl<*>.allFrom(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** @include [ColumnsSelectionDslAllFromDocs] {@setArg [ColumnsSelectionDslAllFromDocs.Arg] ("myColumn")} */
    public fun ColumnsSelectionDsl<*>.allFrom(column: String): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** @include [ColumnsSelectionDslAllFromDocs] {@setArg [ColumnsSelectionDslAllFromDocs.Arg] (myColumn)} */
    public fun ColumnsSelectionDsl<*>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /** @include [ColumnsSelectionDslAllFromDocs] {@setArg [ColumnsSelectionDslAllFromDocs.Arg] (Type::myColumn)} */
    public fun ColumnsSelectionDsl<*>.allFrom(column: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsFrom(column)

    /**
     * @include [AllFromDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsFrom][SingleColumn.allColsFrom]`{@getArg [SingleColumnAllFromDocs.Arg]} }`
     */
    private interface SingleColumnAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllFromDocs] {@setArg [SingleColumnAllFromDocs.Arg] \\ \{ myColumn \\\}} */
    public fun <T> SingleColumn<DataRow<T>>.allColsFrom(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allFromInternal { it.data == resolvedCol!! }
    }

    /** @include [SingleColumnAllFromDocs] {@setArg [SingleColumnAllFromDocs.Arg] ("pathTo"["myColumn"])} */
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

    /** @include [SingleColumnAllFromDocs] {@setArg [SingleColumnAllFromDocs.Arg] ("myColumn")} */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: String): ColumnSet<*> =
        allColsFrom(pathOf(column))

    /** @include [SingleColumnAllFromDocs] {@setArg [SingleColumnAllFromDocs.Arg] (myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        allColsFrom(column.path())

    /** @include [SingleColumnAllFromDocs] {@setArg [SingleColumnAllFromDocs.Arg] (Type::myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        allColsFrom(column.toColumnAccessor().path())

    /**
     * @include [AllFromDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsFrom][String.allColsFrom]`{@getArg [StringAllFromDocs.Arg]} }`
     */
    private interface StringAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllFromDocs] {@setArg [StringAllFromDocs.Arg] \\ \{ myColumn \\\}} */
    public fun String.allColsFrom(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [StringAllFromDocs] {@setArg [StringAllFromDocs.Arg] ("pathTo"["myColumn"])} */
    public fun String.allColsFrom(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [StringAllFromDocs] {@setArg [StringAllFromDocs.Arg] ("myColumn")} */
    public fun String.allColsFrom(column: String): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [StringAllFromDocs] {@setArg [StringAllFromDocs.Arg] (myColumn)} */
    public fun String.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [StringAllFromDocs] {@setArg [StringAllFromDocs.Arg] (Type::myColumn)} */
    public fun String.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /**
     * @include [AllFromDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allColsFrom][KProperty.allColsFrom]`{@getArg [KPropertyAllFromDocs.Arg]} }`
     */
    private interface KPropertyAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /**
     * @include [KPropertyAllFromDocs] {@setArg [KPropertyAllFromDocs.Arg] \\ \{ myColumn \\\}}
     * {@comment We cannot provide types here because we'd need KProperty<T> and KProperty<DataRow<T>>, which provides
     * ambiguity as receiver.}
     */
    public fun KProperty<*>.allColsFrom(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [KPropertyAllFromDocs] {@setArg [KPropertyAllFromDocs.Arg] ("pathTo"["myColumn"])} */
    public fun KProperty<*>.allColsFrom(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [KPropertyAllFromDocs] {@setArg [KPropertyAllFromDocs.Arg] ("myColumn")} */
    public fun KProperty<*>.allColsFrom(column: String): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [KPropertyAllFromDocs] {@setArg [KPropertyAllFromDocs.Arg] (myColumn)} */
    public fun KProperty<*>.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [KPropertyAllFromDocs] {@setArg [KPropertyAllFromDocs.Arg] (Type::myColumn)} */
    public fun KProperty<*>.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /**
     * @include [AllFromDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][ColumnPath.allColsFrom]`{@getArg [ColumnPathAllFromDocs.Arg]} }`
     */
    private interface ColumnPathAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllFromDocs] {@setArg [ColumnPathAllFromDocs.Arg] \\ \{ myColumn \\\}} */
    public fun ColumnPath.allColsFrom(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [ColumnPathAllFromDocs] {@setArg [ColumnPathAllFromDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnPath.allColsFrom(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [ColumnPathAllFromDocs] {@setArg [ColumnPathAllFromDocs.Arg] ("myColumn")} */
    public fun ColumnPath.allColsFrom(column: String): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [ColumnPathAllFromDocs] {@setArg [ColumnPathAllFromDocs.Arg] (myColumn)} */
    public fun ColumnPath.allColsFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    /** @include [ColumnPathAllFromDocs] {@setArg [ColumnPathAllFromDocs.Arg] (Type::myColumn)} */
    public fun ColumnPath.allColsFrom(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsFrom(column)

    // endregion

    // region allBefore

    /**
     * @include [CommonAllSubsetDocs]
     * @setArg [CommonAllSubsetDocs.TitleArg] All (Cols) Before
     * @setArg [CommonAllSubsetDocs.FunctionArg] allBefore
     * @setArg [CommonAllSubsetDocs.FunctionColsArg] allColsBefore
     * @setArg [CommonAllSubsetDocs.BehaviorArg] before [column\], excluding [column\] itself
     * @setArg [CommonAllSubsetDocs.ColumnDoesNotExistArg] the function will return a [ColumnSet][ColumnSet] containing all columns
     * @param [column\] The specified column before which all columns should be taken
     */
    private interface AllBeforeDocs

    /**
     * @include [AllBeforeDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allBefore][ColumnSet.allBefore]`{@getArg [ColumnSetAllBeforeDocs.Arg]} }`
     */
    private interface ColumnSetAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllBeforeDocs] {@setArg [ColumnSetAllBeforeDocs.Arg] \\ \{ myColumn \\\}} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allBefore(column: ColumnSelector<*, *>): ColumnSet<C> {
        var resolvedColumn: DataColumn<C>? = null
        return this
            .onResolve { resolvedColumn = it.toColumnGroup("").getColumn(column) as DataColumn<C> }
            .allBeforeInternal { it.data == resolvedColumn!! } as ColumnSet<C>
    }

    /** @include [ColumnSetAllBeforeDocs] {@setArg [ColumnSetAllBeforeDocs.Arg] ("pathTo"["myColumn"])} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allBefore(column: ColumnPath): ColumnSet<C> =
        allBeforeInternal { it.path == column } as ColumnSet<C>

    /** @include [ColumnSetAllBeforeDocs] {@setArg [ColumnSetAllBeforeDocs.Arg] ("myColumn")} */
    public fun <C> ColumnSet<C>.allBefore(column: String): ColumnSet<C> =
        allBefore(pathOf(column))

    /** @include [ColumnSetAllBeforeDocs] {@setArg [ColumnSetAllBeforeDocs.Arg] (myColumn)} */
    public fun <C> ColumnSet<C>.allBefore(column: AnyColumnReference): ColumnSet<C> =
        allBefore(column.path())

    /** @include [ColumnSetAllBeforeDocs] {@setArg [ColumnSetAllBeforeDocs.Arg] (Type::myColumn)} */
    public fun <C> ColumnSet<C>.allBefore(column: KProperty<*>): ColumnSet<C> =
        allBefore(column.toColumnAccessor().path())

    /**
     * @include [AllBeforeDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allBefore][ColumnsSelectionDsl.allBefore]`{@getArg [ColumnsSelectionDslAllBeforeDocs.Arg]} }`
     */
    private interface ColumnsSelectionDslAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnsSelectionDslAllBeforeDocs] {@setArg [ColumnsSelectionDslAllBeforeDocs.Arg] \\ \{ myColumn \\\}} */
    public fun <T> ColumnsSelectionDsl<T>.allBefore(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsBefore(column)

    /** @include [ColumnsSelectionDslAllBeforeDocs] {@setArg [ColumnsSelectionDslAllBeforeDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnsSelectionDsl<*>.allBefore(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsBefore(column)

    /** @include [ColumnsSelectionDslAllBeforeDocs] {@setArg [ColumnsSelectionDslAllBeforeDocs.Arg] ("myColumn")} */
    public fun ColumnsSelectionDsl<*>.allBefore(column: String): ColumnSet<*> =
        allBefore(pathOf(column))

    /** @include [ColumnsSelectionDslAllBeforeDocs] {@setArg [ColumnsSelectionDslAllBeforeDocs.Arg] (myColumn)} */
    public fun ColumnsSelectionDsl<*>.allBefore(column: AnyColumnReference): ColumnSet<*> =
        allBefore(column.path())

    /** @include [ColumnsSelectionDslAllBeforeDocs] {@setArg [ColumnsSelectionDslAllBeforeDocs.Arg] (Type::myColumn)} */
    public fun ColumnsSelectionDsl<*>.allBefore(column: KProperty<*>): ColumnSet<*> =
        allBefore(column.toColumnAccessor().path())

    /**
     * @include [AllBeforeDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsBefore][SingleColumn.allColsBefore]`{@getArg [SingleColumnAllBeforeDocs.Arg]} }`
     */
    private interface SingleColumnAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllBeforeDocs] {@setArg [SingleColumnAllBeforeDocs.Arg] \\ \{ myColumn \\\}} */
    public fun <T> SingleColumn<DataRow<T>>.allColsBefore(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allBeforeInternal { it.data == resolvedCol }
    }

    /** @include [SingleColumnAllBeforeDocs] {@setArg [SingleColumnAllBeforeDocs.Arg] ("pathTo"["myColumn"])} */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: ColumnPath): ColumnSet<*> {
        var path: ColumnPath? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { path = it!!.path }
            .allBeforeInternal { it.path == path!! + column || it.path == column }
    }

    /** @include [SingleColumnAllBeforeDocs] {@setArg [SingleColumnAllBeforeDocs.Arg] ("myColumn")} */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: String): ColumnSet<*> =
        allColsBefore(pathOf(column))

    /** @include [SingleColumnAllBeforeDocs] {@setArg [SingleColumnAllBeforeDocs.Arg] (myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        allColsBefore(column.path())

    /** @include [SingleColumnAllBeforeDocs] {@setArg [SingleColumnAllBeforeDocs.Arg] (Type::myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        allColsBefore(column.toColumnAccessor().path())

    /**
     * @include [AllBeforeDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsBefore][String.allColsBefore]`{@getArg [StringAllBeforeDocs.Arg]} }`
     */
    private interface StringAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllBeforeDocs] {@setArg [StringAllBeforeDocs.Arg] \\ \{ myColumn \\\}} */
    public fun String.allColsBefore(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [StringAllBeforeDocs] {@setArg [StringAllBeforeDocs.Arg] ("pathTo"["myColumn"])} */
    public fun String.allColsBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [StringAllBeforeDocs] {@setArg [StringAllBeforeDocs.Arg] ("myColumn")} */
    public fun String.allColsBefore(column: String): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [StringAllBeforeDocs] {@setArg [StringAllBeforeDocs.Arg] (myColumn)} */
    public fun String.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [StringAllBeforeDocs] {@setArg [StringAllBeforeDocs.Arg] (Type::myColumn)} */
    public fun String.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /**
     * @include [AllBeforeDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allColsBefore][KProperty.allColsBefore]`{@getArg [KPropertyAllBeforeDocs.Arg]} }`
     */
    private interface KPropertyAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /**
     * @include [KPropertyAllBeforeDocs] {@setArg [KPropertyAllBeforeDocs.Arg] \\ \{ myColumn \\\}}
     * {@comment We cannot provide types here because we'd need KProperty<T> and KProperty<DataRow<T>>, which provides
     * ambiguity as receiver.}
     */
    public fun KProperty<*>.allColsBefore(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@setArg [KPropertyAllBeforeDocs.Arg] ("pathTo"["myColumn"])} */
    public fun KProperty<*>.allColsBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@setArg [KPropertyAllBeforeDocs.Arg] ("myColumn")} */
    public fun KProperty<*>.allColsBefore(column: String): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@setArg [KPropertyAllBeforeDocs.Arg] (myColumn)} */
    public fun KProperty<*>.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@setArg [KPropertyAllBeforeDocs.Arg] (Type::myColumn)} */
    public fun KProperty<*>.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /**
     * @include [AllBeforeDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allColsBefore][ColumnPath.allColsBefore]`{@getArg [ColumnPathAllBeforeDocs.Arg]} }`
     */
    private interface ColumnPathAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllBeforeDocs] {@setArg [ColumnPathAllBeforeDocs.Arg] \\ \{ myColumn \\\}} */
    public fun ColumnPath.allColsBefore(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@setArg [ColumnPathAllBeforeDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnPath.allColsBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@setArg [ColumnPathAllBeforeDocs.Arg] ("myColumn")} */
    public fun ColumnPath.allColsBefore(column: String): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@setArg [ColumnPathAllBeforeDocs.Arg] (myColumn)} */
    public fun ColumnPath.allColsBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@setArg [ColumnPathAllBeforeDocs.Arg] (Type::myColumn)} */
    public fun ColumnPath.allColsBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsBefore(column)

    // endregion

    // region allUpTo

    /**
     * @include [CommonAllSubsetDocs]
     * @setArg [CommonAllSubsetDocs.TitleArg] All (Cols) Up To
     * @setArg [CommonAllSubsetDocs.FunctionArg] allUpTo
     * @setArg [CommonAllSubsetDocs.FunctionColsArg] allColsUpTo
     * @setArg [CommonAllSubsetDocs.BehaviorArg] up to [column\], including [column\] itself
     * @setArg [CommonAllSubsetDocs.ColumnDoesNotExistArg] the function will return a [ColumnSet][ColumnSet] containing all columns
     * @param [column\] The specified column up to which all columns should be taken.
     */
    private interface AllUpToDocs

    /**
     * @include [AllUpToDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][ColumnSet.allUpTo]`{@getArg [ColumnSetAllUpToDocs.Arg]} }`
     */
    private interface ColumnSetAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllUpToDocs] {@setArg [ColumnSetAllUpToDocs.Arg] \\ \{ myColumn \\\}} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allUpTo(column: ColumnSelector<*, *>): ColumnSet<C> {
        var resolvedColumn: DataColumn<C>? = null
        return this
            .onResolve { resolvedColumn = it.toColumnGroup("").getColumn(column) as DataColumn<C> }
            .allUpToInternal { it.data == resolvedColumn!! } as ColumnSet<C>
    }

    /** @include [ColumnSetAllUpToDocs] {@setArg [ColumnSetAllUpToDocs.Arg] ("pathTo"["myColumn"])} */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.allUpTo(column: ColumnPath): ColumnSet<C> =
        allUpToInternal { it.path == column } as ColumnSet<C>

    /** @include [ColumnSetAllUpToDocs] {@setArg [ColumnSetAllUpToDocs.Arg] ("myColumn")} */
    public fun <C> ColumnSet<C>.allUpTo(column: String): ColumnSet<C> =
        allUpTo(pathOf(column))

    /** @include [ColumnSetAllUpToDocs] {@setArg [ColumnSetAllUpToDocs.Arg] (myColumn)} */
    public fun <C> ColumnSet<C>.allUpTo(column: AnyColumnReference): ColumnSet<C> =
        allUpTo(column.path())

    /** @include [ColumnSetAllUpToDocs] {@setArg [ColumnSetAllUpToDocs.Arg] (Type::myColumn)} */
    public fun <C> ColumnSet<C>.allUpTo(column: KProperty<*>): ColumnSet<C> =
        allUpTo(column.toColumnAccessor().path())

    /**
     * @include [AllUpToDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allUpTo][ColumnsSelectionDsl.allColsUpTo]`{@getArg [ColumnsSelectionDslAllUpToDocs.Arg]} }`
     */
    private interface ColumnsSelectionDslAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnsSelectionDslAllUpToDocs] {@setArg [ColumnsSelectionDslAllUpToDocs.Arg] \\ \{ myColumn \\\}} */
    public fun <T> ColumnsSelectionDsl<T>.allUpTo(column: ColumnSelector<T, *>): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** @include [ColumnsSelectionDslAllUpToDocs] {@setArg [ColumnsSelectionDslAllUpToDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** @include [ColumnsSelectionDslAllUpToDocs] {@setArg [ColumnsSelectionDslAllUpToDocs.Arg] ("myColumn")} */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: String): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** @include [ColumnsSelectionDslAllUpToDocs] {@setArg [ColumnsSelectionDslAllUpToDocs.Arg] (myColumn)} */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /** @include [ColumnsSelectionDslAllUpToDocs] {@setArg [ColumnsSelectionDslAllUpToDocs.Arg] (Type::myColumn)} */
    public fun ColumnsSelectionDsl<*>.allUpTo(column: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsUpTo(column)

    /**
     * @include [AllUpToDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allColsUpTo][SingleColumn.allColsUpTo]`{@getArg [SingleColumnAllUpToDocs.Arg]} }`
     */
    private interface SingleColumnAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllUpToDocs] {@setArg [SingleColumnAllUpToDocs.Arg] \\ \{ myColumn \\\}} */
    public fun <T> SingleColumn<DataRow<T>>.allColsUpTo(column: ColumnSelector<T, *>): ColumnSet<*> {
        var resolvedCol: DataColumn<*>? = null
        return this
            .ensureIsColumnGroup()
            .onResolve { resolvedCol = it!!.asColumnGroup().getColumn(column) }
            .allUpToInternal { it.data == resolvedCol!! }
    }

    /** @include [SingleColumnAllUpToDocs] {@setArg [SingleColumnAllUpToDocs.Arg] ("pathTo"["myColumn"])} */
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

    /** @include [SingleColumnAllUpToDocs] {@setArg [SingleColumnAllUpToDocs.Arg] ("myColumn")} */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: String): ColumnSet<*> =
        allColsUpTo(pathOf(column))

    /** @include [SingleColumnAllUpToDocs] {@setArg [SingleColumnAllUpToDocs.Arg] (myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        allColsUpTo(column.path())

    /** @include [SingleColumnAllUpToDocs] {@setArg [SingleColumnAllUpToDocs.Arg] (Type::myColumn)} */
    public fun SingleColumn<DataRow<*>>.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        allColsUpTo(column.toColumnAccessor().path())

    /**
     * @include [AllUpToDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allColsUpTo][String.allColsUpTo]`{@getArg [StringAllUpToDocs.Arg]} }`
     */
    private interface StringAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllUpToDocs] {@setArg [StringAllUpToDocs.Arg] \\ \{ myColumn \\\}} */
    public fun String.allColsUpTo(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [StringAllUpToDocs] {@setArg [StringAllUpToDocs.Arg] ("pathTo"["myColumn"])} */
    public fun String.allColsUpTo(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [StringAllUpToDocs] {@setArg [StringAllUpToDocs.Arg] ("myColumn")} */
    public fun String.allColsUpTo(column: String): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [StringAllUpToDocs] {@setArg [StringAllUpToDocs.Arg] (myColumn)} */
    public fun String.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [StringAllUpToDocs] {@setArg [StringAllUpToDocs.Arg] (Type::myColumn)} */
    public fun String.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /**
     * @include [AllUpToDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allColsUpTo][KProperty.allColsUpTo]`{@getArg [KPropertyAllUpToDocs.Arg]} }`
     */
    private interface KPropertyAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /**
     * @include [KPropertyAllUpToDocs] {@setArg [KPropertyAllUpToDocs.Arg] \\ \{ myColumn \\\}}
     * {@comment We cannot provide types here because we'd need KProperty<T> and KProperty<DataRow<T>>, which provides
     * ambiguity as receiver.}
     */
    public fun KProperty<*>.allColsUpTo(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@setArg [KPropertyAllUpToDocs.Arg] ("pathTo"["myColumn"])} */
    public fun KProperty<*>.allColsUpTo(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@setArg [KPropertyAllUpToDocs.Arg] ("myColumn")} */
    public fun KProperty<*>.allColsUpTo(column: String): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@setArg [KPropertyAllUpToDocs.Arg] (myColumn)} */
    public fun KProperty<*>.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@setArg [KPropertyAllUpToDocs.Arg] (Type::myColumn)} */
    public fun KProperty<*>.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /**
     * @include [AllUpToDocs]
     * @setArg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allColsUpTo][ColumnPath.allColsUpTo]`{@getArg [ColumnPathAllUpToDocs.Arg]} }`
     */
    private interface ColumnPathAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllUpToDocs] {@setArg [ColumnPathAllUpToDocs.Arg] \\ \{ myColumn \\\}} */
    public fun ColumnPath.allColsUpTo(column: ColumnSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [ColumnPathAllUpToDocs] {@setArg [ColumnPathAllUpToDocs.Arg] ("pathTo"["myColumn"])} */
    public fun ColumnPath.allColsUpTo(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [ColumnPathAllUpToDocs] {@setArg [ColumnPathAllUpToDocs.Arg] ("myColumn")} */
    public fun ColumnPath.allColsUpTo(column: String): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [ColumnPathAllUpToDocs] {@setArg [ColumnPathAllUpToDocs.Arg] (myColumn)} */
    public fun ColumnPath.allColsUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

    /** @include [ColumnPathAllUpToDocs] {@setArg [ColumnPathAllUpToDocs.Arg] (Type::myColumn)} */
    public fun ColumnPath.allColsUpTo(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsUpTo(column)

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
        if (isSingleColumnWithGroup(it)) {
            it.single().children()
        } else {
            it
        }
    }

/**
 * Returns a new ColumnSet containing all columns after the first column that matches the given predicate.
 *
 * @param colByPredicate a function that takes a ColumnWithPath and returns true if the column matches the predicate, false otherwise
 * @return a new ColumnSet containing all columns after the first column that matches the given predicate
 */
internal fun ColumnsResolver<*>.allAfterInternal(colByPredicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> {
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
internal fun ColumnsResolver<*>.allFromInternal(colByPredicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> {
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
internal fun ColumnsResolver<*>.allBeforeInternal(colByPredicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> {
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
internal fun ColumnsResolver<*>.allUpToInternal(colByPredicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> {
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
