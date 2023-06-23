package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DoubleIndent
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.impl.columns.DistinctColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.getChildrenAt
import org.jetbrains.kotlinx.dataframe.impl.columns.getColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.performCheck
import org.jetbrains.kotlinx.dataframe.impl.columns.roots
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
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
     * [get][ColumnsContainer.get]`(MyType::myColumnGroup).`[asColumnGroup][KProperty.asColumnGroup]`().`[get][ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`()`[`[`][ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][ColumnsContainer.get].
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
     * Retrieves the value of the column with this name from the [DataFrame]. This can be
     * both typed and untyped.
     * This is a shorthand for [get][ColumnsContainer.get]`("myColumnName")` and can be
     * written as `"myColumnName"<MyColumnType>()` instead.
     *
     * @throws [IllegalArgumentException] if there is no column with this name.
     * @return The [DataColumn] with this name.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("stringInvokeTyped")
    public operator fun <C> String.invoke(): DataColumn<C> = getColumn(this).cast()

    /**
     * Retrieves the value of the column with this name from the [DataFrame]. This can be
     * both typed and untyped.
     * This is a shorthand for [get][ColumnsContainer.get]`("myColumnName")` and can be
     * written as `"myColumnName"()` instead.
     *
     * @throws [IllegalArgumentException] if there is no column with this name.
     * @return The [DataColumn] with this name.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("stringInvokeUntyped")
    public operator fun String.invoke(): DataColumn<*> = getColumn(this)

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

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> ColumnsSelectionDsl<T>.asSingleColumn(): SingleColumn<DataRow<T>> = this as SingleColumn<DataRow<T>>

/**
 * @include [CommonColumnSelectionDocs]
 *
 * Can be safely cast to [SingleColumn] across the library. It does not directly
 * implement it for DSL purposes.
 */
public interface ColumnsSelectionDsl<out T> :
    ColumnSelectionDsl<T>,

    FirstColumnsSelectionDsl,
    LastColumnsSelectionDsl,
    SingleColumnsSelectionDsl,

    ConstructorsColumnsSelectionDsl,

    ColsColumnsSelectionDsl,

    ValueColsColumnsSelectionDsl,
    ColGroupsColumnsSelectionDsl,
    FrameColsColumnsSelectionDsl,
    ColsOfKindColumnsSelectionDsl,

    AllColumnsSelectionDsl,

    // TODO due to String.invoke conflict this cannot be moved out
//    SelectColumnsSelectionDsl,

    RecursivelyColumnsSelectionDsl,
    ChildrenColumnsSelectionDsl,
    TakeColumnsSelectionDsl,
    DropColumnsSelectionDsl,
    ExceptColumnsSelectionDsl<T>,
    ColumnNameFiltersColumnsSelectionDsl,
    WithoutNullsColumnsSelectionDsl,

    FilterColumnsSelectionDsl {
    /*, SingleColumn<DataRow<T>> */

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
     * Creates an empty [ColumnsResolver], essentially selecting no columns at all.
     *
     * #### For example:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { `[none][none]`() }`
     *
     * @return An empty [ColumnsResolver].
     */
    public fun none(): ColumnsResolver<*> = ColumnsList<Any?>(emptyList())

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
     * `df.`[select][DataFrame.select]` { `[col][SingleColumn.col]`(3) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][SingleColumn.col]`5`[`]`][SingleColumn.col]` }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[col][String.col]`(0) }`
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
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[col][ColumnSet.col]`(0) }`
     *
     * `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`()`[`[`][ColumnSet.col]`5`[`]`][ColumnSet.col]` }`
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
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.col]`5`[`]`][SingleColumn.col]` }`
     */
    private interface SingleColumnColIndexDocs

    /** @include [SingleColumnColIndexDocs] */
    public fun SingleColumn<DataRow<*>>.col(index: Int): SingleColumn<*> =
        ensureIsColGroup().asColumnSet().getChildrenAt(index).singleImpl()

    /** @include [SingleColumnColIndexDocs] */
    public operator fun SingleColumn<DataRow<*>>.get(index: Int): SingleColumn<*> = col(index)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<*> =
        asSingleColumn().col(index)

    /** TODO */
    public operator fun ColumnsSelectionDsl<*>.get(index: Int): SingleColumn<*> = col(index)

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
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[col][SingleColumn.col]`(5) }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::myColumnGroup)`[`[`][SingleColumn.col]`0`[`]`][SingleColumn.col]` }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[col][KProperty.col]`(5) }`
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
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"]`[`[`][ColumnPath.col]`0`[`]`][ColumnPath.col]` }`
     */
    private interface ColumnPathIndexDocs

    /** @include [ColumnPathIndexDocs] */
    public fun ColumnPath.col(index: Int): SingleColumn<*> = colGroup(this).col(index)

    /** @include [ColumnPathIndexDocs] */
    public operator fun ColumnPath.get(index: Int): SingleColumn<*> = col(index)

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

    /** todo */
    public fun ColumnsSelectionDsl<*>.roots(): ColumnSet<*> = asSingleColumn().roots()

    /** @include [ColumnSet.roots] */
    public fun String.roots(): ColumnSet<*> = colGroup(this).roots()

    /** @include [ColumnSet.roots] */
    public fun KProperty<DataRow<*>>.roots(): ColumnSet<*> = colGroup(this).roots()

    /** @include [ColumnSet.roots] */
    public fun ColumnPath.roots(): ColumnSet<*> = colGroup(this).roots()

    // endregion

    // region select

    /**
     * ## Select from [ColumnGroup]
     *
     * Perform a selection of columns using the {@include [ColumnsSelectionDslLink]} on
     * any [ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol" `[{][String.select]` "colA" and `[expr][ColumnsSelectionDsl.expr]` { 0 } `[}][String.select]` }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[select][ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][DataColumn.asColumnGroup]`()`[() {][SingleColumn.select]` "colA" and "colB" `[}][SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonSelectDocs.ExampleArg]}
     *
     * {@include [LineBreak]}
     *
     * See also [except][ColumnsSelectionDsl.except]/[allExcept][ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector\] The [ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup] to select from.
     * @throws [IllegalArgumentException\] If [this\] is not a [ColumnGroup].
     * @return A [ColumnSet] containing the columns selected by [selector\].
     * @see [SingleColumn.except\]
     */
    private interface CommonSelectDocs {

        interface ExampleArg
    }

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup `[{][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][SingleColumn.select]` }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C, R> SingleColumn<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        createColumnSet { context ->
            this.ensureIsColGroup().resolveSingle(context)?.let { col ->
                require(col.isColumnGroup()) {
                    "Column ${col.path} is not a ColumnGroup and can thus not be selected from."
                }

                col.asColumnGroup()
                    .getColumnsWithPaths(selector as ColumnsSelector<*, R>)
                    .map { it.changePath(col.path + it.path) }
            } ?: emptyList()
        }

    /** @include [SingleColumn.select] */
    public operator fun <C, R> SingleColumn<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup)`[() `{`][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][SingleColumn.select]` }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[select][SingleColumn.select]` { colA `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[select][KProperty.select]` { colA `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup `[`{`][KProperty.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][KProperty.select]` }`
     */
    public fun <C, R> KProperty<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** @include [KProperty.select] */
    public operator fun <C, R> KProperty<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][String.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup" `[{][String.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][String.select]` }`
     */
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** @include [String.select] */
    public operator fun <R> String.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [CommonSelectDocs]
     * @arg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"] `[{][ColumnPath.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup")`[() {][ColumnPath.select]` someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[}][ColumnPath.select]` }`
     */
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** @include [ColumnPath.select] */
    public operator fun <R> ColumnPath.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

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

    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        this(this@ColumnsSelectionDsl, this@ColumnsSelectionDsl)

    // region rename

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][ColumnReference.named] or [into][ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of {@include [AccessApiLink]} can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]` { name `[named][ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][DataFrame.select]` { `[expr][expr]` { 0 } `[into][ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][DataFrame.select]` { "colA" `[named][String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { {@includeArg [CommonRenameDocs.ReceiverArg]} `[{@includeArg [CommonRenameDocs.FunctionNameArg]}][{@includeArg [CommonRenameDocs.ReceiverTypeArg]}.{@includeArg [CommonRenameDocs.FunctionNameArg]}]` {@includeArg [CommonRenameDocs.ParamArg]} }`
     *
     * @receiver The [{@includeArg [ReceiverTypeArg]}] referencing the column to rename.
     * @param [{@includeArg [ParamNameArg]}\] A [{@includeArg [ParamTypeArg]}\] used to specify the new name of the column.
     * @return A [ColumnReference] to the renamed column.
     */
    private interface CommonRenameDocs {

        interface ReceiverArg

        interface ReceiverTypeArg

        /** "named" or "into" */
        interface FunctionNameArg

        /** "newName" or "nameOf" */
        interface ParamNameArg
        interface ParamArg

        interface ParamTypeArg

        /**
         * @arg [ReceiverArg] columnA
         * @arg [ReceiverTypeArg] ColumnReference
         */
        interface ColumnReferenceReceiver

        /**
         * @arg [ReceiverArg] "columnA"
         * @arg [ReceiverTypeArg] String
         */
        interface StringReceiver

        /**
         * @arg [ReceiverArg] Type::columnA
         * @arg [ReceiverTypeArg] KProperty
         */
        interface KPropertyReceiver

        /**
         * @arg [ParamArg] columnB
         * @arg [ParamNameArg] nameOf
         * @arg [ParamTypeArg] ColumnReference
         */
        interface ColumnReferenceParam

        /**
         * @arg [ParamArg] "columnB"
         * @arg [ParamNameArg] newName
         * @arg [ParamTypeArg] String
         */
        interface StringParam

        /**
         * @arg [ParamArg] Type::columnB
         * @arg [ParamNameArg] nameOf
         * @arg [ParamTypeArg] KProperty
         */
        interface KPropertyParam

        /** @arg [CommonRenameDocs.FunctionNameArg] named */
        interface NamedFunctionName

        /** @arg [CommonRenameDocs.FunctionNameArg] into */
        interface IntoFunctionName
    }

    // region named

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        named(nameOf.columnName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun String.named(newName: String): ColumnReference<*> = toColumnAccessor().named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun <C> KProperty<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.columnName)

    // endregion

    // region into

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun <C> ColumnReference<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun String.into(newName: String): ColumnReference<*> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion

    // endregion

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

    /**
     * ## Distinct
     * Returns a new [ColumnSet] from [this] containing only distinct columns (by path).
     * This is useful when you've selected the same column multiple times.
     *
     * #### For Example:
     * `df.`[select][DataFrame.select]` { (`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() `[and][ColumnSet.and]` age).`[distinct][ColumnSet.distinct]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`().`[nameStartsWith][ColumnSet.nameStartsWith]`("order").`[recursively][TransformableColumnSet.recursively]`().`[distinct][ColumnSet.distinct]`() }`
     *
     * @return A new [ColumnSet] containing only distinct columns (by path).
     */
    public fun <C> ColumnSet<C>.distinct(): ColumnSet<C> = DistinctColumnSet(this)

    /**
     * @include [CommonColsOfDocs]
     * Get sub-columns of the column with this name by [type] with a [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> String.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> = colGroup(this).colsOf(type, filter)

    /**
     * @include [CommonColsOfDocs]
     * Get sub-columns of the column this [KProperty Accessor][KProperty] points to by [type] with or without [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsOf][KProperty.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::myColumnGroup).`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> KProperty<DataRow<*>>.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> = colGroup(this).colsOf(type, filter)

    /**
     * @include [CommonColsOfDocs]
     * Get sub-columns of the column this [ColumnPath] points to by [type] with or without [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> ColumnPath.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> = colGroup(this).colsOf(type, filter)
}

/**
 * @include [ColumnExpression.CommonDocs]
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][ColumnsSelectionDsl.expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][ColumnsSelectionDsl.expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
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

/**
 * ## SingleColumn As ColumnGroup
 * Casts [this][this\] [SingleColumn][SingleColumn]`<`[C][C\]`>` to a [SingleColumn][SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 * This is especially useful when you want to use `ColumnGroup` functions in the [ColumnsSelectionDsl] but your column
 * type is not recognized as a `ColumnGroup`.
 * If you're not sure whether a column is recognized or not, you can always call [asColumnGroup][SingleColumn.asColumnGroup]
 * and it will return the same type if it is already a `ColumnGroup`.
 *
 * For example:
 *
 * `df.`[select][DataFrame.select]` { it`[`[`][ColumnsContainer.get]`"myColumn"`[`]`][ColumnsContainer.get]`.`[asColumnGroup][SingleColumn.asColumnGroup]`().`[first][ColumnsSelectionDsl.firstChild]`() }`
 *
 * @receiver The [SingleColumn] to cast to a [SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 * @param [C\] The type of the (group) column.
 * @return A [SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 */
private interface SingleColumnAsColumnGroupDocs

/** @include [SingleColumnAsColumnGroupDocs] */
public fun <C> SingleColumn<C>.asColumnGroup(): SingleColumn<DataRow<C>> = this as SingleColumn<DataRow<C>>

/** @include [SingleColumnAsColumnGroupDocs] */
@JvmName("asColumnGroupDataRow")
public fun <C> SingleColumn<DataRow<C>>.asColumnGroup(): SingleColumn<DataRow<C>> = this

/**
 * ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][ColumnAccessor]`<`[DataRow][DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor]`<`[DataRow][DataRow]`>`.
 */
private interface AsColumnGroupDocs

/** @include [AsColumnGroupDocs] */
public fun <C> KProperty<C>.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** @include [AsColumnGroupDocs] */
@JvmName("asColumnGroupDataRowKProperty")
public fun <C> KProperty<DataRow<C>>.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** @include [AsColumnGroupDocs] */
@JvmName("asColumnGroupTyped")
public fun <C> String.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** @include [AsColumnGroupDocs] */
public fun String.asColumnGroup(): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(this)

/** @include [AsColumnGroupDocs] */
@JvmName("asColumnGroupTyped")
public fun <C> ColumnPath.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** @include [AsColumnGroupDocs] */
public fun ColumnPath.asColumnGroup(): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(this)

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> =
    { this@filter(it, it).asColumnSet().filter(predicate) }

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
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 * {@include [LineBreak]}
 * Alternatively, [colsOf] can also be called on existing columns:
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { `[columnGroup][columnGroup]`(Type::myColumnGroup).`[colsOf][SingleColumn.colsOf]`<`[Double][Double]`>() }`
 * {@include [LineBreak]}
 * Finally, [colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 * {@comment TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325) }
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 */
internal interface ColsOf

/**
 * @include [ColsOf]
 * ### This Cols Of Overload
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
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
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
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
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
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @include [CommonColsOfDocs.FilterParam]
 * @include [CommonColsOfDocs.Return]
 */
public fun <C> SingleColumn<DataRow<*>>.colsOf(
    type: KType,
    filter: (DataColumn<C>) -> Boolean = { true },
): TransformableColumnSet<C> =
    ensureIsColGroup().colsInternal { it.isSubtypeOf(type) && filter(it.cast()) } as TransformableColumnSet<C>

/** TODO */
public fun <C> ColumnsSelectionDsl<*>.colsOf(
    type: KType,
    filter: (DataColumn<C>) -> Boolean = { true },
): TransformableColumnSet<C> =
    asSingleColumn().colsOf(type, filter)

/**
 * @include [CommonColsOfDocs]
 * Get (sub-)columns by a given type with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * @include [CommonColsOfDocs.FilterParam]
 * @include [CommonColsOfDocs.Return]
 */
public inline fun <reified C> SingleColumn<DataRow<*>>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): TransformableColumnSet<C> =
    colsOf(typeOf<C>(), filter)

/** TODO */
public inline fun <reified C> ColumnsSelectionDsl<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): TransformableColumnSet<C> =
    asSingleColumn().colsOf(typeOf<C>(), filter)

/**
 * Checks the validity of this [SingleColumn],
 * by adding a check to see it's a [ColumnGroup] (so, a [SingleColumn]<[DataRow]<*>>)
 * and throwing an [IllegalArgumentException] if it's not.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> SingleColumn<DataRow<C>>.ensureIsColGroup(): SingleColumn<DataRow<C>> =
    performCheck { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Attempted to perform a ColumnGroup operation on ${col?.kind()} ${col?.path}."
        }
    } as SingleColumn<DataRow<C>>

/* TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325)
context(ColumnsSelectionDsl)
public inline fun <reified C> KProperty<DataRow<*>>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<*> =
    colsOf(typeOf<C>(), filter)

context(ColumnsSelectionDsl)
public inline fun <reified C> String.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<*> =
    colsOf(typeOf<C>(), filter)

 */
