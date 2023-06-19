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
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
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
     * {@includeArg [CommonAllDocs.Examples]}
     *
     * {@include [AllFlavors]}
     *
     * @see [allBefore\]
     * @see [allAfter\]
     * @see [allFrom\]
     * @see [allUpTo\]
     * @see [cols\]
     */
    private interface CommonAllDocs {

        /** Example argument */
        interface Examples
    }

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "a" in `[name][ColumnWithPath.name]` }.`[all][ColumnSet.all]`() }`
     * {@include [LineBreak]}
     * NOTE: This is an identity call and can be omitted in most cases. However, it can still prove useful
     * for readability or in combination with [recursively][ColumnsSelectionDsl.recursively].
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.all(): TransformableColumnSet<C> = allColumnsInternal() as TransformableColumnSet<C>

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`() }`
     *
     * `df.`[select][DataFrame.select]` { myGroup.`[all][SingleColumn.all]`() }`
     */
    public fun SingleColumn<DataRow<*>>.all(): TransformableColumnSet<*> = ensureIsColGroup().allColumnsInternal()

    public fun ColumnsSelectionDsl<*>.all(): TransformableColumnSet<*> = this.asSingleColumn().allColumnsInternal()

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[all][String.all]`() }`
     */
    public fun String.all(): TransformableColumnSet<*> = columnGroup(this).all()

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::columnGroup).`[all][SingleColumn.all]`() }`
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[all][SingleColumn.all]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::columnGroup.`[all][KProperty.all]`() }`
     */
    public fun KProperty<DataRow<*>>.all(): TransformableColumnSet<*> = columnGroup(this).all()

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroup"].`[all][ColumnPath.all]`() }`
     */
    public fun ColumnPath.all(): TransformableColumnSet<*> = columnGroup(this).all()

    // endregion

    /**
     * ## {@includeArg [TitleArg]}
     *
     * Creates a new [ColumnSet] that contains a subset from the current [ColumnSet],
     * containing all columns {@includeArg [BehaviorArg]}.
     *
     * If the current [ColumnSet] is a [SingleColumn] and consists of only one [column group][ColumnGroup],
     * then the function will take columns from its children.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[{@includeArg [FunctionArg]}][SingleColumn.{@includeArg [FunctionArg]}]`("someColumn") }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[{@includeArg [FunctionArg]}][SingleColumn.{@includeArg [FunctionArg]}]`(someColumn) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[{@includeArg [FunctionArg]}][SingleColumn.{@includeArg [FunctionArg]}]`(Type::someColumn) }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg]}
     *
     * {@include [AllFlavors]}
     *
     * @return A new [ColumnSet] containing all columns {@includeArg [BehaviorArg]}.
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
     * @include [CommonAllSubsetDocs]
     * @arg [CommonAllSubsetDocs.TitleArg] All After
     * @arg [CommonAllSubsetDocs.FunctionArg] allAfter
     * @arg [CommonAllSubsetDocs.BehaviorArg] after [column\], excluding [column\] itself
     * @param [column\] The specified column after which all columns should be taken.
     */
    private interface AllAfterDocs

    /**
     * @include [AllAfterDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allAfter][ColumnSet.allAfter]`({@includeArg [ColumnSetAllAfterDocs.Arg]}) }`
     */
    private interface ColumnSetAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllAfterDocs] {@arg [ColumnSetAllAfterDocs.Arg] "pathTo"["myColumn"]} */
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

    /** @include [ColumnSetAllAfterDocs] {@arg [ColumnSetAllAfterDocs.Arg] "myColumn"} */
    public fun <C> ColumnSet<C>.allAfter(column: String): ColumnSet<C> = allAfter(pathOf(column))

    /** @include [ColumnSetAllAfterDocs] {@arg [ColumnSetAllAfterDocs.Arg] myColumn} */
    public fun <C> ColumnSet<C>.allAfter(column: AnyColumnReference): ColumnSet<C> = allAfter(column.path())

    /** @include [ColumnSetAllAfterDocs] {@arg [ColumnSetAllAfterDocs.Arg] Type::myColumn} */
    public fun <C> ColumnSet<C>.allAfter(column: KProperty<*>): ColumnSet<C> =
        allAfter(column.toColumnAccessor().path())

    /**
     * @include [AllAfterDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allAfter][SingleColumn.allAfter]`({@includeArg [SingleColumnAllAfterDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allAfter][SingleColumn.allAfter]`({@includeArg [SingleColumnAllAfterDocs.Arg]}) }`
     */
    private interface SingleColumnAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllAfterDocs] {@arg [SingleColumnAllAfterDocs.Arg] "pathTo"["myColumn"]} */
    public fun SingleColumn<DataRow<*>>.allAfter(column: ColumnPath): ColumnSet<*> =
        ensureIsColGroup().asColumnSet().allAfter(column)

    /** @include [SingleColumnAllAfterDocs] {@arg [SingleColumnAllAfterDocs.Arg] "myColumn"} */
    public fun SingleColumn<DataRow<*>>.allAfter(column: String): ColumnSet<*> = allAfter(pathOf(column))

    /** @include [SingleColumnAllAfterDocs] {@arg [SingleColumnAllAfterDocs.Arg] myColumn} */
    public fun SingleColumn<DataRow<*>>.allAfter(column: AnyColumnReference): ColumnSet<*> = allAfter(column.path())

    /** @include [SingleColumnAllAfterDocs] {@arg [SingleColumnAllAfterDocs.Arg] Type::myColumn} */
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
     * @include [AllAfterDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allAfter][String.allAfter]`({@includeArg [StringAllAfterDocs.Arg]}) }`
     */
    private interface StringAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllAfterDocs] {@arg [StringAllAfterDocs.Arg] "pathTo"["myColumn"]} */
    public fun String.allAfter(column: ColumnPath): ColumnSet<*> = columnGroup(this).allAfter(column)

    /** @include [StringAllAfterDocs] {@arg [StringAllAfterDocs.Arg] "myColumn"} */
    public fun String.allAfter(column: String): ColumnSet<*> = columnGroup(this).allAfter(column)

    /** @include [StringAllAfterDocs] {@arg [StringAllAfterDocs.Arg] myColumn} */
    public fun String.allAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** @include [StringAllAfterDocs] {@arg [StringAllAfterDocs.Arg] Type::myColumn} */
    public fun String.allAfter(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allAfter(column)

    /**
     * @include [AllAfterDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::myColGroup).`[allAfter][SingleColumn.allAfter]`({@includeArg [KPropertyAllAfterDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[allAfter][KProperty.allAfter]`({@includeArg [KPropertyAllAfterDocs.Arg]}) }`
     */
    private interface KPropertyAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allAfter(column: String): ColumnSet<*> = columnGroup(this).allAfter(column)

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /**
     * @include [AllAfterDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allAfter][ColumnPath.allAfter]`({@includeArg [ColumnPathAllAfterDocs.Arg]}) }`
     */
    private interface ColumnPathAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllAfterDocs] {@arg [ColumnPathAllAfterDocs.Arg] "pathTo"["myColumn"]} */
    public fun ColumnPath.allAfter(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@arg [ColumnPathAllAfterDocs.Arg] "myColumn"} */
    public fun ColumnPath.allAfter(column: String): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@arg [ColumnPathAllAfterDocs.Arg] myColumn} */
    public fun ColumnPath.allAfter(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@arg [ColumnPathAllAfterDocs.Arg] Type::myColumn} */
    public fun ColumnPath.allAfter(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allAfter(column)

    // endregion

    // region allFrom

    /**
     * @include [CommonAllSubsetDocs]
     * @arg [CommonAllSubsetDocs.TitleArg] All From
     * @arg [CommonAllSubsetDocs.FunctionArg] allFrom
     * @arg [CommonAllSubsetDocs.BehaviorArg] from [column\], including [column\] itself
     * @param [column\] The specified column from which all columns should be taken.
     */
    private interface AllFromDocs

    /**
     * @include [AllFromDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allFrom][ColumnSet.allFrom]`({@includeArg [ColumnSetAllFromDocs.Arg]}) }`
     */
    private interface ColumnSetAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllFromDocs] {@arg [ColumnSetAllFromDocs.Arg] "pathTo"["myColumn"]} */
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

    /** @include [ColumnSetAllFromDocs] {@arg [ColumnSetAllFromDocs.Arg] "myColumn"} */
    public fun <C> ColumnSet<C>.allFrom(column: String): ColumnSet<C> = allFrom(pathOf(column))

    /** @include [ColumnSetAllFromDocs] {@arg [ColumnSetAllFromDocs.Arg] myColumn} */
    public fun <C> ColumnSet<C>.allFrom(column: AnyColumnReference): ColumnSet<C> = allFrom(column.path())

    /** @include [ColumnSetAllFromDocs] {@arg [ColumnSetAllFromDocs.Arg] Type::myColumn} */
    public fun <C> ColumnSet<C>.allFrom(column: KProperty<*>): ColumnSet<C> =
        allFrom(column.toColumnAccessor().path())

    /**
     * @include [AllFromDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allFrom][SingleColumn.allFrom]`({@includeArg [SingleColumnAllFromDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allFrom][SingleColumn.allFrom]`({@includeArg [SingleColumnAllFromDocs.Arg]}) }`
     */
    private interface SingleColumnAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllFromDocs] {@arg [SingleColumnAllFromDocs.Arg] "pathTo"["myColumn"]} */
    public fun SingleColumn<DataRow<*>>.allFrom(column: ColumnPath): ColumnSet<*> =
        ensureIsColGroup().asColumnSet().allFrom(column)

    /** @include [SingleColumnAllFromDocs] {@arg [SingleColumnAllFromDocs.Arg] "myColumn"} */
    public fun SingleColumn<DataRow<*>>.allFrom(column: String): ColumnSet<*> = allFrom(pathOf(column))

    /** @include [SingleColumnAllFromDocs] {@arg [SingleColumnAllFromDocs.Arg] myColumn} */
    public fun SingleColumn<DataRow<*>>.allFrom(column: AnyColumnReference): ColumnSet<*> = allFrom(column.path())

    /** @include [SingleColumnAllFromDocs] {@arg [SingleColumnAllFromDocs.Arg] Type::myColumn} */
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
     * @include [AllFromDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allFrom][String.allFrom]`({@includeArg [StringAllFromDocs.Arg]}) }`
     */
    private interface StringAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllFromDocs] {@arg [StringAllFromDocs.Arg] "pathTo"["myColumn"]} */
    public fun String.allFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** @include [StringAllFromDocs] {@arg [StringAllFromDocs.Arg] "myColumn"} */
    public fun String.allFrom(column: String): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** @include [StringAllFromDocs] {@arg [StringAllFromDocs.Arg] myColumn} */
    public fun String.allFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /** @include [StringAllFromDocs] {@arg [StringAllFromDocs.Arg] Type::myColumn} */
    public fun String.allFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allFrom(column)

    /**
     * @include [AllFromDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allFrom][SingleColumn.allFrom]`({@includeArg [KPropertyAllFromDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allFrom][KProperty.allFrom]`({@includeArg [KPropertyAllFromDocs.Arg]}) }`
     */
    private interface KPropertyAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allFrom(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allFrom(column: String): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allFrom(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allFrom(column)

    /**
     * @include [AllFromDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allFrom][ColumnPath.allFrom]`({@includeArg [ColumnPathAllFromDocs.Arg]}) }`
     */
    private interface ColumnPathAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllFromDocs] {@arg [ColumnPathAllFromDocs.Arg] "pathTo"["myColumn"]} */
    public fun ColumnPath.allFrom(column: ColumnPath): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** @include [ColumnPathAllFromDocs] {@arg [ColumnPathAllFromDocs.Arg] "myColumn"} */
    public fun ColumnPath.allFrom(column: String): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** @include [ColumnPathAllFromDocs] {@arg [ColumnPathAllFromDocs.Arg] myColumn} */
    public fun ColumnPath.allFrom(column: AnyColumnReference): ColumnSet<*> = columnGroup(this).allFrom(column)

    /** @include [ColumnPathAllFromDocs] {@arg [ColumnPathAllFromDocs.Arg] Type::myColumn} */
    public fun ColumnPath.allFrom(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allFrom(column)

    // endregion

    // region allBefore

    /**
     * @include [CommonAllSubsetDocs]
     * @arg [CommonAllSubsetDocs.TitleArg] All Before
     * @arg [CommonAllSubsetDocs.FunctionArg] allBefore
     * @arg [CommonAllSubsetDocs.BehaviorArg] before [column\], excluding [column\] itself
     * @param [column\] The specified column before which all columns should be taken
     */
    private interface AllBeforeDocs

    /**
     * @include [AllBeforeDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allBefore][ColumnSet.allBefore]`({@includeArg [ColumnSetAllBeforeDocs.Arg]}) }`
     */
    private interface ColumnSetAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllBeforeDocs] {@arg [ColumnSetAllBeforeDocs.Arg] "pathTo"["myColumn"]} */
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

    /** @include [ColumnSetAllBeforeDocs] {@arg [ColumnSetAllBeforeDocs.Arg] "myColumn"} */
    public fun <C> ColumnSet<C>.allBefore(column: String): ColumnSet<C> = allBefore(pathOf(column))

    /** @include [ColumnSetAllBeforeDocs] {@arg [ColumnSetAllBeforeDocs.Arg] myColumn} */
    public fun <C> ColumnSet<C>.allBefore(column: AnyColumnReference): ColumnSet<C> = allBefore(column.path())

    /** @include [ColumnSetAllBeforeDocs] {@arg [ColumnSetAllBeforeDocs.Arg] Type::myColumn} */
    public fun <C> ColumnSet<C>.allBefore(column: KProperty<*>): ColumnSet<C> =
        allBefore(column.toColumnAccessor().path())

    /**
     * @include [AllBeforeDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allBefore][SingleColumn.allBefore]`({@includeArg [SingleColumnAllBeforeDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allBefore][SingleColumn.allBefore]`({@includeArg [SingleColumnAllBeforeDocs.Arg]}) }`
     */
    private interface SingleColumnAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllBeforeDocs] {@arg [SingleColumnAllBeforeDocs.Arg] "pathTo"["myColumn"]} */
    public fun SingleColumn<DataRow<*>>.allBefore(column: ColumnPath): ColumnSet<*> =
        ensureIsColGroup().asColumnSet().allBefore(column)

    /** @include [SingleColumnAllBeforeDocs] {@arg [SingleColumnAllBeforeDocs.Arg] "myColumn"} */
    public fun SingleColumn<DataRow<*>>.allBefore(column: String): ColumnSet<*> = allBefore(pathOf(column))

    /** @include [SingleColumnAllBeforeDocs] {@arg [SingleColumnAllBeforeDocs.Arg] myColumn} */
    public fun SingleColumn<DataRow<*>>.allBefore(column: AnyColumnReference): ColumnSet<*> = allBefore(column.path())

    /** @include [SingleColumnAllBeforeDocs] {@arg [SingleColumnAllBeforeDocs.Arg] Type::myColumn} */
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
     * @include [AllBeforeDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allBefore][String.allBefore]`({@includeArg [StringAllBeforeDocs.Arg]}) }`
     */
    private interface StringAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllBeforeDocs] {@arg [StringAllBeforeDocs.Arg] "pathTo"["myColumn"]} */
    public fun String.allBefore(column: ColumnPath): ColumnSet<*> = columnGroup(this).allBefore(column)

    /** @include [StringAllBeforeDocs] {@arg [StringAllBeforeDocs.Arg] "myColumn"} */
    public fun String.allBefore(column: String): ColumnSet<*> = columnGroup(this).allBefore(column)

    /** @include [StringAllBeforeDocs] {@arg [StringAllBeforeDocs.Arg] myColumn} */
    public fun String.allBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** @include [StringAllBeforeDocs] {@arg [StringAllBeforeDocs.Arg] Type::myColumn} */
    public fun String.allBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /**
     * @include [AllBeforeDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allBefore][SingleColumn.allBefore]`({@includeArg [KPropertyAllBeforeDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allBefore][KProperty.allBefore]`({@includeArg [KPropertyAllBeforeDocs.Arg]}) }`
     */
    private interface KPropertyAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allBefore(column: String): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /**
     * @include [AllBeforeDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allBefore][ColumnPath.allBefore]`({@includeArg [ColumnPathAllBeforeDocs.Arg]}) }`
     */
    private interface ColumnPathAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllBeforeDocs] {@arg [ColumnPathAllBeforeDocs.Arg] "pathTo"["myColumn"]} */
    public fun ColumnPath.allBefore(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@arg [ColumnPathAllBeforeDocs.Arg] "myColumn"} */
    public fun ColumnPath.allBefore(column: String): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@arg [ColumnPathAllBeforeDocs.Arg] myColumn} */
    public fun ColumnPath.allBefore(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@arg [ColumnPathAllBeforeDocs.Arg] Type::myColumn} */
    public fun ColumnPath.allBefore(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allBefore(column)

    // endregion

    // region allUpTo

    /**
     * @include [CommonAllSubsetDocs]
     * @arg [CommonAllSubsetDocs.TitleArg] All Up To
     * @arg [CommonAllSubsetDocs.FunctionArg] allUpTo
     * @arg [CommonAllSubsetDocs.BehaviorArg] up to [column\], including [column\] itself
     * @param [column\] The specified column up to which all columns should be taken.
     */
    private interface AllUpToDocs

    /**
     * @include [AllUpToDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[allUpTo][ColumnSet.allUpTo]`({@includeArg [ColumnSetAllUpToDocs.Arg]}) }`
     */
    private interface ColumnSetAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllUpToDocs] {@arg [ColumnSetAllUpToDocs.Arg] "pathTo"["myColumn"]} */
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

    /** @include [ColumnSetAllUpToDocs] {@arg [ColumnSetAllUpToDocs.Arg] "myColumn"} */
    public fun <C> ColumnSet<C>.allUpTo(column: String): ColumnSet<C> = allUpTo(pathOf(column))

    /** @include [ColumnSetAllUpToDocs] {@arg [ColumnSetAllUpToDocs.Arg] myColumn} */
    public fun <C> ColumnSet<C>.allUpTo(column: AnyColumnReference): ColumnSet<C> = allUpTo(column.path())

    /** @include [ColumnSetAllUpToDocs] {@arg [ColumnSetAllUpToDocs.Arg] Type::myColumn} */
    public fun <C> ColumnSet<C>.allUpTo(column: KProperty<*>): ColumnSet<C> =
        allUpTo(column.toColumnAccessor().path())

    /**
     * @include [AllUpToDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[allUpTo][SingleColumn.allUpTo]`({@includeArg [SingleColumnAllUpToDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { someColumnGroup.`[allUpTo][SingleColumn.allUpTo]`({@includeArg [SingleColumnAllUpToDocs.Arg]}) }`
     */
    private interface SingleColumnAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [SingleColumnAllUpToDocs] {@arg [SingleColumnAllUpToDocs.Arg] "pathTo"["myColumn"]} */
    public fun SingleColumn<DataRow<*>>.allUpTo(column: ColumnPath): ColumnSet<*> =
        ensureIsColGroup().asColumnSet().allUpTo(column)

    /** @include [SingleColumnAllUpToDocs] {@arg [SingleColumnAllUpToDocs.Arg] "myColumn"} */
    public fun SingleColumn<DataRow<*>>.allUpTo(column: String): ColumnSet<*> = allUpTo(pathOf(column))

    /** @include [SingleColumnAllUpToDocs] {@arg [SingleColumnAllUpToDocs.Arg] myColumn} */
    public fun SingleColumn<DataRow<*>>.allUpTo(column: AnyColumnReference): ColumnSet<*> = allUpTo(column.path())

    /** @include [SingleColumnAllUpToDocs] {@arg [SingleColumnAllUpToDocs.Arg] Type::myColumn} */
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
     * @include [AllUpToDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someColGroup".`[allUpTo][String.allUpTo]`({@includeArg [StringAllUpToDocs.Arg]}) }`
     */
    private interface StringAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [StringAllUpToDocs] {@arg [StringAllUpToDocs.Arg] "pathTo"["myColumn"]} */
    public fun String.allUpTo(column: ColumnPath): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /** @include [StringAllUpToDocs] {@arg [StringAllUpToDocs.Arg] "myColumn"} */
    public fun String.allUpTo(column: String): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /** @include [StringAllUpToDocs] {@arg [StringAllUpToDocs.Arg] myColumn} */
    public fun String.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /** @include [StringAllUpToDocs] {@arg [StringAllUpToDocs.Arg] Type::myColumn} */
    public fun String.allUpTo(column: KProperty<*>): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /**
     * @include [AllUpToDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(SomeType::someColGroup).`[allUpTo][SingleColumn.allUpTo]`({@includeArg [KPropertyAllUpToDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someColGroup.`[allUpTo][KProperty.allUpTo]`({@includeArg [KPropertyAllUpToDocs.Arg]}) }`
     */
    private interface KPropertyAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allUpTo(column: ColumnPath): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allUpTo(column: String): ColumnSet<*> = columnGroup(this).allUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allUpTo(column: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allUpTo(column)

    /**
     * @include [AllUpToDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someColGroup"].`[allUpTo][ColumnPath.allUpTo]`({@includeArg [ColumnPathAllUpToDocs.Arg]}) }`
     */
    private interface ColumnPathAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnPathAllUpToDocs] {@arg [ColumnPathAllUpToDocs.Arg] "pathTo"["myColumn"]} */
    public fun ColumnPath.allUpTo(column: ColumnPath): ColumnSet<*> =
        allUpTo(column.toColumnAccessor().path())

    /** @include [ColumnPathAllUpToDocs] {@arg [ColumnPathAllUpToDocs.Arg] "myColumn"} */
    public fun ColumnPath.allUpTo(column: String): ColumnSet<*> = allUpTo(pathOf(column))

    /** @include [ColumnPathAllUpToDocs] {@arg [ColumnPathAllUpToDocs.Arg] myColumn} */
    public fun ColumnPath.allUpTo(column: AnyColumnReference): ColumnSet<*> = allUpTo(column.path())

    /** @include [ColumnPathAllUpToDocs] {@arg [ColumnPathAllUpToDocs.Arg] Type::myColumn} */
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
