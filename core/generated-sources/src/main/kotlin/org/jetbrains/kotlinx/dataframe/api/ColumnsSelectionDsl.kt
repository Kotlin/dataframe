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
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.impl.columns.DistinctColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.getChildrenAt
import org.jetbrains.kotlinx.dataframe.impl.columns.getColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.performCheck
import org.jetbrains.kotlinx.dataframe.impl.columns.roots
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
private interface CommonColumnSelectionDocs

/**
 *
 */
private interface CommonColumnSelectionExamples

/** [Column Selection DSL][ColumnSelectionDsl] */
internal interface ColumnSelectionDslLink

/** Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html) */
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
     * Retrieves the value of this [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] or [-Accessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] from
     * the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [DataColumn] this [Column Reference][ColumnReference] or [-Accessor][ColumnAccessor] points to.
     */
    public operator fun <C> ColumnReference<C>.invoke(): DataColumn<C> = get(this)

    /**
     * Retrieves the value of this [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] or [-Accessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] from
     * the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [ColumnGroup] this [Column Reference][ColumnReference] or [-Accessor][ColumnAccessor] points to.
     */
    public operator fun <T> ColumnReference<DataRow<T>>.invoke(): ColumnGroup<T> = get(this)

    /**
     * Retrieves the value of this [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] or [-Accessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] from
     * the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [FrameColumn] this [Column Reference][ColumnReference] or [-Accessor][ColumnAccessor] points to.
     */
    public operator fun <T> ColumnReference<DataFrame<T>>.invoke(): FrameColumn<T> = get(this)

    /**
     * Retrieves the value of this [ColumnPath] from the [DataFrame].
     * This is a shorthand for [getColumn][ColumnsContainer.getColumn]`(myColumnPath)` and
     * is most often used in combination with `operator fun String.get(column: String)`, 
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
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [DataColumn] this [KProperty Accessor][KProperty] points to.
     */
    public operator fun <T> KProperty<T>.invoke(): DataColumn<T> = this@ColumnSelectionDsl[this]

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [ColumnGroup] this [KProperty Accessor][KProperty] points to.
     */
    public operator fun <T> KProperty<DataRow<T>>.invoke(): ColumnGroup<T> = this@ColumnSelectionDsl[this]

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
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
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`().`[get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup.`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`()`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [DataColumn] these [KProperty Accessors][KProperty] point to.
     */
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<R>): DataColumn<R> = invoke()[column]

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`().`[get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup.`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`()`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [ColumnGroup] these [KProperty Accessors][KProperty] point to.
     */
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<DataRow<R>>): ColumnGroup<R> =
        invoke()[column]

    /**
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`().`[get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup.`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`()`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
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
 * Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
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
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `"fromColumn".."toColumn"` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun String.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `"fromColumn"..Type::toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun String.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `"fromColumn"..toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun String.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `Type::fromColumn.."toColumn"` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `Type::fromColumn..Type::toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `Type::fromColumn..toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `fromColumn.."toColumn"` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: String): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `fromColumn..Type::toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Subset of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `fromColumn..toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents.
     *
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
     * ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[col][ColumnSet.col]`(0) }`
     *
     * `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`()`[`[`][ColumnSet.col]`5`[`]`][ColumnSet.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    private interface ColumnSetColIndexDocs

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[col][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public fun <C> ColumnSet<C>.col(index: Int): SingleColumn<C> = getAt(index)

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>().`[col][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public operator fun <C> ColumnSet<C>.get(index: Int): SingleColumn<C> = col(index)

    /**
     * ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[col][SingleColumn.col]`(0) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.col]`5`[`]`][SingleColumn.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    private interface SingleColumnColIndexDocs

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public fun SingleColumn<DataRow<*>>.col(index: Int): SingleColumn<*> =
        ensureIsColGroup().asColumnSet().getChildrenAt(index).singleImpl()

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public operator fun SingleColumn<DataRow<*>>.get(index: Int): SingleColumn<*> = col(index)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<*> =
        asSingleColumn().col(index)

    /** TODO */
    public operator fun ColumnsSelectionDsl<*>.get(index: Int): SingleColumn<*> = col(index)

    /**
     * ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[col][String.col]`(5) }`
     *
     * `// NOTE: There's a `[String.get][String.get]` function that prevents this:`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"[0] }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    private interface StringIndexDocs

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(5) }`
     *
     * `// NOTE: There's a `[String.get][kotlin.String.get]` function that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"[0] }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public fun String.col(index: Int): SingleColumn<*> = colGroup(this).col(index)

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(5) }`
     *
     * `// NOTE: There's a `[String.get][kotlin.String.get]` function that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"[0] }`
     *
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public operator fun String.get(index: Int): SingleColumn<*> = col(index)

    /**
     * ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[col][SingleColumn.col]`(5) }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::myColumnGroup)`[`[`][SingleColumn.col]`0`[`]`][SingleColumn.col]` }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[col][KProperty.col]`(5) }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    private interface KPropertyIndexDocs

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`().`[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::myColumnGroup)`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`0`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup.`[col][kotlin.reflect.KProperty.col]`(5) }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public fun KProperty<DataRow<*>>.col(index: Int): SingleColumn<*> = colGroup(this).col(index)

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`().`[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::myColumnGroup)`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`0`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup.`[col][kotlin.reflect.KProperty.col]`(5) }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public operator fun KProperty<DataRow<*>>.get(index: Int): SingleColumn<*> = col(index)

    /**
     * ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[col][ColumnPath.col]`(0) }`
     *
     * `// NOTE: There's a `[List.get][List.get]` function that prevents this:`
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"]`[`[`][ColumnPath.col]`0`[`]`][ColumnPath.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    private interface ColumnPathIndexDocs

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[col][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.col]`(0) }`
     *
     * `// NOTE: There's a `[List.get][kotlin.collections.List.get]` function that prevents this:`
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.col]`0`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
    public fun ColumnPath.col(index: Int): SingleColumn<*> = colGroup(this).col(index)

    /** ## Col: Column by Index
     *
     * Retrieves a [column][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] by index.
     * If the index is out of bounds, an [IndexOutOfBoundsException] will be thrown.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn], [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the function will take the child found at the
     * given [index].
     * Else, if called on a normal [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet],
     * the function will return the [index]'th column in the set.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`(3) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.col]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][kotlin.String.col]`(0) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[col][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.col]`(0) }`
     *
     * `// NOTE: There's a `[List.get][kotlin.collections.List.get]` function that prevents this:`
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.col]`0`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.col]` }`
     *
     * @throws [IndexOutOfBoundsException] If the index is out of bounds.
     * @param [index] The index of the column to retrieve.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] for the column at the given index.
     */
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
     *
     */
    public fun <C> ColumnSet<C>.roots(): ColumnSet<C> = rootsInternal() as ColumnSet<C>

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun SingleColumn<DataRow<*>>.roots(): ColumnSet<*> = ensureIsColGroup().rootsInternal()

    /** todo */
    public fun ColumnsSelectionDsl<*>.roots(): ColumnSet<*> = asSingleColumn().roots()

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun String.roots(): ColumnSet<*> = colGroup(this).roots()

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun KProperty<DataRow<*>>.roots(): ColumnSet<*> = colGroup(this).roots()

    /** ## Roots
     *
     * Returns a sub-set of columns that are roots of the trees of columns.
     *
     * In practice, this means that if a column in [this] is a child of another column in [this],
     * it will not be included in the result.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a single [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] it will run on the children of that group,
     * else it simply runs on the columns in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     */
    public fun ColumnPath.roots(): ColumnSet<*> = colGroup(this).roots()

    // endregion

    // region name filter

    // region nameContains

    /**
     * ## Name Contains
     * Returns a ([transformable][TransformableColumnSet]) [ColumnSet] containing
     * all columns containing {@includeArg [CommonNameContainsDocs.ArgumentArg]} in their name.
     *
     * If [this\] is a [SingleColumn] containing a [ColumnGroup], the function runs on the children of the [ColumnGroup].
     * Else, if [this\] is a [ColumnSet], the function runs on the [ColumnSet] itself.
     *
     * This function is a shorthand for [cols][SingleColumn.cols]` { `{@includeArg [ArgumentArg]}{@includeArg [ArgumentArg]}` `[in][String.contains]` it.`[name][DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][SingleColumn.nameContains]`("my").`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg]}
     *
     * @param {@includeArg [ArgumentArg]} what the column name should contain to be included in the result.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing
     *   all columns containing {@includeArg [CommonNameContainsDocs.ArgumentArg]} in their name.
     */
    private interface CommonNameContainsDocs {
        interface ExampleArg

        /** [text\] or [regex\] */
        interface ArgumentArg
    }

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonNameContainsDocs.ExampleArg]}
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    private interface NameContainsTextDocs

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(text: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.contains(text) } as TransformableColumnSet<C>

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[nameContains][SingleColumn.nameContains]`("my").`[rec][TransformableColumnSet.rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][SingleColumn.nameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun SingleColumn<DataRow<*>>.nameContains(text: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.contains(text) }

    /** TODO */
    public fun ColumnsSelectionDsl<*>.nameContains(text: CharSequence): TransformableColumnSet<*> =
        this.asSingleColumn().nameContains(text)

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.nameContains]`("my").`[rec][TransformableColumnSet.rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.nameContains]`("my") }`
     *
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun String.nameContains(text: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameContains(text)

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][SingleColumn.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[nameContains][KProperty.nameContains]`("my").`[rec][TransformableColumnSet.rec]`() }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun KProperty<DataRow<*>>.nameContains(text: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameContains(text)

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[nameContains][ColumnPath.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[nameContains][ColumnPath.nameContains]`("my").`[rec][TransformableColumnSet.rec]`() }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun ColumnPath.nameContains(text: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameContains(text)

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonNameContainsDocs.ExampleArg]}
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    private interface NameContainsRegexDocs

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): TransformableColumnSet<C> =
        colsInternal { it.name.contains(regex) } as TransformableColumnSet<C>

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[nameContains][SingleColumn.nameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][TransformableColumnSet.rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][SingleColumn.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun SingleColumn<DataRow<*>>.nameContains(regex: Regex): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.contains(regex) }

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.nameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][TransformableColumnSet.rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun String.nameContains(regex: Regex): TransformableColumnSet<*> =
        colGroup(this).nameContains(regex)

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][SingleColumn.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[nameContains][KProperty.nameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][TransformableColumnSet.rec]`() }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun KProperty<DataRow<*>>.nameContains(regex: Regex): TransformableColumnSet<*> =
        colGroup(this).nameContains(regex)

    /**
     * ## Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.nameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.nameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[nameContains][ColumnPath.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[nameContains][ColumnPath.nameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][TransformableColumnSet.rec]`() }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun ColumnPath.nameContains(regex: Regex): TransformableColumnSet<*> =
        colGroup(this).nameContains(regex)

    // endregion

    /**
     * ## Name {@includeArg [CommonNameStartsEndsDocs.CapitalTitle]} With
     * Returns a ([transformable][TransformableColumnSet]) [ColumnSet] containing
     * all columns {@includeArg [CommonNameStartsEndsDocs.Noun]} with {@includeArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
     *
     * If [this\] is a [SingleColumn] containing a [ColumnGroup], the function runs on the children of the [ColumnGroup].
     * Else, if [this\] is a [ColumnSet], the function runs on the [ColumnSet] itself.
     *
     * This function is a shorthand for [cols][SingleColumn.cols]` { it.`[name][DataColumn.name]`.`[{@includeArg [OperationName]}][String.{@includeArg [OperationName]}]`(`{@includeArg [ArgumentArg]}{@includeArg [ArgumentArg]}`) }`.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[{@includeArg [NameOperationName]}][SingleColumn.{@includeArg [NameOperationName]}]`("order").`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[{@includeArg [NameOperationName]}][String.{@includeArg [NameOperationName]}]`("b") }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[{@includeArg [NameOperationName]}][SingleColumn.{@includeArg [NameOperationName]}]`("a") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg]}
     *
     * @param {@includeArg [ArgumentArg]} Columns {@includeArg [CommonNameStartsEndsDocs.Noun]} with this {@includeArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name will be returned.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing
     *   all columns {@includeArg [CommonNameStartsEndsDocs.Noun]} with {@includeArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
     */
    private interface CommonNameStartsEndsDocs {

        /** "Starts" or "Ends" */
        interface CapitalTitle

        /** "starting" or "ending" */
        interface Noun

        /** "startsWith" or "endsWith" */
        interface OperationName

        /** "nameStartsWith" or "nameEndsWith" */
        interface NameOperationName

        /** [prefix\] or [suffix\] */
        interface ArgumentArg

        interface ExampleArg
    }

    // region nameStartsWith

    /**
     * ## Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains][prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][SingleColumn.nameStartsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameStartsWith][String.nameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][SingleColumn.nameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonNameStartsEndsDocs.ExampleArg]}
     *
     * @param [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] Columns starting with this [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] in their name.
     */
    private interface CommonNameStartsWithDocs

    @Deprecated("Use nameStartsWith instead", ReplaceWith("this.nameStartsWith(prefix)"))
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.startsWith(prefix: CharSequence): TransformableColumnSet<C> =
        nameStartsWith(prefix)

    @Deprecated("Use nameStartsWith instead", ReplaceWith("this.nameStartsWith(prefix)"))
    public fun SingleColumn<DataRow<*>>.startsWith(prefix: CharSequence): TransformableColumnSet<*> =
        nameStartsWith(prefix)

    @Deprecated("Use nameStartsWith instead", ReplaceWith("this.nameStartsWith(prefix)"))
    public fun ColumnsSelectionDsl<*>.startsWith(prefix: CharSequence): TransformableColumnSet<*> =
        nameStartsWith(prefix)

    /**
     * ## Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][SingleColumn.nameStartsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameStartsWith][String.nameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][SingleColumn.nameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameStartsWith][ColumnSet.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.startsWith(prefix) } as TransformableColumnSet<C>

    /**
     * ## Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][SingleColumn.nameStartsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameStartsWith][String.nameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][SingleColumn.nameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[nameStartsWith][SingleColumn.nameStartsWith]`("order-") }`
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[nameStartsWith][SingleColumn.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun SingleColumn<DataRow<*>>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.startsWith(prefix) }

    /** TODO */
    public fun ColumnsSelectionDsl<*>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().nameStartsWith(prefix)

    /**
     * ## Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][SingleColumn.nameStartsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameStartsWith][String.nameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][SingleColumn.nameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameStartsWith][String.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun String.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameStartsWith(prefix)

    /**
     * ## Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][SingleColumn.nameStartsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameStartsWith][String.nameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][SingleColumn.nameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][SingleColumn.nameStartsWith]`("order-") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[nameStartsWith][KProperty.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun KProperty<DataRow<*>>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameStartsWith(prefix)

    /**
     * ## Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][SingleColumn.nameStartsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameStartsWith][String.nameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][SingleColumn.nameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameStartsWith][ColumnPath.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun ColumnPath.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameStartsWith(prefix)

    // endregion

    // region nameEndsWith

    /**
     * ## Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains][suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][SingleColumn.nameEndsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameEndsWith][String.nameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][SingleColumn.nameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonNameStartsEndsDocs.ExampleArg]}
     *
     * @param [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] Columns ending with this [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] in their name.
     */
    private interface CommonNameEndsWithDocs

    @Deprecated("Use nameEndsWith instead", ReplaceWith("this.nameEndsWith(suffix)"))
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.endsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    @Deprecated("Use nameEndsWith instead", ReplaceWith("this.nameEndsWith(suffix)"))
    public fun SingleColumn<DataRow<*>>.endsWith(suffix: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * ## Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][SingleColumn.nameEndsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameEndsWith][String.nameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][SingleColumn.nameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameEndsWith][ColumnSet.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun <C> ColumnSet<C>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    /**
     * ## Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][SingleColumn.nameEndsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameEndsWith][String.nameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][SingleColumn.nameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[nameEndsWith][SingleColumn.nameEndsWith]`("-order") }`
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[nameEndsWith][SingleColumn.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun SingleColumn<DataRow<*>>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * ## Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][SingleColumn.nameEndsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameEndsWith][String.nameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][SingleColumn.nameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameEndsWith][String.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun String.nameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameEndsWith(suffix)

    /**
     * ## Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][SingleColumn.nameEndsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameEndsWith][String.nameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][SingleColumn.nameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][SingleColumn.nameEndsWith]`("-order") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[nameEndsWith][KProperty.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun KProperty<DataRow<*>>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameEndsWith(suffix)

    /**
     * ## Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][SingleColumn.nameEndsWith]`("order").`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameEndsWith][String.nameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][SingleColumn.nameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::someGroupCol).`[nameEndsWith][ColumnPath.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun ColumnPath.nameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        colGroup(this).nameEndsWith(suffix)

    // endregion

    // endregion

    // region select

    /**
     * ## Select from [ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
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
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
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
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup `[{][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][SingleColumn.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
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

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup `[{][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` colB `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]
     */
    public operator fun <C, R> SingleColumn<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
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
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public fun <C, R> KProperty<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup)`[() `{`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` colB `[`}`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColGroup.`[asColumnGroup][kotlin.reflect.KProperty.asColumnGroup]`().`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { colA `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup.`[select][kotlin.reflect.KProperty.select]` { colA `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` colB } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColGroup `[`{`][kotlin.reflect.KProperty.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` colB `[`}`][kotlin.reflect.KProperty.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <C, R> KProperty<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][String.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup" `[{][String.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][String.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[select][kotlin.String.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup" `[{][kotlin.String.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` colB `[}][kotlin.String.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <R> String.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    /**
     * ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"] `[{][ColumnPath.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup")`[() {][ColumnPath.select]` someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[}][ColumnPath.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /** ## Select from [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol" `[{][kotlin.String.select]` "colA" and `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[}][kotlin.String.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[() {][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` "colA" and "colB" `[}][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"] `[{][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` colA `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` colB `[}][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[pathOf][org.jetbrains.kotlinx.dataframe.api.pathOf]`("pathTo", "myColGroup").`[select][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[pathOf][org.jetbrains.kotlinx.dataframe.api.pathOf]`("pathTo", "myColGroup")`[() {][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` someCol `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[}][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]/[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept] for the inverted operation of this function.
     *
     * @param [selector] The [ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
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

    // region except

    /** TODO tbd */
    public fun <C> ColumnSet<C>.colsExcept(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
        cols { !predicate(it) }

    /** TODO tbd */
    public fun SingleColumn<DataRow<*>>.colsExcept(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        cols { !predicate(it) }

    // TODO Same as select and cols but then inverted

    // region ColumnsSelector

    public infix fun <C> ColumnSet<C>.except(selector: ColumnsSelector<T, *>): ColumnSet<C> =
        except(selector.toColumns()) as ColumnSet<C>

    // TODO TBD
    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(selector)"), DeprecationLevel.WARNING)
    public infix fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allExcept(selector)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(selector)"), DeprecationLevel.WARNING)
    public infix fun ColumnsSelectionDsl<*>.except(selector: ColumnsSelector<T, *>): ColumnSet<*> =
        allExcept(selector)

    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<*>> =
        ensureIsColGroup().transformSingle { singleCol ->

            val columnsToExcept = singleCol.asColumnGroup().getColumnsWithPaths(selector)
                .map { it.changePath(singleCol.path + it.path) }

            val newCols = listOf(singleCol).allColumnsExceptKeepingStructure(columnsToExcept)

            newCols as List<ColumnWithPath<DataRow<*>>>
        }.single()

    public fun <C> SingleColumn<DataRow<C>>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        createColumnSet { context ->
            this.ensureIsColGroup().resolveSingle(context)?.let { col ->
                require(col.isColumnGroup()) {
                    "Column ${col.path} is not a ColumnGroup and can thus not be excepted from."
                }

                val allCols = col.asColumnGroup()
                    .getColumnsWithPaths { all() }

                val columnsToExcept = col.asColumnGroup()
                    .getColumnsWithPaths(selector as ColumnsSelector<*, *>)

                allCols.allColumnsExceptKeepingStructure(columnsToExcept)
                    .map { it.changePath(col.path + it.path) }
            } ?: emptyList()
        }

    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allExcept(selector)

    public fun String.allExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        colGroup(this).allExcept(selector)

    public fun <C> KProperty<DataRow<C>>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        colGroup(this).allExcept(selector)

    public fun ColumnPath.allExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        colGroup(this).allExcept(selector)

    /** TODO tbd */
    public operator fun <C> SingleColumn<DataRow<C>>.minus(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allExcept(selector)

    // endregion

    // region ColumnsResolver

    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): ColumnSet<C> =
        createColumnSet { context ->
            this@except
                .resolve(context)
                .allColumnsExceptKeepingStructure(other.resolve(context))
        } as ColumnSet<C>

    public fun <C> ColumnSet<C>.except(vararg other: ColumnsResolver<*>): ColumnSet<C> =
        except(other.toColumnSet())

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun SingleColumn<DataRow<*>>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(*other)

    @Deprecated("Use allExcept instead", ReplaceWith("this.allExcept(other)"))
    public fun ColumnsSelectionDsl<*>.except(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(*other)

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        ensureIsColGroup().all().except(other)

    public fun SingleColumn<DataRow<*>>.allExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(other.toColumnSet())

    public infix fun ColumnsSelectionDsl<*>.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        this.asSingleColumn().allExcept(other)

    public fun ColumnsSelectionDsl<*>.allExcept(vararg other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(other.toColumnSet())

    /** TODO tbd */
    public operator fun SingleColumn<DataRow<*>>.minus(other: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(other)

    public infix fun String.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // region String

    public infix fun <C> ColumnSet<C>.except(other: String): ColumnSet<C> =
        except(col(other))

    public fun <C> ColumnSet<C>.except(vararg others: String): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: String): ColumnSet<*> =
        allExcept(col(other))

    public fun SingleColumn<DataRow<*>>.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public fun String.allExcept(other: String): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: String): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: String): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: String): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // region KProperty

    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> =
        except(col(other))

    public fun <C> ColumnSet<C>.except(vararg others: KProperty<C>): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: KProperty<*>): ColumnSet<*> =
        allExcept(col(other))

    public fun SingleColumn<DataRow<*>>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public fun String.allExcept(other: KProperty<*>): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: KProperty<*>): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: KProperty<*>): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // region ColumnPath

    public infix fun <C> ColumnSet<C>.except(other: ColumnPath): ColumnSet<C> =
        except(col(other))

    public fun <C> ColumnSet<C>.except(vararg others: ColumnPath): ColumnSet<C> =
        except(others.toColumnSet())

    public infix fun SingleColumn<DataRow<*>>.allExcept(other: ColumnPath): ColumnSet<*> =
        allExcept(col(other))

    public fun SingleColumn<DataRow<*>>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public fun String.allExcept(other: ColumnPath): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun String.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun KProperty<DataRow<*>>.allExcept(other: ColumnPath): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun KProperty<DataRow<*>>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    public infix fun ColumnPath.allExcept(other: ColumnPath): ColumnSet<*> =
        colGroup(this).allExcept(other)

    public fun ColumnPath.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        allExcept(others.toColumnSet())

    // endregion

    // endregion

    // region without nulls

    /**
     * ## Without Nulls
     * Returns a new [ColumnSet] that contains only columns that do not have `null` values.
     *
     * If called on a [SingleColumn] containing just a [ColumnGroup], [withoutNulls][SingleColumn.withoutNulls]
     * returns a [ColumnSet] containing all columns in the [ColumnGroup]'s children that do not contain `null`s.
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]` { `[all][SingleColumn.all]`().`[nameContains][ColumnSet.nameContains]`("middleName").`[withoutNulls][ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[withoutNulls][SingleColumn.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::userData).`[withoutNulls][SingleColumn.withoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    private interface CommonWithoutNullsDocs {

        interface ExampleArg
    }

    /**
     * ## Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing just a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]
     * returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]'s children that do not contain `null`s.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.nameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::userData).`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C & Any> =
        transform { it.filter { !it.hasNulls() } } as ColumnSet<C & Any>

    /**
     * ## Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing just a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]
     * returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]'s children that do not contain `null`s.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.nameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::userData).`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun SingleColumn<DataRow<*>>.withoutNulls(): ColumnSet<Any> =
        ensureIsColGroup().allColumnsInternal().withoutNulls()

    /**
     * ## Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing just a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]
     * returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]'s children that do not contain `null`s.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.nameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::userData).`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun String.withoutNulls(): ColumnSet<Any> =
        colGroup(this).withoutNulls()

    /**
     * ## Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing just a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]
     * returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]'s children that do not contain `null`s.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.nameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::userData).`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun KProperty<DataRow<*>>.withoutNulls(): ColumnSet<Any> =
        colGroup(this).withoutNulls()

    /**
     * ## Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns that do not have `null` values.
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing just a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]
     * returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]'s children that do not contain `null`s.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.nameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][colGroup]`(Type::userData).`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.withoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonWithoutNullsDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonWithoutNullsDocs.ExampleArg]]}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun ColumnPath.withoutNulls(): ColumnSet<Any> =
        colGroup(this).withoutNulls()

    // endregion

    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        this(this@ColumnsSelectionDsl, this@ColumnsSelectionDsl)

    // region rename

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][ColumnReference.named] or [into][ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
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
         */
        interface ColumnReferenceReceiver

        /**
         */
        interface StringReceiver

        /**
         */
        interface KPropertyReceiver

        /**
         */
        interface ColumnReferenceParam

        /**
         */
        interface StringParam

        /**
         */
        interface KPropertyParam

        interface NamedFunctionName

        interface IntoFunctionName
    }

    // region named

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[named][ColumnReference.named]` "columnB" }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[named][ColumnReference.named]` columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[named][ColumnReference.named]` Type::columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[named][String.named]` "columnB" }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.named(newName: String): ColumnReference<*> = toColumnAccessor().named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[named][String.named]` columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[named][String.named]` Type::columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[named][KProperty.named]` "columnB" }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[named][KProperty.named]` columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[named][KProperty.named]` Type::columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.columnName)

    // endregion

    // region into

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[into][ColumnReference.into]` "columnB" }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[into][ColumnReference.into]` columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[into][ColumnReference.into]` Type::columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[into][String.into]` "columnB" }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.into(newName: String): ColumnReference<*> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[into][String.into]` columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[into][String.into]` Type::columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[into][KProperty.into]` "columnB" }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[into][KProperty.into]` columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[into][KProperty.into]` Type::columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion

    // endregion

    // region and

    /**
     * ## And Operator
     * The [and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { "colA" `[and][String.and]` colB }`
     *
     * `df.`[select][DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[recursively][TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
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
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]` { ... } `[and][ColumnsResolver.and] {@includeArg [ColumnsResolverAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    private interface ColumnsResolverAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnsList(this, other)

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this and other()

    // endregion

    // region String

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { "colA" `[and][String.and] {@includeArg [StringAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    private interface StringAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: ColumnsSelector<T, C>): ColumnSet<*> = toColumnAccessor() and other()

    // endregion

    // region KProperty

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::colA `[and][KProperty.and] {@includeArg [KPropertyAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    private interface KPropertyAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = toColumnAccessor() and other()

    // endregion

    // region ColumnsSelector

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]` { intCols `[and][and] {@includeArg [ColumnsSelectorAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    private interface ColumnsSelectorAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsResolver<C>): ColumnSet<C> = this() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsSelector<T, C>.and(other: String): ColumnSet<*> = this() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsSelector<T, C>.and(other: KProperty<C>): ColumnSet<C> = this() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[recursively][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet.recursively]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and] operator.
     */
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
     * ## Cols Of
     * Get columns by a given type and an optional filter.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ### This Cols Of Overload
     * Get sub-columns of the column with this name by [type] with a [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> String.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> = colGroup(this).colsOf(type, filter)

    /**
     * ## Cols Of
     * Get columns by a given type and an optional filter.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ### This Cols Of Overload
     * Get sub-columns of the column this [KProperty Accessor][KProperty] points to by [type] with or without [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsOf][KProperty.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(Type::myColumnGroup).`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> KProperty<DataRow<*>>.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> = colGroup(this).colsOf(type, filter)

    /**
     * ## Cols Of
     * Get columns by a given type and an optional filter.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ### This Cols Of Overload
     * Get sub-columns of the column this [ColumnPath] points to by [type] with or without [filter].
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> ColumnPath.colsOf(
        type: KType,
        filter: (DataColumn<C>) -> Boolean = { true },
    ): ColumnSet<*> = colGroup(this).colsOf(type, filter)
}

/**
 * ## Column Expression
 * Create a temporary new column by defining an expression to fill up each row.
 *
 * See [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] for more information.
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][ColumnsSelectionDsl.expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][ColumnsSelectionDsl.expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Will be empty by default.
 * @param [infer] [An enum][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [DataColumn.type][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [None][org.jetbrains.kotlinx.dataframe.api.Infer.None], [Nulls][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [Type][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [Nulls][Infer.Nulls].
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

/** ## SingleColumn As ColumnGroup
 * Casts [this][this] [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[C][C]`>` to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * This is especially useful when you want to use `ColumnGroup` functions in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] but your column
 * type is not recognized as a `ColumnGroup`.
 * If you're not sure whether a column is recognized or not, you can always call [asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]
 * and it will return the same type if it is already a `ColumnGroup`.
 *
 * For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`"myColumn"`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`.`[asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]`().`[first][ColumnsSelectionDsl.firstChild]`() }`
 *
 * @receiver The [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] to cast to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * @param [C] The type of the (group) column.
 * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`. */
public fun <C> SingleColumn<C>.asColumnGroup(): SingleColumn<DataRow<C>> = this as SingleColumn<DataRow<C>>

/** ## SingleColumn As ColumnGroup
 * Casts [this][this] [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[C][C]`>` to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * This is especially useful when you want to use `ColumnGroup` functions in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] but your column
 * type is not recognized as a `ColumnGroup`.
 * If you're not sure whether a column is recognized or not, you can always call [asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]
 * and it will return the same type if it is already a `ColumnGroup`.
 *
 * For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`"myColumn"`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`.`[asColumnGroup][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]`().`[first][ColumnsSelectionDsl.firstChild]`() }`
 *
 * @receiver The [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] to cast to a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`.
 * @param [C] The type of the (group) column.
 * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>`. */
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

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
public fun <C> KProperty<C>.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
@JvmName("asColumnGroupDataRowKProperty")
public fun <C> KProperty<DataRow<C>>.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
@JvmName("asColumnGroupTyped")
public fun <C> String.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
public fun String.asColumnGroup(): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
@JvmName("asColumnGroupTyped")
public fun <C> ColumnPath.asColumnGroup(): ColumnAccessor<DataRow<C>> = columnGroup<C>(this)

/** ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<`[C][C]`>>` from [this][this].
 * It can both be typed and untyped and is just a shortcut to [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(`[this][this]`)`
 *
 * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`>`. */
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
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf] can also be called on existing columns:
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { `[columnGroup][columnGroup]`(Type::myColumnGroup).`[colsOf][SingleColumn.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 */
internal interface ColsOf

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ### This Cols Of Overload
 */
private interface CommonColsOfDocs {

    /** @return A [ColumnSet] containing the columns of given type that were included by [filter\]. */
    interface Return

    /** @param [filter\] an optional filter function that takes a column of type [C\] and returns `true` if the column should be included. */
    interface FilterParam
}

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ### This Cols Of Overload
 * Get (sub-)columns by [type] with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> ColumnSet<*>.colsOf(
    type: KType,
    filter: (DataColumn<C>) -> Boolean = { true },
): TransformableColumnSet<C> =
    colsInternal { it.isSubtypeOf(type) && filter(it.cast()) } as TransformableColumnSet<C>

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ### This Cols Of Overload
 * Get (sub-)columns by a given type with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public inline fun <reified C> ColumnSet<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): TransformableColumnSet<C> =
    colsOf(typeOf<C>(), filter)

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ### This Cols Of Overload
 * Get (sub-)columns by [type] with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
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
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup]`(Type::myColumnGroup).`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ### This Cols Of Overload
 * Get (sub-)columns by a given type with or without [filter].
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
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
