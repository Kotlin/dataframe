package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.DoubleIndent
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.*
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.flattenRecursively
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * @include [DocumentationUrls.ColumnSelectors]
 */
private interface CommonColumnSelectionDocs

/**
 * {@comment TODO}
 */
private interface CommonColumnSelectionExamples

/** [Column Selection DSL][ColumnSelectionDsl] */
internal interface ColumnSelectionDslLink

/** @include [CommonColumnSelectionDocs] */
public interface ColumnSelectionDsl<out T> : ColumnsContainer<T> {

    /**
     * Retrieves the value of this [ColumnReference] or [-Accessor][ColumnAccessor] from
     * the [DataFrame].
     *
     * This is a shorthand for [get][ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private interface CommonColumnReferenceInvokeDocs

    /**
     * @include [CommonColumnReferenceInvokeDocs]
     * @return The [DataColumn] this [Column Reference][ColumnReference] or [-Accessor][ColumnAccessor] points to.
     */
    public operator fun <C> ColumnReference<C>.invoke(): DataColumn<C> = get(this)

    /**
     * @include [CommonColumnReferenceInvokeDocs]
     * @return The [ColumnGroup] this [Column Reference][ColumnReference] or [-Accessor][ColumnAccessor] points to.
     */
    public operator fun <T> ColumnReference<DataRow<T>>.invoke(): ColumnGroup<T> = get(this)

    /**
     * @include [CommonColumnReferenceInvokeDocs]
     * @return The [FrameColumn] this [Column Reference][ColumnReference] or [-Accessor][ColumnAccessor] points to.
     */
    public operator fun <T> ColumnReference<DataFrame<T>>.invoke(): FrameColumn<T> = get(this)

    /**
     * Retrieves the value of this [ColumnPath] from the [DataFrame].
     * This is a shorthand for [getColumn][ColumnsContainer.getColumn]`(myColumnPath)` and
     * is most often used in combination with `operator fun String.get(column: String)`, {@comment cannot point to the right function.}
     * for instance:
     * ```kotlin
     * "myColumn"["myNestedColumn"]<NestedColumnType>()
     * ```
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [DataColumn] this [ColumnPath] points to.
     */
    public operator fun <C> ColumnPath.invoke(): DataColumn<C> = getColumn(this).cast()

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame].
     *
     * This is a shorthand for [get][ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private interface CommonKPropertyInvokeDocs

    /**
     * @include [CommonKPropertyInvokeDocs]
     * @return The [DataColumn] this [KProperty Accessor][KProperty] points to.
     */
    public operator fun <T> KProperty<T>.invoke(): DataColumn<T> = this@ColumnSelectionDsl[this]

    /**
     * @include [CommonKPropertyInvokeDocs]
     * @return The [ColumnGroup] this [KProperty Accessor][KProperty] points to.
     */
    public operator fun <T> KProperty<DataRow<T>>.invoke(): ColumnGroup<T> = this@ColumnSelectionDsl[this]

    /**
     * @include [CommonKPropertyInvokeDocs]
     * @return The [FrameColumn] this [KProperty Accessor][KProperty] points to.
     */
    public operator fun <T> KProperty<DataFrame<T>>.invoke(): FrameColumn<T> = this@ColumnSelectionDsl[this]

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame].
     *
     * This is a shorthand for
     *
     * [get][ColumnsContainer.get]`(MyType::myColumn).`[get][ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumn`[`[`][ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][ColumnsContainer.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private interface CommonKPropertyGetDocs

    /**
     * @include [CommonKPropertyGetDocs]
     * @return The [DataColumn] these [KProperty Accessors][KProperty] point to.
     */
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<R>): DataColumn<R> = invoke()[column]

    /**
     * @include [CommonKPropertyGetDocs]
     * @return The [ColumnGroup] these [KProperty Accessors][KProperty] point to.
     */
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<DataRow<R>>): ColumnGroup<R> =
        invoke()[column]

    /**
     * @include [CommonKPropertyGetDocs]
     * @return The [FrameColumn] these [KProperty Accessors][KProperty] point to.
     */
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<DataFrame<R>>): FrameColumn<R> =
        invoke()[column]

    /**
     * Retrieves the value of the column with this name from the [DataFrame].
     * This is a shorthand for [get][ColumnsContainer.get]`("myColumnName")` and can be
     * written as `"myColumnName"<MyColumnType>()` instead.
     *
     * @throws [IllegalArgumentException] if there is no column with this name.
     * @return The [DataColumn] with this name.
     */
    public operator fun <C> String.invoke(): DataColumn<C> = getColumn(this).cast()

    /**
     * Creates a [ColumnPath] from the receiver and the given column name [column].
     * This is a shorthand for [pathOf]`("myColumnName", "myNestedColumnName")` and is often used
     * in combination with [ColumnPath.invoke] to retrieve the value of a nested column.
     * For instance:
     * ```kotlin
     * "myColumn"["myNestedColumn"]<NestedColumnType>()
     * ```
     */
    public operator fun String.get(column: String): ColumnPath = pathOf(this, column)
}

/** [Columns Selection DSL][ColumnsSelectionDsl] */
internal interface ColumnsSelectionDslLink

/** @include [CommonColumnSelectionDocs] */
public interface ColumnsSelectionDsl<out T> : ColumnSelectionDsl<T>, SingleColumn<DataRow<T>> {

    // region first

    /**
     * ## First
     * Returns the first column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
     * If no column adheres to the given [condition\], no column is selected.
     *
     * #### For example:
     *
     * {@includeArg [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the first column that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @see [last\]
     */
    private interface CommonFirstDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * @include [CommonFirstDocs]
     * @arg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[first][first]`() }`
     */
    public fun <C> ColumnSet<C>.first(condition: ColumnFilter<C> = { true }): TransformableSingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.first(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * @include [CommonFirstDocs]
     * @arg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { `[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[first][first]`() }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun SingleColumn<DataRow<*>>.first(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        ensureIsColGroup().asColumnSet().first(condition)

    /**
     * @include [CommonFirstDocs]
     * @arg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun String.first(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).first(condition)

    /**
     * @include [CommonFirstDocs]
     * @arg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun KProperty<DataRow<*>>.first(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).first(condition)

    /**
     * @include [CommonFirstDocs]
     * @arg [CommonFirstDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.first(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).first(condition)

    // endregion

    // region last

    /**
     * ## Last
     * Returns the last column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
     * If no column adheres to the given [condition\], no column is selected.
     *
     * #### For example:
     *
     * {@includeArg [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the last column that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @see [first\]
     */
    private interface CommonLastDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * @include [CommonLastDocs]
     * @arg [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[first][last]`() }`
     */
    public fun <C> ColumnSet<C>.last(condition: ColumnFilter<C> = { true }): TransformableSingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.last(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * @include [CommonLastDocs]
     * @arg [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { `[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[last][last]`() }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun SingleColumn<DataRow<*>>.last(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        ensureIsColGroup().asColumnSet().last(condition)

    /**
     * @include [CommonLastDocs]
     * @arg [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun String.last(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).last(condition)

    /**
     * @include [CommonLastDocs]
     * @arg [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun KProperty<DataRow<*>>.last(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).last(condition)

    /**
     * @include [CommonLastDocs]
     * @arg [CommonLastDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.last(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).last(condition)

    // endregion

    // region single

    /**
     * ## Single
     * Returns the single column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
     * If no column adheres to the given [condition\] or multiple columns adhere to it, no column is selected.
     *
     * #### For example:
     *
     * {@includeArg [Examples]}
     *
     * @param [condition\] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the single column that adheres to the given [condition\].
     * @throws [NoSuchElementException\] if no column adheres to the given [condition\].
     * @throws [IllegalArgumentException\] if more than one column adheres to the given [condition\].
     */
    private interface CommonSingleDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * @include [CommonSingleDocs]
     * @arg [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[single][single]`() }`
     */
    public fun <C> ColumnSet<C>.single(condition: ColumnFilter<C> = { true }): TransformableSingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.single(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * @include [CommonSingleDocs]
     * @arg [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { `[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[single][single]`() }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun SingleColumn<DataRow<*>>.single(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        ensureIsColGroup().asColumnSet().single(condition)

    /**
     * @include [CommonSingleDocs]
     * @arg [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun String.single(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).single(condition)

    /**
     * @include [CommonSingleDocs]
     * @arg [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun KProperty<DataRow<*>>.single(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).single(condition)

    /**
     * @include [CommonSingleDocs]
     * @arg [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.single(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        colGroup(this).single(condition)

    // endregion

    // region subset

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet] containing all columns from [this\] to [endInclusive\].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `{@includeArg [CommonSubsetOfColumnsDocs.Example]}` }`
     *
     * @param [endInclusive\] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet] containing all columns from [this\] to [endInclusive\].
     * @throws [IllegalArgumentException\] if the columns have different parents.
     */
    private interface CommonSubsetOfColumnsDocs {

        /** Examples key */
        interface Example
    }

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] "fromColumn".."toColumn"}
     */
    public operator fun String.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] "fromColumn"..Type::toColumn}
     */
    public operator fun String.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] "fromColumn"..toColumn}
     */
    public operator fun String.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] Type::fromColumn.."toColumn"}
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] Type::fromColumn..Type::toColumn}
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] Type::fromColumn..toColumn}
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] fromColumn.."toColumn"}
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: String): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] fromColumn..Type::toColumn}
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * @include [CommonSubsetOfColumnsDocs]
     * {@arg [CommonSubsetOfColumnsDocs.Example] fromColumn..toColumn}
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        object : ColumnSet<Any?> {

            private fun process(col: AnyColumnReference, context: ColumnResolutionContext): List<ColumnWithPath<Any?>> {
                val startPath = col.resolveSingle(context)!!.path
                val endPath = endInclusive.resolveSingle(context)!!.path
                val parentPath = startPath.parent()!!
                require(parentPath == endPath.parent()) { "Start and end columns have different parent column paths" }
                val parentCol = context.df.getColumnGroup(parentPath)
                val startIndex = parentCol.getColumnIndex(startPath.name)
                val endIndex = parentCol.getColumnIndex(endPath.name)
                return (startIndex..endIndex).map {
                    parentCol.getColumn(it).let {
                        it.addPath(parentPath + it.name)
                    }
                }
            }

            override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<Any?>> =
                process(this@rangeTo, context)
        }

    // endregion

    // region none

    /**
     * ## None
     * Creates an empty [ColumnSet], essentially selecting no columns at all.
     *
     * #### For example:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { `[none][none]`() }`
     *
     * @return An empty [ColumnSet].
     */
    public fun none(): ColumnsResolver<*> = ColumnsList<Any?>(emptyList())

    // endregion

    // region colGroupFrame

    // region colAccessor

    /**
     * ## Col: Column Accessor
     *
     * Creates a [ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup].
     *
     * {@includeArg [CommonColAccessorDocs.Note]}
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[col][col]`({@includeArg [CommonColAccessorDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[col][col]`<SomeType>({@includeArg [CommonColAccessorDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the column with the given argument.
     * @see [column\]
     * @see [colGroup\]
     * @see [frameCol\]
     * {@arg [CommonColAccessorDocs.Note]}
     */
    private interface CommonColAccessorDocs {

        /** Example argument */
        interface Arg

        /** Optional note */
        interface Note
    }

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnName"}
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun col(name: String): ColumnAccessor<*> = column<Any?>(name)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnName"}
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    public fun <C> col(name: String): ColumnAccessor<C> = column(name)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnGroup"["columnName"]}
     * {@arg [CommonColAccessorDocs.Note] NOTE: For column paths, this is an identity function and can be removed.}
     * @param [path] The [ColumnPath] pointing to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun col(path: ColumnPath): ColumnAccessor<*> = column<Any?>(path)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the column.
     * @param [C] The type of the column.
     */
    public fun <C> col(path: ColumnPath): ColumnAccessor<C> = column(path)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the column.
     */
    public fun <C> col(property: KProperty<C>): ColumnAccessor<C> = column(property)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnName"}
     * @param [name] The name of the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnGroupReference.col(name: String): ColumnAccessor<*> = column<Any?>(name)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnName"}
     * @param [name] The name of the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     * @param [C] The type of the column.
     */
    public fun <C> ColumnGroupReference.col(name: String): ColumnAccessor<C> = column(name)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnGroupReference.col(path: ColumnPath): ColumnAccessor<*> = column<Any?>(path)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     * @param [C] The type of the column.
     */
    public fun <C> ColumnGroupReference.col(path: ColumnPath): ColumnAccessor<C> = column(path)

    /**
     * @include [CommonColAccessorDocs] {@arg [CommonColAccessorDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     */
    public fun <C> ColumnGroupReference.col(property: KProperty<C>): ColumnAccessor<C> = column(property)

    // endregion

    // region colIndex

    /**
     * ## Col: Column by Index
     *
     * Retrieves a [column][SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn], [ColumnGroup], or [DataFrame], the function will take the child found at the
     * given [index\].
     * Else, if called on a normal [ColumnSet],
     * the function will return the [index\]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[col][SingleColumn.col\]`(3) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][SingleColumn.col\]`5`[`]`][SingleColumn.col\]` }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[col][String.col\]`(0) }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonColIndexDocs.ExampleArg]}
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index\] The index of the column to retrieve.
     * @return A [SingleColumn] for the column at the given index.
     */
    private interface CommonColIndexDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonColIndexDocs]
     * @arg [CommonColIndexDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[col][ColumnSet.col]`(0) }`
     *
     * `df.`[select][DataFrame.select]` { `[all][all]`()`[`[`][ColumnSet.col]`5`[`]`][ColumnSet.col]` }`
     */
    private interface ColumnSetColIndexDocs

    /** @include [ColumnSetColIndexDocs] */
    public fun <C> ColumnSet<C>.col(index: Int): SingleColumn<C> = getAt(index)

    /** @include [ColumnSetColIndexDocs] */
    public operator fun <C> ColumnSet<C>.get(index: Int): SingleColumn<C> = col(index)

    /**
     * @include [CommonColIndexDocs]
     * @arg [CommonColIndexDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[col][SingleColumn.col]`(0) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.col\]`5`[`]`][SingleColumn.col\]` }`
     */
    private interface SingleColumnColIndexDocs

    /** @include [SingleColumnColIndexDocs] */
    public fun SingleColumn<DataRow<*>>.col(index: Int): SingleColumn<*> =
        ensureIsColGroup().asColumnSet().getChildrenAt(index).singleImpl()

    /** @include [SingleColumnColIndexDocs] */
    public operator fun SingleColumn<DataRow<*>>.get(index: Int): SingleColumn<*> = col(index)

    /**
     * @include [CommonColIndexDocs]
     * @arg [CommonColIndexDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[col][String.col]`(5) }`
     *
     * `// NOTE: There's a `[String.get][String.get]` function that prevents this:`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"[0] }`
     */
    private interface StringIndexDocs

    /** @include [StringIndexDocs] */
    public fun String.col(index: Int): SingleColumn<*> = colGroup(this).col(index)

    /** @include [StringIndexDocs]
     * {@comment this function is shadowed by [String.get]} */
    public operator fun String.get(index: Int): SingleColumn<*> = col(index)

    /**
     * @include [CommonColIndexDocs]
     * @arg [CommonColIndexDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[col][KProperty.col]`(5) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[`[`][KProperty.col]`0`[`]`][KProperty.col]` }`
     */
    private interface KPropertyIndexDocs

    /** @include [KPropertyIndexDocs] */
    public fun KProperty<DataRow<*>>.col(index: Int): SingleColumn<*> = colGroup(this).col(index)

    /** @include [KPropertyIndexDocs] */
    public operator fun KProperty<DataRow<*>>.get(index: Int): SingleColumn<*> = col(index)

    /**
     * @include [CommonColIndexDocs]
     * @arg [CommonColIndexDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[col][ColumnPath.col]`(0) }`
     *
     * `// NOTE: There's a `[List.get][List.get]` function that prevents this:`
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[col][ColumnPath.col]`(0) }`
     */
    private interface ColumnPathIndexDocs

    /** @include [ColumnPathIndexDocs] */
    public fun ColumnPath.col(index: Int): SingleColumn<*> = colGroup(this).col(index)

    /** @include [ColumnPathIndexDocs] */
    public operator fun ColumnPath.get(index: Int): SingleColumn<*> = col(index)

    // endregion

    // region valueCol

    /**
     * ## Value Column Accessor
     * Creates a [ColumnAccessor] for a value column with the given argument.
     * This is a shorthand for [valueColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[valueCol][valueCol]`({@includeArg [CommonValueColDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCol][valueCol]`<SomeType>({@includeArg [CommonValueColDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup\]
     * @see [col\]
     * @see [frameCol\]
     */
    private interface CommonValueColDocs {

        /** Example argument */
        interface Arg
    }

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnName"}
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnName"}
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the value column.
     */
    public fun <C> valueCol(property: KProperty<C>): ColumnAccessor<C> = valueColumn(property)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnName"}
     * @param [name] The name of the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun ColumnGroupReference.valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnName"}
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    public fun <C> ColumnGroupReference.valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUntyped")
    public fun ColumnGroupReference.valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the value column.
     * @param [C] The type of the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    public fun <C> ColumnGroupReference.valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path)

    /**
     * @include [CommonValueColDocs] {@arg [CommonValueColDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the value column.
     * @receiver The [ColumnGroupReference] to get the value column from.
     */
    public fun <C> ColumnGroupReference.valueCol(property: KProperty<C>): ColumnAccessor<C> = valueColumn(property)

    // endregion

    // region colGroup

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`({@includeArg [CommonColGroupDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroup][colGroup]`<SomeType>({@includeArg [CommonColGroupDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup\]
     * @see [col\]
     * @see [valueCol\]
     * @see [frameCol\]
     */
    private interface CommonColGroupDocs {

        /** Example argument */
        interface Arg
    }

    @Deprecated("Use colGroup() instead.", ReplaceWith("this.colGroup(name)"))
    public fun ColumnsContainer<*>.group(name: String): ColumnGroupReference = name.toColumnOf()

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroupName"}
     * @param [name] The name of the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("groupUnTyped")
    public fun colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroupName"}
     * @param [name] The name of the column group.
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroup"["columnGroupName"]}
     * @param [path] The [ColumnPath] pointing to the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUntyped")
    public fun colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(path)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroup"["columnGroupName"]}
     * @param [path] The [ColumnPath] pointing to the column group.
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> = columnGroup<C>(path)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] Type::columnGroupName}
     * @param [property] The [KProperty] pointing to the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> = columnGroup(property)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] Type::columnGroupName}
     * @param [property] The [KProperty] pointing to the column group.
     */
    public fun <C> colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> = columnGroup(property)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroupName"}
     * @param [name] The name of the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUntyped")
    public fun ColumnGroupReference.colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroupName"}
     * @param [name] The name of the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnGroupReference.colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroup"["columnGroupName"]}
     * @param [path] The [ColumnPath] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUntyped")
    public fun ColumnGroupReference.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        columnGroup<Any?>(path)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] "columnGroup"["columnGroupName"]}
     * @param [path] The [ColumnPath] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnGroupReference.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        columnGroup<C>(path)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] Type::columnGroupName}
     * @param [property] The [KProperty] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> ColumnGroupReference.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(property)

    /**
     * @include [CommonColGroupDocs] {@arg [CommonColGroupDocs.Arg] Type::columnGroupName}
     * @param [property] The [KProperty] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    public fun <C> ColumnGroupReference.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        columnGroup(property)

    // endregion

    // region frameCol

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup].
     *
     * #### For example:
     * `df.`[select][DataFrame.select]` { `[frameCol][frameCol]`({@includeArg [CommonFrameColDocs.Arg]}) }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[frameCol][frameCol]`<SomeType>({@includeArg [CommonFrameColDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn\]
     * @see [colGroup\]
     * @see [valueCol\]
     * @see [col\]
     */
    private interface CommonFrameColDocs {

        /** Example argument */
        interface Arg
    }

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnName"}
     * @param [name] The name of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnName"}
     * @param [name] The name of the frame column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(path)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> = frameColumn<C>(path)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColKPropertyDataFrame")
    public fun <C> frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> = frameColumn(property)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the frame column.
     */
    public fun <C> frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> = frameColumn(property)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnName"}
     * @param [name] The name of the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun ColumnGroupReference.frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnName"}
     * @param [name] The name of the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnGroupReference.frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUntyped")
    public fun ColumnGroupReference.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
        frameColumn<Any?>(path)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] "columnGroup"["columnName"]}
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnGroupReference.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        frameColumn<C>(path)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColKPropertyDataFrame")
    public fun <C> ColumnGroupReference.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        frameColumn(property)

    /**
     * @include [CommonFrameColDocs] {@arg [CommonFrameColDocs.Arg] Type::columnName}
     * @param [property] The [KProperty] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    public fun <C> ColumnGroupReference.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        frameColumn(property)

    // endregion

    // endregion

    // region cols

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet]) from the current [ColumnSet].
     *
     * If the current [ColumnSet] is a [SingleColumn] and consists of a [column group][ColumnGroup],
     * then `cols` will create a subset of its children.
     *
     * You can use either a [ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][AccessApi] (+ [ColumnPath]).
     *
     * Aside from calling [cols] directly, you can also use the [get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][DataFrame.remove]` { `[cols][cols]` { it.`[hasNulls][DataColumn.hasNulls]`() } }`
     *
     * `df.`[select][DataFrame.select]` { myGroupCol.`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`1, 3, 5`[`]`][cols]` }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonColsDocs.Examples]}
     *
     */
    private interface CommonColsDocs {

        /**
         * @include [CommonColsDocs]
         *
         * {@include [LineBreak]}
         * NOTE: If used with a [predicate\], `cols` functions exactly like [filter][SingleColumn.filter].
         *
         * @param [predicate\] A [ColumnFilter function][ColumnFilter] that takes a [ColumnReference] and returns a [Boolean].
         * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing the columns that match the given [predicate\].
         * @see [filter\]
         */
        interface Predicate

        /**
         * @include [CommonColsDocs]
         *
         * @param [firstCol\] A {@includeArg [AccessorType]} that points to a column.
         * @param [otherCols\] Optional additional {@includeArg [AccessorType]}s that point to columns.
         * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing the columns that [firstCol\] and [otherCols\] point to.
         */
        interface Vararg {

            interface AccessorType
        }

        /** Example argument */
        interface Examples
    }

    // region predicate

    /**
     * @include [CommonColsDocs.Predicate]
     * @arg [CommonColsDocs.Examples]
     *
     * `// although these can be shortened to just the `[colsOf][colsOf]` call`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `// identity call, same as `[all][all]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`() }`
     *
     * @see [all\]
     */
    private interface ColumnSetColsPredicateDocs

    /** @include [ColumnSetColsPredicateDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        predicate: ColumnFilter<C> = { true },
    ): TransformableColumnSet<C> = colsInternal(predicate as ColumnFilter<*>) as TransformableColumnSet<C>

    /** @include [ColumnSetColsPredicateDocs] */
    public operator fun <C> ColumnSet<C>.get(
        predicate: ColumnFilter<C> = { true },
    ): TransformableColumnSet<C> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() }.`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][cols]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`.[cols][cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][cols]`() } // same as `[all][all]
     *
     * `// NOTE: there's a `[DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][cols]`{ ... }`[`]`][cols]` }`
     * {@include [LineBreak]}
     * NOTE: On a [SingleColumn], [cols][SingleColumn.cols] behaves exactly the same as
     * [children][SingleColumn.children].
     *
     * @see [all\]
     * @see [children\]
     */
    private interface SingleColumnAnyRowColsPredicateDocs

    /** @include [SingleColumnAnyRowColsPredicateDocs] */
    public fun SingleColumn<DataRow<*>>.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = ensureIsColGroup().colsInternal(predicate)

    /**
     * @include [SingleColumnAnyRowColsPredicateDocs]
     * {@comment this function is shadowed by [DataFrame.get]}
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[cols][cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol"`[`[`][cols]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `// same as `[all][all]
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[cols][cols]`() }`
     */
    private interface StringColsPredicateDocs

    /** @include [StringColsPredicateDocs] */
    public fun String.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = colGroup(this).cols(predicate)

    /** @include [StringColsPredicateDocs] */
    public operator fun String.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[cols][cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup`[`[`][cols]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `// identity call, same as `[all][all]
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[cols][cols]`() }`
     *
     * @see [all\]
     */
    private interface KPropertyColsPredicateDocs

    /** @include [KPropertyColsPredicateDocs] */
    public fun KProperty<DataRow<*>>.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = colGroup(this).cols(predicate)

    /** @include [KPropertyColsPredicateDocs] */
    public operator fun KProperty<DataRow<*>>.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[cols][cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][cols]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[cols][cols]`() } // identity call, same as `[all][all]
     */
    private interface ColumnPathPredicateDocs

    /** @include [ColumnPathPredicateDocs] */
    public fun ColumnPath.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = colGroup(this).cols(predicate)

    /** @include [ColumnPathPredicateDocs] */
    public operator fun ColumnPath.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    // endregion

    // region references

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     */
    private interface ColumnSetColsVarargColumnReferenceDocs

    /** @include [ColumnSetColsVarargColumnReferenceDocs] */
    public fun <C> ColumnSet<C>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = transformWithContext {
        dataFrameOf(it)
            .asColumnGroup()
            .cols(firstCol, *otherCols)
            .resolve(this)
    }

    /** @include [ColumnSetColsVarargColumnReferenceDocs] */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][cols]`(columnA, columnB) }`
     *
     * `// NOTE: there's a `[DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     */
    private interface SingleColumnColsVarargColumnReferenceDocs

    /** @include [SingleColumnColsVarargColumnReferenceDocs] */
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { refs ->
        ensureIsColGroup().asColumnSet().transform {
            it.flatMap { col -> refs.mapNotNull { col.getChild(it) } }
        }
    }

    /**
     * @include [SingleColumnColsVarargColumnReferenceDocs]
     * {@comment this function is shadowed by [DataFrame.get] for accessors}
     */
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     */
    private interface StringColsVarargColumnReferenceDocs

    /** @include [StringColsVarargColumnReferenceDocs] */
    public fun <C> String.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargColumnReferenceDocs] */
    public operator fun <C> String.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     */
    private interface KPropertyColsVarargColumnReferenceDocs

    /** @include [KPropertyColsVarargColumnReferenceDocs] */
    public fun <C> KProperty<DataRow<*>>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertyColsVarargColumnReferenceDocs] */
    public operator fun <C> KProperty<DataRow<*>>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     */
    private interface ColumnPathColsVarargColumnReferenceDocs

    /** @include [ColumnPathColsVarargColumnReferenceDocs] */
    public fun <C> ColumnPath.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargColumnReferenceDocs] */
    public operator fun <C> ColumnPath.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region names

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     */
    private interface ColumnSetColsVarargStringDocs

    /** @include [ColumnSetColsVarargStringDocs] */
    public fun <C> ColumnSet<C>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { names ->
        filter { it.name in names }
    }

    /**
     * @include [ColumnSetColsVarargStringDocs]
     */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][cols]`("columnA", "columnB") }`
     *
     * `// NOTE: there's a `[DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     */
    private interface SingleColumnColsVarargStringDocs

    /** @include [SingleColumnColsVarargStringDocs] */
    public fun SingleColumn<DataRow<*>>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = headPlusArray(firstCol, otherCols).let { names ->
        ensureIsColGroup().asColumnSet().transform { it.flatMap { col -> names.mapNotNull { col.getChild(it) } } }
    }

    /**
     * @include [SingleColumnColsVarargStringDocs]
     * {@comment this function is shadowed by [DataFrame.get] for accessors}
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "columnGroup".`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { "columnGroup"`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     */
    private interface StringColsVarargStringDocs

    /** @include [StringColsVarargStringDocs] */
    public fun String.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargStringDocs] */
    public operator fun String.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     */
    private interface KPropertiesColsVarargStringDocs

    /** @include [KPropertiesColsVarargStringDocs] */
    public fun KProperty<DataRow<*>>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertiesColsVarargStringDocs] */
    public operator fun KProperty<DataRow<*>>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     */
    private interface ColumnPathColsVarargStringDocs

    /** @include [ColumnPathColsVarargStringDocs] */
    public fun ColumnPath.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargStringDocs] */
    public operator fun ColumnPath.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    // endregion

    // region properties

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     */
    private interface ColumnSetColsVarargKPropertyDocs

    /** @include [ColumnSetColsVarargKPropertyDocs] */
    public fun <C> ColumnSet<C>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).map { it.name }.let { names ->
        filter { it.name in names }
    }

    /** @include [ColumnSetColsVarargKPropertyDocs] */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     */
    private interface SingleColumnColsVarargKPropertyDocs

    /** @include [SingleColumnColsVarargKPropertyDocs] */
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { props ->
        ensureIsColGroup().asColumnSet().transform { it.flatMap { col -> props.mapNotNull { col.getChild(it) } } }
    }

    /** @include [SingleColumnColsVarargKPropertyDocs] */
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     */
    private interface StringColsVarargKPropertyDocs

    /** @include [StringColsVarargKPropertyDocs] */
    public fun <C> String.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargKPropertyDocs] */
    public operator fun <C> String.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     */
    private interface KPropertyColsVarargKPropertyDocs

    /** @include [KPropertyColsVarargKPropertyDocs] */
    public fun <C> KProperty<DataRow<*>>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertyColsVarargKPropertyDocs] */
    public operator fun <C> KProperty<DataRow<*>>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@arg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @arg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     */
    private interface ColumnPathColsVarargKPropertyDocs

    /** @include [ColumnPathColsVarargKPropertyDocs] */
    public fun <C> ColumnPath.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargKPropertyDocs] */
    public operator fun <C> ColumnPath.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region indices

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves multiple columns in the form of a [ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * If called on a [SingleColumn], [ColumnGroup], or [DataFrame], the function will take the children found at the
     * given indices.
     * Else, if called on a normal [ColumnSet], the function will return a new [ColumnSet] with the columns found at
     * the given indices in the set.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols\]`(1, 3, 2) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][SingleColumn.get\]`5, 1, 2`[`]`][SingleColumn.get\]` }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][String.cols\]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonColsIndicesDocs.ExampleArg]}
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex\] The index of the first column to retrieve.
     * @param [otherIndices\] The other indices of the columns to retrieve.
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsIndicesDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonColsIndicesDocs]
     * @arg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[cols][ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[select][DataFrame.select]` { `[all][all]`()`[`[`][ColumnSet.cols]`5, 1`[`]`][ColumnSet.cols]` }`
     */
    private interface ColumnSetColsIndicesDocs

    /** @include [ColumnSetColsIndicesDocs] */
    public fun <C> ColumnSet<C>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<C> = colsInternal(headPlusArray(firstIndex, otherIndices)) as ColumnSet<C>

    /** @include [ColumnSetColsIndicesDocs] */
    public operator fun <C> ColumnSet<C>.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<C> = cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @arg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][SingleColumn.cols]`5, 0`[`]`][SingleColumn.cols]` }`
     *
     * `// NOTE: There's a `[ColumnGroup.get][ColumnGroup.get]` overload that prevents this from working as expected here:`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`5, 6`[`]`][SingleColumn.cols]` }`
     */
    private interface SingleColumnColsIndicesDocs

    /** @include [SingleColumnColsIndicesDocs] */
    public fun SingleColumn<DataRow<*>>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = ensureIsColGroup().colsInternal(headPlusArray(firstIndex, otherIndices))

    /**
     * {@comment this function is shadowed by [ColumnGroup.get] for accessors}
     * @include [SingleColumnColsIndicesDocs]
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @arg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][String.cols]`(5, 3, 1) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"`[`[`][String.cols]`0, 3`[`]`][String.cols]` }`
     */
    private interface StringColsIndicesDocs

    /** @include [StringColsIndicesDocs] */
    public fun String.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = colGroup(this).cols(firstIndex, *otherIndices)

    /** @include [StringColsIndicesDocs] */
    public operator fun String.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @arg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][KProperty.cols]`(5, 4) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup`[`[`][KProperty.cols]`0, 3`[`]`][KProperty.cols]` }`
     */
    private interface KPropertyColsIndicesDocs

    /** @include [KPropertyColsIndicesDocs] */
    public fun KProperty<DataRow<*>>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = colGroup(this).cols(firstIndex, *otherIndices)

    /** @include [KPropertyColsIndicesDocs] */
    public operator fun KProperty<DataRow<*>>.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @arg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[col][ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"]`[`[`][ColumnPath.cols]`5, 6`[`]`][ColumnPath.cols]` }`
     */
    private interface ColumnPathColsIndicesDocs

    /** @include [ColumnPathColsIndicesDocs] */
    public fun ColumnPath.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = colGroup(this).cols(firstIndex, *otherIndices)

    /** @include [ColumnPathColsIndicesDocs] */
    public operator fun ColumnPath.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    // endregion

    // region ranges

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns in the form of a [ColumnSet] by a [range\] of indices.
     * If any of the indices in the [range\] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * If called on a [SingleColumn], [ColumnGroup], or [DataFrame], the function will take the children found at the
     * given indices.
     * Else, if called on a normal [ColumnSet], the function will return a new [ColumnSet] with the columns found at
     * the given indices in the set.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]`(1`[..][Int.rangeTo]`3) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][ColumnSet.cols]`1`[..][Int.rangeTo]`5`[`]`][ColumnSet.cols]` }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[col][ColumnSet.cols]`(0`[..][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonColsRangeDocs.ExampleArg]}
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range\] are out of bounds.
     * @throws [IllegalArgumentException] if the [range\] is empty.
     * @param [range\] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsRangeDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonColsRangeDocs]
     * @arg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[cols][ColumnSet.cols]`(1`[..][Int.rangeTo]`3) }`
     *
     * `df.`[select][DataFrame.select]` { `[all][all]`()`[`[`][ColumnSet.cols]`1`[..][Int.rangeTo]`5`[`]`][ColumnSet.cols]` }`
     */
    private interface ColumnSetColsRangeDocs

    /** @include [ColumnSetColsRangeDocs] */
    public fun <C> ColumnSet<C>.cols(range: IntRange): ColumnSet<C> = colsInternal(range) as ColumnSet<C>

    /** @include [ColumnSetColsRangeDocs] */
    public operator fun <C> ColumnSet<C>.get(range: IntRange): ColumnSet<C> = cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @arg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]`(1`[..][Int.rangeTo]`3) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][SingleColumn.cols]`0`[..][Int.rangeTo]`5`[`]`][SingleColumn.cols]` }`
     *
     * `// NOTE: There's a `[ColumnGroup.get][ColumnGroup.get]` overload that prevents this from working as expected here:`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`5`[..][Int.rangeTo]`6`[`]`][SingleColumn.cols]` }`
     */
    private interface SingleColumnColsRangeDocs

    /** @include [SingleColumnColsRangeDocs] */
    public fun SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<*> = ensureIsColGroup().colsInternal(range)

    /**
     * {@comment this function is shadowed by [ColumnGroup.get] for accessors}
     * @include [SingleColumnColsRangeDocs]
     */
    public operator fun SingleColumn<DataRow<*>>.get(range: IntRange): ColumnSet<*> = cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @arg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[cols][String.cols]`(1`[..][Int.rangeTo]`3) }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup"`[`[`][String.cols]`0`[..][Int.rangeTo]`5`[`]`][String.cols]` }`
     */
    private interface StringColsRangeDocs

    /** @include [StringColsRangeDocs] */
    public fun String.cols(range: IntRange): ColumnSet<*> = colGroup(this).cols(range)

    /** @include [StringColsRangeDocs] */
    public operator fun String.get(range: IntRange): ColumnSet<*> = cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @arg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][KProperty.cols]`(1`[..][Int.rangeTo]`3) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup`[`[`][KProperty.cols]`0`[..][Int.rangeTo]`5`[`]`][KProperty.cols]` }`
     */
    private interface KPropertyColsRangeDocs

    /** @include [KPropertyColsRangeDocs] */
    public fun KProperty<DataRow<*>>.cols(range: IntRange): ColumnSet<*> = colGroup(this).cols(range)

    /** @include [KPropertyColsRangeDocs] */
    public operator fun KProperty<DataRow<*>>.get(range: IntRange): ColumnSet<*> = cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @arg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[col][ColumnPath.cols]`(0`[..][Int.rangeTo]`1) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"]`[`[`][ColumnPath.cols]`0`[..][Int.rangeTo]`5`[`]`][ColumnPath.cols]` }`
     */
    private interface ColumnPathColsRangeDocs

    /** @include [ColumnPathColsRangeDocs] */
    public fun ColumnPath.cols(range: IntRange): ColumnSet<*> = colGroup(this).cols(range)

    /** @include [ColumnPathColsRangeDocs] */
    public operator fun ColumnPath.get(range: IntRange): ColumnSet<*> = cols(range)

    /**
     * ## Columns by Index Range from List of Columns
     * Helper function to create a [ColumnSet] from a list of columns by specifying a range of indices.
     *
     * {@comment TODO find out usages of this function and add examples}
     */
    public operator fun <C> List<DataColumn<C>>.get(range: IntRange): ColumnSet<C> =
        ColumnsList(subList(range.first, range.last + 1))

    // endregion

    // endregion

    // region valueCols

    /**
     * ## Value Columns
     * Creates a subset of columns that are [ValueColumns][ValueColumn] from the current [ColumnSet].
     *
     * If the current [ColumnSet] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [valueCols] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [valueCols] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]`().`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonValueColsDocs.ExampleArg]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the value columns by.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] of [ValueColumns][ValueColumn].
     */
    private interface CommonValueColsDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonValueColsDocs]
     * @arg [CommonValueColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[valueCols][ColumnSet.valueCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnSet<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        valueColumnsInternal(filter)

    /**
     * @include [CommonValueColsDocs]
     * @arg [CommonValueColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCols][SingleColumn.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun SingleColumn<DataRow<*>>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        ensureIsColGroup().valueColumnsInternal(filter)

    /**
     * @include [CommonValueColsDocs]
     * @arg [CommonValueColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]`() }`
     */
    public fun String.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        colGroup(this).valueCols(filter)

    /**
     * @include [CommonValueColsDocs]
     * @arg [CommonValueColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[valueCols][KProperty.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[valueCols][KProperty.valueCols]`() }`
     */
    public fun KProperty<DataRow<*>>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        colGroup(this).valueCols(filter)

    /**
     * @include [CommonValueColsDocs]
     * @arg [CommonValueColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[valueCols][ColumnPath.valueCols]`().`[recursively][TransformableColumnSet.recursively]`() }`
     */
    public fun ColumnPath.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        colGroup(this).valueCols(filter)

    // endregion

    // region colGroups

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun ColumnSet<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun SingleColumn<DataRow<*>>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        ensureIsColGroup().columnGroupsInternal(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun String.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        colGroup(this).groups(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun KProperty<DataRow<*>>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        colGroup(this).groups(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun ColumnPath.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        colGroup(this).groups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][ColumnGroup] from the current [ColumnSet].
     *
     * If the current [ColumnSet] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [colGroups] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]`().`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonColGroupsDocs.ExampleArg]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the column groups by.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] of [ColumnGroups][ColumnGroup].
     */
    private interface CommonColGroupsDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonColGroupsDocs]
     * @arg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnSet.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colGroups][ColumnSet.colGroups]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnSet<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @arg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        ensureIsColGroup().columnGroupsInternal(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @arg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     */
    public fun String.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        colGroup(this).colGroups(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @arg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[colGroups][KProperty.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[colGroups][KProperty.colGroups]`() }`
     */
    public fun KProperty<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        colGroup(this).colGroups(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @arg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colGroups][ColumnPath.colGroups]`().`[recursively][TransformableColumnSet.recursively]`() }`
     */
    public fun ColumnPath.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        colGroup(this).colGroups(filter)

    // endregion

    // region frameCols

    /**
     * ## Frame Columns
     * Creates a subset of columns that are [FrameColumns][FrameColumn] from the current [ColumnSet].
     *
     * If the current [ColumnSet] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [frameCols] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [frameCols] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][SingleColumn.frameCols]`().`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonFrameColsDocs.ExampleArg]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the frame columns by.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] of [FrameColumns][FrameColumn].
     */
    private interface CommonFrameColsDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonFrameColsDocs]
     * @arg [CommonFrameColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[frameCols][ColumnSet.frameCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnSet<*>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        frameColumnsInternal(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @arg [CommonFrameColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[frameCols][SingleColumn.frameCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[frameCols][SingleColumn.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        ensureIsColGroup().frameColumnsInternal(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @arg [CommonFrameColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[frameCols][String.frameCols]`() }`
     */
    public fun String.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        colGroup(this).frameCols(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @arg [CommonFrameColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[frameCols][KProperty.frameCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[frameCols][KProperty.frameCols]`() }`
     */
    public fun KProperty<DataRow<*>>.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        colGroup(this).frameCols(filter)

    /**
     * @include [CommonFrameColsDocs]
     * @arg [CommonFrameColsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[frameCols][ColumnPath.frameCols]`().`[recursively][TransformableColumnSet.recursively]`() }`
     */
    public fun ColumnPath.frameCols(filter: Predicate<FrameColumn<*>> = { true }): TransformableColumnSet<DataFrame<*>> =
        colGroup(this).frameCols(filter)

    // endregion

    // region colsOfKind

    /** TODO tbd */
    public fun ColumnSet<*>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            predicate = predicate
        )

    /** TODO tbd */
    public fun SingleColumn<DataRow<*>>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        ensureIsColGroup().columnsOfKindInternal(
            kinds = headPlusArray(kind, others).toSet(),
            predicate = predicate
        )

    /** TODO tbd */
    public fun String.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        colGroup(this).colsOfKind(kind, *others, predicate = predicate)

    /** TODO tbd */
    public fun KProperty<DataRow<*>>.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        colGroup(this).colsOfKind(kind, *others, predicate = predicate)

    /** TODO tbd */
    public fun ColumnPath.colsOfKind(
        kind: ColumnKind,
        vararg others: ColumnKind,
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> =
        colGroup(this).colsOfKind(kind, *others, predicate = predicate)

    // endregion

    // region select

    /**
     * ## Select from [ColumnGroup]
     *
     * Perform a selection of columns using the {@include [ColumnsSelectionDslLink]} on
     * any [ColumnGroup]. This is more powerful than [SingleColumn.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][SingleColumn.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol" `[{][ColumnPath.select]` "colA" and `[expr][ColumnsSelectionDsl.expr]` { 0 } `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[select][ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][DataColumn.asColumnGroup]`().`[select][ColumnPath.select]` { "colA" and "colB" } }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonSelectDocs.ExampleArg]}
     *
     * @param [selector\] The [ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup] to select from.
     * @throws [IllegalArgumentException\] If [this\] is not a [ColumnGroup].
     * @return A [ColumnSet] containing the columns selected by [selector\].
     */
    private interface CommonSelectDocs {

        interface ExampleArg
    }

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][SingleColumn.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup `[{][ColumnPath.select]` colA `[and][SingleColumn.and]` colB `[}][ColumnPath.select]` }`
     */
    public fun <C, R> SingleColumn<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        ensureIsColGroup().let { singleColumn ->
            createColumnSet {
                singleColumn.resolveSingle(it)?.let { col ->
                    require(col.isColumnGroup()) {
                        "Column ${col.path} is not a ColumnGroup and can thus not be selected from."
                    }

                    col.asColumnGroup()
                        .getColumnsWithPaths(selector as ColumnsSelector<*, R>)
                        .map { it.changePath(col.path + it.path) }
                } ?: emptyList()
            }
        }

    /** @include [SingleColumn.select] */
    public operator fun <C, R> SingleColumn<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[select][SingleColumn.select]` { someCol `[and][SingleColumn.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup `[{][ColumnPath.select]` colA `[and][SingleColumn.and]` colB `[}][ColumnPath.select]` }`
     */
    public fun <C, R> KProperty<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        colGroup(this).select(selector)

    /** @include [KProperty.select] */
    public operator fun <C, R> KProperty<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][SingleColumn.select]` { someCol `[and][SingleColumn.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup" `[{][ColumnPath.select]` colA `[and][SingleColumn.and]` colB `[}][ColumnPath.select]` }`
     */
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        colGroup(this).select(selector)

    /** @include [String.select] */
    public operator fun <R> String.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][SingleColumn.select]` { someCol `[and][SingleColumn.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"] `[{][ColumnPath.select]` colA `[and][SingleColumn.and]` colB `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][SingleColumn.select]` { someCol `[and][SingleColumn.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup") { colA `[and][SingleColumn.and]` colB } }`
     */
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        colGroup(this).select(selector)

    /** @include [ColumnPath.select] */
    public operator fun <R> ColumnPath.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [ColumnPath.select]
     * {@comment Needed overload to allow for `pathOf("pathTo", "myColGroup") { colA and colB \\\\}` syntax.}
     */
    public fun <R> pathOf(vararg columnNames: String, selector: ColumnsSelector<*, R>): ColumnSet<R> =
        pathOf(*columnNames).select(selector)


    @Deprecated(
        message = "Nested select is reserved for ColumnsSelector/ColumnsSelectionDsl behavior. " +
            "Use myGroup.cols(\"col1\", \"col2\") to select columns by name from a ColumnGroup.",
        replaceWith = ReplaceWith("this.cols(*columns)"),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.select(vararg columns: String): ColumnSet<*> =
        select { columns.toColumnSet() }

    @Deprecated(
        message = "Nested select is reserved for ColumnsSelector/ColumnsSelectionDsl behavior. " +
            "Use myGroup.cols(col1, col2) to select columns by name from a ColumnGroup.",
        replaceWith = ReplaceWith("this.cols(*columns)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <R> SingleColumn<DataRow<*>>.select(vararg columns: ColumnReference<R>): ColumnSet<R> =
        select { columns.toColumnSet() }

    @Deprecated(
        message = "Nested select is reserved for ColumnsSelector/ColumnsSelectionDsl behavior. " +
            "Use myGroup.cols(Type::col1, Type::col2) to select columns by name from a ColumnGroup.",
        replaceWith = ReplaceWith("this.cols(*columns)"),
        level = DeprecationLevel.ERROR,
    )
    public fun <R> SingleColumn<DataRow<*>>.select(vararg columns: KProperty<R>): ColumnSet<R> =
        select { columns.toColumnSet() }

    // endregion

    // region dfs

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively(includeTopLevel = false)"),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> = dfsInternal(predicate)

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        ensureIsColGroup().asColumnSet().dfsInternal(predicate)

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun String.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        colGroup(this).dfs(predicate)

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> KProperty<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        colGroup(this).dfs(predicate)

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnPath.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        colGroup(this).dfs(predicate)

    // endregion

    // region allDfs

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols { includeGroups || !it.isColumnGroup() }.recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnSet<*>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        if (includeGroups) dfs { true } else dfs { !it.isColumnGroup() }

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols { includeGroups || !it.isColumnGroup() }.recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        if (includeGroups) dfs { true } else dfs { !it.isColumnGroup() }

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols { includeGroups || !it.isColumnGroup() }.recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun String.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        colGroup(this).allDfs(includeGroups)

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols { includeGroups || !it.isColumnGroup() }.recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun KProperty<DataRow<*>>.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        colGroup(this).allDfs(includeGroups)

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols { includeGroups || !it.isColumnGroup() }.recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnPath.allDfs(includeGroups: Boolean = false): ColumnSet<*> =
        colGroup(this).allDfs(includeGroups)

    /**
     * ## Recursively / Rec
     *
     * Modifies the previous call to run not only on the current column set,
     * but also on all columns inside [column groups][ColumnGroup].
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() }`
     *
     * returns all columns of type [String] in the top-level, as expected. However, what if you want ALL
     * columns of type [String] even if they are inside a nested [column group][ColumnGroup]? Then you can use [recursively]:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[recursively][recursively]`() }`
     *
     * This will return the columns of type [String] in all levels.
     *
     * More examples:
     *
     * `df.`[select][DataFrame.select]` { `[first][SingleColumn.first]` { col -> col.`[any][DataColumn.any]` { it == "Alice" } }.`[recursively][recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { "name" in it.`[name][ColumnReference.name]` }.`[rec][rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]`().`[recursively][recursively]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonRecursivelyDocs.Examples]}
     *
     * @param [includeTopLevel\] Whether to include the top-level columns in the result. `true` by default.
     * @see [DataFrame.flatten\]
     */
    private interface CommonRecursivelyDocs {

        /** Example argument */
        interface Examples
    }

    /**
     * @include [CommonRecursivelyDocs]
     * @arg [CommonRecursivelyDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[recursively][recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[all][SingleColumn.all]`().`[rec][rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[groups][SingleColumn.groups]`().`[recursively][recursively]`() }`
     */
    public fun <C> TransformableColumnSet<C>.recursively(): ColumnSet<C> =
        recursivelyImpl(includeTopLevel = true, includeGroups = true)

    /** @include [TransformableColumnSet.recursively] */
    public fun <C> TransformableColumnSet<C>.rec(): ColumnSet<C> = recursively()

    /**
     * @include [CommonRecursivelyDocs]
     * @arg [CommonRecursivelyDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[first][SingleColumn.first]` { col -> col.`[any][DataColumn.any]` { it == "Alice" } }.`[recursively][recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[single][SingleColumn.single]` { it.name == "myCol" }.`[rec][rec]`() }`
     */
    public fun TransformableSingleColumn<*>.recursively(): SingleColumn<*> =
        recursivelyImpl(includeTopLevel = true, includeGroups = true)

    /** @include [TransformableSingleColumn.recursively] */
    public fun TransformableSingleColumn<*>.rec(): SingleColumn<*> = recursively()

    // endregion

    // region all

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
     * This makes the function equivalent to [cols()][SingleColumn.cols].
     *
     * #### For example:
     * `df.`[move][DataFrame.move]` { `[all][SingleColumn.all]`().`[recursively][recursively]`() }.`[under][MoveClause.under]`("info")`
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
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { "a" in `[name][ColumnWithPath.name]` }.`[all][ColumnSet.all]`() }`
     * {@include [LineBreak]}
     * NOTE: This is an identity call and can be omitted in most cases. However, it can still prove useful
     * for readability or in combination with [recursively][TransformableColumnSet.recursively].
     */
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

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[all][String.all]`() }`
     */
    public fun String.all(): TransformableColumnSet<*> = colGroup(this).all()

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[all][KProperty.all]`() }`
     */
    public fun KProperty<DataRow<*>>.all(): TransformableColumnSet<*> = colGroup(this).all()

    /**
     * @include [CommonAllDocs]
     * @arg [CommonAllDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroup"].`[all][ColumnPath.all]`() }`
     */
    public fun ColumnPath.all(): TransformableColumnSet<*> = colGroup(this).all()

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
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[{@includeArg [FunctionArg]}][SingleColumn.{@includeArg [FunctionArg]}]`(someColumn) }`
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
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { .. }.`[allAfter][ColumnSet.allAfter]`({@includeArg [ColumnSetAllAfterDocs.Arg]}) }`
     */
    private interface ColumnSetAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllAfterDocs] {@arg [ColumnSetAllAfterDocs.Arg] "pathTo"["myColumn"]} */
    public fun <C> ColumnSet<C>.allAfter(column: ColumnPath): ColumnSet<C> {
        var take = false
        return cols {
            if (take) {
                true
            } else {
                take = column == it.path
                false
            }
        }
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
    public fun String.allAfter(column: ColumnPath): ColumnSet<*> = colGroup(this).allAfter(column)

    /** @include [StringAllAfterDocs] {@arg [StringAllAfterDocs.Arg] "myColumn"} */
    public fun String.allAfter(column: String): ColumnSet<*> = colGroup(this).allAfter(column)

    /** @include [StringAllAfterDocs] {@arg [StringAllAfterDocs.Arg] myColumn} */
    public fun String.allAfter(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allAfter(column)

    /** @include [StringAllAfterDocs] {@arg [StringAllAfterDocs.Arg] Type::myColumn} */
    public fun String.allAfter(column: KProperty<*>): ColumnSet<*> = colGroup(this).allAfter(column)

    /**
     * @include [AllAfterDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { SomeType::myColGroup.`[allAfter][KProperty.allAfter]`({@includeArg [KPropertyAllAfterDocs.Arg]}) }`
     */
    private interface KPropertyAllAfterDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allAfter(column: ColumnPath): ColumnSet<*> =
        colGroup(this).allAfter(column)

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allAfter(column: String): ColumnSet<*> = colGroup(this).allAfter(column)

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allAfter(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allAfter(column)

    /** @include [KPropertyAllAfterDocs] {@arg [KPropertyAllAfterDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allAfter(column: KProperty<*>): ColumnSet<*> =
        colGroup(this).allAfter(column)

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
        colGroup(this).allAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@arg [ColumnPathAllAfterDocs.Arg] "myColumn"} */
    public fun ColumnPath.allAfter(column: String): ColumnSet<*> =
        colGroup(this).allAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@arg [ColumnPathAllAfterDocs.Arg] myColumn} */
    public fun ColumnPath.allAfter(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allAfter(column)

    /** @include [ColumnPathAllAfterDocs] {@arg [ColumnPathAllAfterDocs.Arg] Type::myColumn} */
    public fun ColumnPath.allAfter(column: KProperty<*>): ColumnSet<*> =
        colGroup(this).allAfter(column)

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
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { .. }.`[allFrom][ColumnSet.allFrom]`({@includeArg [ColumnSetAllFromDocs.Arg]}) }`
     */
    private interface ColumnSetAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllFromDocs] {@arg [ColumnSetAllFromDocs.Arg] "pathTo"["myColumn"]} */
    public fun <C> ColumnSet<C>.allFrom(column: ColumnPath): ColumnSet<C> {
        var take = false
        return cols {
            if (take) {
                true
            } else {
                take = column == it.path
                take
            }
        }
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
    public fun String.allFrom(column: ColumnPath): ColumnSet<*> = colGroup(this).allFrom(column)

    /** @include [StringAllFromDocs] {@arg [StringAllFromDocs.Arg] "myColumn"} */
    public fun String.allFrom(column: String): ColumnSet<*> = colGroup(this).allFrom(column)

    /** @include [StringAllFromDocs] {@arg [StringAllFromDocs.Arg] myColumn} */
    public fun String.allFrom(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allFrom(column)

    /** @include [StringAllFromDocs] {@arg [StringAllFromDocs.Arg] Type::myColumn} */
    public fun String.allFrom(column: KProperty<*>): ColumnSet<*> = colGroup(this).allFrom(column)

    /**
     * @include [AllFromDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { SomeType::someColGroup.`[allFrom][KProperty.allFrom]`({@includeArg [KPropertyAllFromDocs.Arg]}) }`
     */
    private interface KPropertyAllFromDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allFrom(column: ColumnPath): ColumnSet<*> =
        colGroup(this).allFrom(column)

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allFrom(column: String): ColumnSet<*> = colGroup(this).allFrom(column)

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allFrom(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allFrom(column)

    /** @include [KPropertyAllFromDocs] {@arg [KPropertyAllFromDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allFrom(column: KProperty<*>): ColumnSet<*> =
        colGroup(this).allFrom(column)

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
    public fun ColumnPath.allFrom(column: ColumnPath): ColumnSet<*> = colGroup(this).allFrom(column)

    /** @include [ColumnPathAllFromDocs] {@arg [ColumnPathAllFromDocs.Arg] "myColumn"} */
    public fun ColumnPath.allFrom(column: String): ColumnSet<*> = colGroup(this).allFrom(column)

    /** @include [ColumnPathAllFromDocs] {@arg [ColumnPathAllFromDocs.Arg] myColumn} */
    public fun ColumnPath.allFrom(column: AnyColumnReference): ColumnSet<*> = colGroup(this).allFrom(column)

    /** @include [ColumnPathAllFromDocs] {@arg [ColumnPathAllFromDocs.Arg] Type::myColumn} */
    public fun ColumnPath.allFrom(column: KProperty<*>): ColumnSet<*> = colGroup(this).allFrom(column)

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
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { .. }.`[allBefore][ColumnSet.allBefore]`({@includeArg [ColumnSetAllBeforeDocs.Arg]}) }`
     */
    private interface ColumnSetAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllBeforeDocs] {@arg [ColumnSetAllBeforeDocs.Arg] "pathTo"["myColumn"]} */
    public fun <C> ColumnSet<C>.allBefore(column: ColumnPath): ColumnSet<C> {
        var take = true
        return cols {
            if (!take) {
                false
            } else {
                take = column != it.path
                take
            }
        }
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
    public fun String.allBefore(column: ColumnPath): ColumnSet<*> = colGroup(this).allBefore(column)

    /** @include [StringAllBeforeDocs] {@arg [StringAllBeforeDocs.Arg] "myColumn"} */
    public fun String.allBefore(column: String): ColumnSet<*> = colGroup(this).allBefore(column)

    /** @include [StringAllBeforeDocs] {@arg [StringAllBeforeDocs.Arg] myColumn} */
    public fun String.allBefore(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allBefore(column)

    /** @include [StringAllBeforeDocs] {@arg [StringAllBeforeDocs.Arg] Type::myColumn} */
    public fun String.allBefore(column: KProperty<*>): ColumnSet<*> =
        colGroup(this).allBefore(column)

    /**
     * @include [AllBeforeDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { SomeType::someColGroup.`[allBefore][KProperty.allBefore]`({@includeArg [KPropertyAllBeforeDocs.Arg]}) }`
     */
    private interface KPropertyAllBeforeDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allBefore(column: ColumnPath): ColumnSet<*> =
        colGroup(this).allBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allBefore(column: String): ColumnSet<*> =
        colGroup(this).allBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allBefore(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allBefore(column)

    /** @include [KPropertyAllBeforeDocs] {@arg [KPropertyAllBeforeDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allBefore(column: KProperty<*>): ColumnSet<*> =
        colGroup(this).allBefore(column)

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
        colGroup(this).allBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@arg [ColumnPathAllBeforeDocs.Arg] "myColumn"} */
    public fun ColumnPath.allBefore(column: String): ColumnSet<*> =
        colGroup(this).allBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@arg [ColumnPathAllBeforeDocs.Arg] myColumn} */
    public fun ColumnPath.allBefore(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allBefore(column)

    /** @include [ColumnPathAllBeforeDocs] {@arg [ColumnPathAllBeforeDocs.Arg] Type::myColumn} */
    public fun ColumnPath.allBefore(column: KProperty<*>): ColumnSet<*> =
        colGroup(this).allBefore(column)

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
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { .. }.`[allUpTo][ColumnSet.allUpTo]`({@includeArg [ColumnSetAllUpToDocs.Arg]}) }`
     */
    private interface ColumnSetAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [ColumnSetAllUpToDocs] {@arg [ColumnSetAllUpToDocs.Arg] "pathTo"["myColumn"]} */
    public fun <C> ColumnSet<C>.allUpTo(column: ColumnPath): ColumnSet<C> {
        var take = true
        return cols {
            if (!take) {
                false
            } else {
                take = column != it.path
                true
            }
        }
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
    public fun String.allUpTo(column: ColumnPath): ColumnSet<*> = colGroup(this).allUpTo(column)

    /** @include [StringAllUpToDocs] {@arg [StringAllUpToDocs.Arg] "myColumn"} */
    public fun String.allUpTo(column: String): ColumnSet<*> = colGroup(this).allUpTo(column)

    /** @include [StringAllUpToDocs] {@arg [StringAllUpToDocs.Arg] myColumn} */
    public fun String.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allUpTo(column)

    /** @include [StringAllUpToDocs] {@arg [StringAllUpToDocs.Arg] Type::myColumn} */
    public fun String.allUpTo(column: KProperty<*>): ColumnSet<*> = colGroup(this).allUpTo(column)

    /**
     * @include [AllUpToDocs]
     * @arg [CommonAllSubsetDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { SomeType::someColGroup.`[allUpTo][KProperty.allUpTo]`({@includeArg [KPropertyAllUpToDocs.Arg]}) }`
     */
    private interface KPropertyAllUpToDocs {

        /** Example argument to use */
        interface Arg
    }

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] "pathTo"["myColumn"]} */
    public fun KProperty<DataRow<*>>.allUpTo(column: ColumnPath): ColumnSet<*> =
        colGroup(this).allUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] "myColumn"} */
    public fun KProperty<DataRow<*>>.allUpTo(column: String): ColumnSet<*> = colGroup(this).allUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] myColumn} */
    public fun KProperty<DataRow<*>>.allUpTo(column: AnyColumnReference): ColumnSet<*> =
        colGroup(this).allUpTo(column)

    /** @include [KPropertyAllUpToDocs] {@arg [KPropertyAllUpToDocs.Arg] Type::myColumn} */
    public fun KProperty<DataRow<*>>.allUpTo(column: KProperty<*>): ColumnSet<*> =
        colGroup(this).allUpTo(column)

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

    // endregion

    // region children

    /**
     * ## Children
     *
     * [Children][ColumnSet.children] is an interesting operations, since it behaves
     * slightly differently depending on what you call it on. It will return the "children"
     * adhering to the given (optional) [predicate\], however what "children" means depends
     * whether it's called on a [ColumnSet] or a [SingleColumn]:
     *
     * ### On a [SingleColumn]:
     * When called on a [SingleColumn] consisting of a [ColumnGroup], [children][SingleColumn.children] will return the (filtered) children of that
     * column group. This makes the function behave similarly to [all][SingleColumn.all] and exactly the same as
     * [cols][SingleColumn.cols].
     *
     * #### For example:
     *
     * To select some columns or "children" of `myColumnGroup`, you can do both:
     * - `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][SingleColumn.cols]` { it.`[name][DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][DataFrame.select]` { myColumnGroup.`[children][SingleColumn.children]` { it.`[name][DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     *
     * Similarly, to select _all_ columns or "children" of a [DataFrame], you can do:
     * - `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`() }`
     * - `df.`[select][DataFrame.select]` { `[children][SingleColumn.children]`() }`
     * - `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]`() }`
     *
     * ### On a [ColumnSet]:
     * When called on a [ColumnSet], [children][ColumnSet.children] will return the (filtered) children of all [ColumnGroups][ColumnGroup]
     * in that column set.
     *
     * #### For example:
     *
     * To get only the children of all column groups in a [DataFrame], you can do:
     * - `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]`().`[children][ColumnSet.children]`() }`
     * - `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`().`[children][ColumnSet.children]`() }`
     *
     * Similarly, you can take the children of all [column groups][ColumnGroup] in a [ColumnSet]:
     * - `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[children][ColumnSet.children]`() }`
     *
     * #### Examples of this overload:
     *
     * {@includeArg [ChildrenDocs.ExampleArg]}
     *
     * @see [cols\]
     * @see [all\]
     * @param [predicate\] An optional predicate to filter the children by.
     * @return A [TransformableColumnSet] containing the (filtered) children.
     */
    private interface ChildrenDocs {

        /** Example argument to use */
        interface ExampleArg
    }

    /**
     * @include [ChildrenDocs]
     * @arg [ChildrenDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { .. }.`[children][ColumnSet.children]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[DataRow][DataRow]`<MyGroupType>>().`[children][ColumnSet.children]`() }`
     */
    public fun ColumnSet<*>.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        transform { it.flatMap { it.children().filter { predicate(it) } } }

    /**
     * @include [ChildrenDocs]
     * @arg [ChildrenDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[children][SingleColumn.children]`().`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[children][SingleColumn.children]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun SingleColumn<DataRow<*>>.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        ensureIsColGroup().asColumnSet().colsInternal(predicate)

    /**
     * @include [ChildrenDocs]
     * @arg [ChildrenDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[children][SingleColumn.children]`().`[recursively][TransformableColumnSet.recursively]`() }`
     */
    public fun String.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        colGroup(this).children(predicate)

    /**
     * @include [ChildrenDocs]
     * @arg [ChildrenDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[children][SingleColumn.children]`().`[recursively][TransformableColumnSet.recursively]`() }`
     */
    public fun KProperty<DataRow<*>>.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        colGroup(this).children(predicate)

        /**
     * @include [ChildrenDocs]
     * @arg [ChildrenDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[children][SingleColumn.children]`().`[recursively][TransformableColumnSet.recursively]`() }`
     */
    public fun ColumnPath.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        colGroup(this).children(predicate)

    // endregion

    // region take, drop

    // region take
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    public fun SingleColumn<DataRow<*>>.take(n: Int): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().take(n) }

    public fun String.takeChildren(n: Int): ColumnSet<*> = colGroup(this).take(n)

    public fun KProperty<DataRow<*>>.takeChildren(n: Int): ColumnSet<*> = colGroup(this).take(n)

    public fun ColumnPath.takeChildren(n: Int): ColumnSet<*> = colGroup(this).take(n)

    // endregion

    // region takeLast

    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    public fun SingleColumn<DataRow<*>>.takeLast(n: Int = 1): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().takeLast(n) }

    public fun String.takeLastChildren(n: Int): ColumnSet<*> = colGroup(this).takeLast(n)

    public fun KProperty<DataRow<*>>.takeLastChildren(n: Int): ColumnSet<*> = colGroup(this).takeLast(n)

    public fun ColumnPath.takeLastChildren(n: Int): ColumnSet<*> = colGroup(this).takeLast(n)

    // endregion

    // region drop

    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }

    public fun SingleColumn<DataRow<*>>.drop(n: Int): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().drop(n) }

    public fun String.dropChildren(n: Int): ColumnSet<*> = colGroup(this).drop(n)

    public fun KProperty<DataRow<*>>.dropChildren(n: Int): ColumnSet<*> = colGroup(this).drop(n)

    public fun ColumnPath.dropChildren(n: Int): ColumnSet<*> = colGroup(this).drop(n)

    // endregion

    // region dropLast

    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }

    public fun SingleColumn<DataRow<*>>.dropLast(n: Int = 1): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().dropLast(n) }

    /**
     * {@comment Name change to avoid conflict with [List] and [String]}
     */
    public fun String.dropLastChildren(n: Int): ColumnSet<*> = colGroup(this).dropLast(n)

    public fun KProperty<DataRow<*>>.dropLastChildren(n: Int): ColumnSet<*> = colGroup(this).dropLast(n)

    public fun ColumnPath.dropLastChildren(n: Int): ColumnSet<*> = colGroup(this).dropLast(n)

    // endregion

    // region take while

    public fun <C> ColumnSet<C>.takeWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    public fun SingleColumn<DataRow<*>>.takeWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().takeWhile(predicate) }

    public fun <C> ColumnSet<C>.takeLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    public fun SingleColumn<DataRow<*>>.takeLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().takeLastWhile(predicate) }

    // endregion

    // endregion

    // region roots
    @Deprecated("Use roots() instead", ReplaceWith("roots()"))
    public fun <C> ColumnSet<C>.top(): ColumnSet<C> = roots()

    /**
     * ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn] containing a single [ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet] itself.
     *
     * {@comment TODO add helpful examples}
     */
    public fun <C> ColumnSet<C>.roots(): ColumnSet<C> = rootsInternal() as ColumnSet<C>

    /** @include [ColumnSet.roots] */
    public fun SingleColumn<DataRow<*>>.roots(): ColumnSet<*> = ensureIsColGroup().rootsInternal()

    /** @include [ColumnSet.roots] */
    public fun String.roots(): ColumnSet<*> = colGroup(this).roots()

    /** @include [ColumnSet.roots] */
    public fun KProperty<DataRow<*>>.roots(): ColumnSet<*> = colGroup(this).roots()

    /** @include [ColumnSet.roots] */
    public fun ColumnPath.roots(): ColumnSet<*> = colGroup(this).roots()

    // endregion

    // region filter

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.filter(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>) as TransformableColumnSet<C>

    public fun SingleColumn<DataRow<*>>.filter(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal(predicate)

    // endregion

    // region name filter

    public fun SingleColumn<DataRow<*>>.nameContains(text: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.contains(text) }

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(text: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.contains(text) } as TransformableColumnSet<C>

    public fun SingleColumn<DataRow<*>>.nameContains(regex: Regex): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.contains(regex) }

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): TransformableColumnSet<C> =
        colsInternal { it.name.contains(regex) } as TransformableColumnSet<C>

    public fun SingleColumn<DataRow<*>>.startsWith(prefix: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.startsWith(prefix) }

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.startsWith(prefix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.startsWith(prefix) } as TransformableColumnSet<C>

    public fun SingleColumn<DataRow<*>>.endsWith(suffix: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.endsWith(suffix) }

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.endsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    // endregion

    // region except

    public fun <C> ColumnSet<C>.except(vararg other: ColumnsResolver<*>): TransformableColumnSet<*> =
        except(other.toColumnSet())

    public fun SingleColumn<DataRow<*>>.except(vararg other: ColumnsResolver<*>): TransformableColumnSet<*> =
        ensureIsColGroup().asColumnSet().asColumnSet().except(*other)

    public fun <C> ColumnSet<C>.except(vararg other: String): TransformableColumnSet<*> = except(other.toColumnSet())

    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): TransformableColumnSet<*> =
        createTransformableColumnSet(
            resolver = { context ->
                this@except
                    .resolve(context)
                    .allColumnsExcept(other.resolve(context))
            },
            transformResolve = { context, transformer ->
                transformer.transform(this@except)
                    .resolve(context)
                    .allColumnsExcept(other.resolve(context))
            },
        )

    public infix fun <C> ColumnSet<C>.except(selector: ColumnsSelector<T, *>): TransformableColumnSet<C> =
        except(selector.toColumns()) as TransformableColumnSet<C>

    public infix fun SingleColumn<DataRow<*>>.except(selector: ColumnsSelector<T, *>): TransformableColumnSet<*> =
        ensureIsColGroup().except(selector.toColumns())

    // endregion

    // region without nulls

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C?>.withoutNulls(): TransformableColumnSet<C> =
        transform { it.filter { !it.hasNulls() } } as TransformableColumnSet<C>

    // endregion

    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        this(this@ColumnsSelectionDsl, this@ColumnsSelectionDsl)

    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)
    public infix fun <C> ColumnReference<C>.into(column: ColumnAccessor<*>): ColumnReference<C> = into(column.name())
    public infix fun <C> ColumnReference<C>.into(column: KProperty<*>): ColumnReference<C> = named(column.columnName)

    public infix fun String.into(newName: String): ColumnReference<Any?> = toColumnAccessor().into(newName)
    public infix fun String.into(column: ColumnAccessor<*>): ColumnReference<Any?> =
        toColumnAccessor().into(column.name())

    public infix fun String.into(column: KProperty<*>): ColumnReference<Any?> =
        toColumnAccessor().into(column.columnName)

    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)
    public infix fun <C> ColumnReference<C>.named(nameFrom: ColumnReference<*>): ColumnReference<C> =
        named(nameFrom.name)

    public infix fun <C> ColumnReference<C>.named(nameFrom: KProperty<*>): ColumnReference<C> =
        named(nameFrom.columnName)

    public infix fun String.named(newName: String): ColumnReference<Any?> = toColumnAccessor().named(newName)
    public infix fun String.named(nameFrom: ColumnReference<*>): ColumnReference<Any?> =
        toColumnAccessor().named(nameFrom.name)

    public infix fun String.named(nameFrom: KProperty<*>): ColumnReference<Any?> =
        toColumnAccessor().named(nameFrom.columnName)

    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    public infix fun <C> KProperty<C>.named(nameFrom: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameFrom.name)

    public infix fun <C> KProperty<C>.named(nameFrom: KProperty<*>): ColumnReference<C> =
        toColumnAccessor().named(nameFrom.columnName)

    // region and

    /**
     * ## And Operator
     * The [and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any {@include [AccessApiLink]}!
     *
     * ### Examples:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { "colA" `[and][String.and]` colB }`
     *
     * `df.`[select][DataFrame.select]` {`
     *
     * {@include [Indent]}[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * {@include [DoubleIndent]}[cols][SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[recursively][TransformableColumnSet.recursively]`()`
     *
     * {@include [Indent]}`}`
     *
     * `}`
     *
     * `df.`[select][DataFrame.select]` { "colC" `[and][String.and]` Type::colB `[and][KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * {@includeArg [CommonAndDocs.ExampleArg]}
     *
     * @return A [ColumnSet] that contains all the columns from the [ColumnsResolvers][ColumnsResolver] on the left
     *   and right side of the [and] operator.
     */
    private interface CommonAndDocs {

        interface ExampleArg
    }

    // TODO add docs `this { age } / it { age }`
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("invokeColumnsSelector")
    public operator fun <C> invoke(selection: ColumnsSelector<T, C>): ColumnsResolver<C> = selection()

    // region ColumnsResolver

    /**
     * @include [CommonAndDocs]
     * @arg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { ... } `[and][ColumnsResolver.and] {@includeArg [ColumnsResolverAndDocs.Argument]}` }`
     */
    private interface ColumnsResolverAndDocs {

        interface Argument
    }

    /** @include [ColumnsResolverAndDocs] {@arg [ColumnsResolverAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnsList(this, other)

    /** @include [ColumnsResolverAndDocs] {@arg [ColumnsResolverAndDocs.Argument] "colB"} */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** @include [ColumnsResolverAndDocs] {@arg [ColumnsResolverAndDocs.Argument] Type::colB} */
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    /** @include [ColumnsResolverAndDocs] {@arg [ColumnsResolverAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this and other()

    // endregion

    // region String

    /**
     * @include [CommonAndDocs]
     * @arg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "colA" `[and][String.and] {@includeArg [StringAndDocs.Argument]}` }`
     */
    private interface StringAndDocs {

        interface Argument
    }

    /** @include [StringAndDocs] {@arg [StringAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** @include [StringAndDocs] {@arg [StringAndDocs.Argument] "colB"} */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** @include [StringAndDocs] {@arg [StringAndDocs.Argument] Type::colB} */
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    /** @include [StringAndDocs] {@arg [StringAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> String.and(other: ColumnsSelector<T, C>): ColumnSet<*> = toColumnAccessor() and other()

    // endregion

    // region KProperty

    /**
     * @include [CommonAndDocs]
     * @arg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::colA `[and][KProperty.and] {@includeArg [KPropertyAndDocs.Argument]}` }`
     */
    private interface KPropertyAndDocs {

        interface Argument
    }

    /** @include [KPropertyAndDocs] {@arg [KPropertyAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@arg [KPropertyAndDocs.Argument] "colB"} */
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@arg [KPropertyAndDocs.Argument] Type::colB} */
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    /** @include [KPropertyAndDocs] {@arg [KPropertyAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> KProperty<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = toColumnAccessor() and other()

    // endregion

    // region ColumnsSelector

    /**
     * @include [CommonAndDocs]
     * @arg [CommonAndDocs.ExampleArg]
     *
     * `val intCols: `[ColumnsSelector][ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]` { intCols `[and][and] {@includeArg [ColumnsSelectorAndDocs.Argument]}` }`
     */
    private interface ColumnsSelectorAndDocs {

        interface Argument
    }

    /** @include [ColumnsSelectorAndDocs] {@arg [ColumnsSelectorAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsResolver<C>): ColumnSet<C> = this() and other

    /** @include [ColumnsSelectorAndDocs] {@arg [ColumnsSelectorAndDocs.Argument] "colB"} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: String): ColumnSet<*> = this() and other

    /** @include [ColumnsSelectorAndDocs] {@arg [ColumnsSelectorAndDocs.Argument] Type::colB} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: KProperty<C>): ColumnSet<C> = this() and other

    /** @include [ColumnsSelectorAndDocs] {@arg [ColumnsSelectorAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this() and other

    // endregion

    // endregion

    public fun <C> ColumnSet<C>.distinct(): ColumnSet<C> = DistinctColumnSet(this)

    @Deprecated(
        message = "Use recursively() instead",
        replaceWith = ReplaceWith(
            "this.colsOf(type, predicate).recursively()",
            "org.jetbrains.kotlinx.dataframe.columns.recursively",
            "org.jetbrains.kotlinx.dataframe.api.colsOf",
        ),
    )
    public fun <C> String.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
        colGroup(this).dfsOf(type, predicate)

    @Deprecated(
        message = "Use recursively() instead",
        replaceWith = ReplaceWith(
            "this.colsOf(type, predicate).recursively()",
            "org.jetbrains.kotlinx.dataframe.columns.recursively",
            "org.jetbrains.kotlinx.dataframe.api.colsOf",
        ),
    )
    public fun <C> KProperty<DataRow<*>>.dfsOf(
        type: KType,
        predicate: (ColumnWithPath<C>) -> Boolean = { true },
    ): ColumnSet<*> =
        colGroup(this).dfsOf(type, predicate)

    /**
     * @include [CommonColsOfDocs]
     * Get sub-columns of the column with this name by [type] with a [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> String.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> =
        colGroup(this).colsOf(type, filter)

    /**
     * @include [CommonColsOfDocs]
     * Get sub-columns of the column this [KProperty Accessor][KProperty] points to by [type] with or without [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> KProperty<DataRow<*>>.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> =
        colGroup(this).colsOf(type, filter)

    /**
     * @include [CommonColsOfDocs]
     * Get sub-columns of the column this [ColumnPath] points to by [type] with or without [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> ColumnPath.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> =
        colGroup(this).colsOf(type, filter)
}

/**
 * @include [ColumnExpression.CommonDocs]
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Will be empty by default.
 * @include [Infer.Param] By default: [Nulls][Infer.Nulls].
 * @param [expression] An [AddExpression] to define what each new row of the temporary column should contain.
 */
public inline fun <T, reified R> ColumnsSelectionDsl<T>.expr(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, infer, expression)

public fun <C> SingleColumn<C>.asColumnGroup(): SingleColumn<DataRow<C>> = this as SingleColumn<DataRow<C>>

public fun <C> KProperty<C>.asColumnGroup(): KProperty<DataRow<C>> = this as KProperty<DataRow<C>>

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> =
    { this@filter(it, it).asColumnSet().filter(predicate) }

/**
 * If this [ColumnsResolver] is a [SingleColumn], it
 * returns a new [ColumnSet] containing the children of this [SingleColumn] that
 * match the given [predicate].
 *
 * Else, it returns a new [ColumnSet] containing all columns in this [ColumnsResolver] that
 * match the given [predicate].
 */
internal fun ColumnsResolver<*>.colsInternal(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
    allColumnsInternal().transform { it.filter(predicate) }

internal fun ColumnsResolver<*>.colsInternal(indices: IntArray): TransformableColumnSet<*> =
    allColumnsInternal().transform { cols ->
        indices.map {
            try {
                cols[it]
            } catch (e: IndexOutOfBoundsException) {
                throw IndexOutOfBoundsException("Index $it is out of bounds for column set of size ${cols.size}")
            }
        }
    }

internal fun ColumnsResolver<*>.colsInternal(range: IntRange): TransformableColumnSet<*> =
    allColumnsInternal().transform {
        try {
            it.subList(range.first, range.last + 1)
        } catch (e: IndexOutOfBoundsException) {
            throw IndexOutOfBoundsException("Range $range is out of bounds for column set of size ${it.size}")
        }
    }

internal fun ColumnsResolver<*>.rootsInternal(): ColumnSet<*> =
    allColumnsInternal().transform { it.roots() }

internal fun ColumnsResolver<*>.valueColumnsInternal(filter: (ValueColumn<*>) -> Boolean): TransformableColumnSet<*> =
    colsInternal { it.isValueColumn() && filter(it.asValueColumn()) }

internal fun ColumnsResolver<*>.columnGroupsInternal(filter: (ColumnGroup<*>) -> Boolean): TransformableColumnSet<AnyRow> =
    colsInternal { it.isColumnGroup() && filter(it.asColumnGroup()) } as TransformableColumnSet<AnyRow>

internal fun ColumnsResolver<*>.frameColumnsInternal(filter: (FrameColumn<*>) -> Boolean): TransformableColumnSet<AnyFrame> =
    colsInternal { it.isFrameColumn() && filter(it.asFrameColumn()) } as TransformableColumnSet<AnyFrame>

internal fun ColumnsResolver<*>.columnsOfKindInternal(
    kinds: Set<ColumnKind>,
    predicate: ColumnFilter<*>,
): TransformableColumnSet<*> = colsInternal {
    it.kind() in kinds && predicate(it)
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

@Deprecated("Replaced with recursively()")
internal fun ColumnSet<*>.dfsInternal(predicate: (ColumnWithPath<*>) -> Boolean) =
    transform { it.filter { it.isColumnGroup() }.flatMap { it.children().flattenRecursively().filter(predicate) } }

@Deprecated(
    message = "Use recursively() instead",
    replaceWith = ReplaceWith(
        "this.colsOf(type, predicate).recursively()",
        "org.jetbrains.kotlinx.dataframe.columns.recursively",
        "org.jetbrains.kotlinx.dataframe.api.colsOf",
    ),
)
public fun <C> ColumnSet<*>.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
    dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

@Deprecated(
    message = "Use recursively() instead",
    replaceWith = ReplaceWith(
        "this.colsOf(type, predicate).recursively()",
        "org.jetbrains.kotlinx.dataframe.columns.recursively",
        "org.jetbrains.kotlinx.dataframe.api.colsOf",
    ),
)
public fun <C> SingleColumn<DataRow<*>>.dfsOf(
    type: KType,
    predicate: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<*> =
    ensureIsColGroup().asColumnSet().dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

@Deprecated(
    message = "Use recursively() instead",
    replaceWith = ReplaceWith(
        "this.colsOf<C>(filter).recursively(includeTopLevel = false)",
        "org.jetbrains.kotlinx.dataframe.columns.recursively",
        "org.jetbrains.kotlinx.dataframe.api.colsOf",
    ),
)
public inline fun <reified C> ColumnSet<*>.dfsOf(noinline filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<C> =
    dfsOf(typeOf<C>(), filter) as ColumnSet<C>

@Deprecated(
    message = "Use recursively() instead",
    replaceWith = ReplaceWith(
        "this.colsOf<C>(filter).recursively()",
        "org.jetbrains.kotlinx.dataframe.columns.recursively",
        "org.jetbrains.kotlinx.dataframe.api.colsOf",
    ),
)
public inline fun <reified C> SingleColumn<DataRow<*>>.dfsOf(noinline filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<C> =
    dfsOf(typeOf<C>(), filter) as ColumnSet<C>

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 * {@include [LineBreak]}
 * Alternatively, [colsOf] can also be called on existing columns:
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { (Type::myColumnGroup)().`[colsOf][colsOf]`<`[Double][Double]`>() }`
 * {@include [LineBreak]}
 * Finally, [colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 * {@comment TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325) }
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 */
internal interface ColsOf

/**
 * @include [ColsOf]
 * ## This Cols Of Overload
 */
private interface CommonColsOfDocs {

    /** @return A [ColumnSet] containing the columns of given type that were included by [filter\]. */
    interface Return

    /** @param [filter\] an optional filter function that takes a column of type [C\] and returns `true` if the column should be included. */
    interface FilterParam
}

/**
 * @include [CommonColsOfDocs]
 * Get (sub-)columns by [type] with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @include [CommonColsOfDocs.FilterParam]
 * @include [CommonColsOfDocs.Return]
 */
public fun <C> ColumnSet<*>.colsOf(
    type: KType,
    filter: (DataColumn<C>) -> Boolean = { true },
): TransformableColumnSet<C> =
    colsInternal { it.isSubtypeOf(type) && filter(it.cast()) } as TransformableColumnSet<C>

/**
 * @include [CommonColsOfDocs]
 * Get (sub-)columns by a given type with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * @include [CommonColsOfDocs.FilterParam]
 * @include [CommonColsOfDocs.Return]
 */
public inline fun <reified C> ColumnSet<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): TransformableColumnSet<C> =
    colsOf(typeOf<C>(), filter)

/**
 * @include [CommonColsOfDocs]
 * Get (sub-)columns by [type] with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @include [CommonColsOfDocs.FilterParam]
 * @include [CommonColsOfDocs.Return]
 */
public fun <C> SingleColumn<DataRow<*>>.colsOf(
    type: KType,
    filter: (DataColumn<C>) -> Boolean = { true },
): TransformableColumnSet<C> =
    ensureIsColGroup().colsInternal { it.isSubtypeOf(type) && filter(it.cast()) } as TransformableColumnSet<C>

/**
 * @include [CommonColsOfDocs]
 * Get (sub-)columns by a given type with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * @include [CommonColsOfDocs.FilterParam]
 * @include [CommonColsOfDocs.Return]
 */
public inline fun <reified C> SingleColumn<DataRow<*>>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): TransformableColumnSet<C> =
    colsOf(typeOf<C>(), filter)

/**
 * Checks the validity of this [SingleColumn],
 * by adding a check to see it's a [ColumnGroup] (so, a [SingleColumn]<[DataRow]<*>>)
 * and throwing an [IllegalArgumentException] if it's not.
 */
@Suppress("UNCHECKED_CAST")
internal fun SingleColumn<DataRow<*>>.ensureIsColGroup(): SingleColumn<DataRow<*>> =
    performCheck { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Attempted to perform a ColumnGroup operation on ${col?.kind()} ${col?.path}."
        }
    } as SingleColumn<DataRow<*>>

/* TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325)
context(ColumnsSelectionDsl)
public inline fun <reified C> KProperty<DataRow<*>>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<*> =
    colsOf(typeOf<C>(), filter)

context(ColumnsSelectionDsl)
public inline fun <reified C> String.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<*> =
    colsOf(typeOf<C>(), filter)

 */
