package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.renamedReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.impl.columns.DistinctColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExcept
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.getChildrenAt
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.top
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.dfs
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
 * TODO
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
     * Retrieves the value of this [KProperty Accessor][KProperty] from the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn).`[get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumn`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get].
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
     * [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn).`[get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumn`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get].
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
     * [get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn).`[get][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumn`[`[`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`MyOtherType::myOtherColumn`[`]`][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
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

/** Referring to or expressing column(s) in the selection DSL can be done in several ways corresponding to all
 * [Access APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]:
 * TODO: [Issue #286](https://github.com/Kotlin/dataframe/issues/286)
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html) */
public interface ColumnsSelectionDsl<out T> : ColumnSelectionDsl<T>, SingleColumn<DataRow<T>> {

    /**
     * ## First
     * Returns the first column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
     *
     * For example:
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
     * ## First
     * Returns the first column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[first][first]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [last]
     */
    public fun <C> ColumnSet<C>.first(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        transform { listOf(it.first(condition)) }.singleImpl()

    /**
     * ## First
     * Returns the first column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { `[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][select]` { myColumnGroup.`[first][first]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [last]
     */
    public fun SingleColumn<AnyRow>.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        all().first(condition)

    /**
     * ## First
     * Returns the first column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { "myColumnGroup".`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [last]
     */
    public fun String.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        getColumnGroup(this).first(condition)

    /**
     * ## First
     * Returns the first column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [last]
     */
    public fun KProperty<*>.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        getColumnGroup(this).first(condition)


    /**
     * ## Last
     * Returns the last column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
     *
     * For example:
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
     * ## Last
     * Returns the last column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[first][last]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the last column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [first]
     */
    public fun <C> ColumnSet<C>.last(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        transform { listOf(it.last(condition)) }.singleImpl()

    /**
     * ## Last
     * Returns the last column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { `[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][select]` { myColumnGroup.`[last][last]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the last column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [first]
     */
    public fun SingleColumn<AnyRow>.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        all().last(condition)

    /**
     * ## Last
     * Returns the last column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { "myColumnGroup".`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the last column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [first]
     */
    public fun String.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        getColumnGroup(this).last(condition)

    /**
     * ## Last
     * Returns the last column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the last column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [first]
     */
    public fun KProperty<*>.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        getColumnGroup(this).last(condition)


    /**
     * ## Single
     * Returns the single column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
     *
     * For example:
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
     * ## Single
     * Returns the single column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[Int][Int]`>().`[single][single]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun <C> ColumnSet<C>.single(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        transform { listOf(it.single(condition)) }.singleImpl()

    /**
     * ## Single
     * Returns the single column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { `[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][select]` { myColumnGroup.`[single][single]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun SingleColumn<AnyRow>.single(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        all().single(condition)

    /**
     * ## Single
     * Returns the single column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { "myColumnGroup".`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun String.single(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        getColumnGroup(this).single(condition)

    /**
     * ## Single
     * Returns the single column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * For example:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun KProperty<*>.single(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        getColumnGroup(this).single(condition)

    /** TODO */
    public fun SingleColumn<AnyRow>.col(index: Int): SingleColumn<Any?> = getChildrenAt(index).singleImpl()

    /** TODO */
    public operator fun <C> ColumnSet<C>.get(index: Int): SingleColumn<C> = getAt(index)

    /** TODO */
    public fun ColumnsContainer<*>.group(name: String): ColumnGroupReference = name.toColumnOf()

    /**
     * ## Subset of Columns
     * Returns a [ColumnSet] containing all columns from [this\] to [endInclusive\].
     *
     * For example:
     *
     * `df.`[select][DataFrame.select]` { `{@includeArg [Example]}` }`
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     *
     * For example:
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
            override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<Any?>> {
                val startPath = this@rangeTo.resolveSingle(context)!!.path
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
        }

    public fun none(): ColumnSet<*> = ColumnsList<Any?>(emptyList())

    // region cols

    public fun ColumnSet<*>.cols(predicate: (AnyCol) -> Boolean = { true }): ColumnSet<Any?> = colsInternal(predicate)

    public fun <C> ColumnSet<*>.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): ColumnSet<C> =
        (listOf(firstCol) + otherCols).let { refs ->
            transform { it.flatMap { col -> refs.mapNotNull { col.getChild(it) } } }
        }

    public fun ColumnSet<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<Any?> =
        (listOf(firstCol) + otherCols).let { names ->
            transform { it.flatMap { col -> names.mapNotNull { col.getChild(it) } } }
        }

    public fun <C> ColumnSet<*>.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        (listOf(firstCol) + otherCols).let { names ->
            transform { it.flatMap { col -> names.mapNotNull { col.getChild(it) } } }
        }

    public fun ColumnSet<*>.cols(vararg indices: Int): ColumnSet<Any?> =
        transform { it.flatMap { it.children().let { children -> indices.map { children[it] } } } }

    public fun ColumnSet<*>.cols(range: IntRange): ColumnSet<Any?> =
        transform { it.flatMap { it.children().subList(range.first, range.last + 1) } }

    // region select

    public fun <C, R> ColumnSet<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> = createColumnSet {
        this@select.resolve(it).flatMap { group ->
            group.asColumnGroup().getColumnsWithPaths(selector).map {
                it.changePath(group.path + it.path)
            }
        }
    }

    public fun <C> ColumnSet<DataRow<C>>.select(vararg columns: String): ColumnSet<*> = select { columns.toColumnSet() }

    public fun <C, R> ColumnSet<DataRow<C>>.select(vararg columns: ColumnReference<R>): ColumnSet<R> =
        select { columns.toColumnSet() }

    public fun <C, R> ColumnSet<DataRow<C>>.select(vararg columns: KProperty<R>): ColumnSet<R> =
        select { columns.toColumnSet() }

    // endregion

    // endregion

    // region dfs

    public fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<Any?> = dfsInternal(predicate)

    public fun String.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> = toColumnAccessor().dfs(predicate)

    public fun <C> KProperty<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        toColumnAccessor().dfs(predicate)

    // endregion

    // region all

    public fun SingleColumn<*>.all(): ColumnSet<*> = transformSingle { it.children() }

    public fun String.all(): ColumnSet<*> = toColumnAccessor().transformSingle { it.children() }

    public fun KProperty<*>.all(): ColumnSet<*> = toColumnAccessor().transformSingle { it.children() }

    // region allDfs

    public fun ColumnSet<*>.allDfs(includeGroups: Boolean = false): ColumnSet<Any?> =
        if (includeGroups) dfs { true } else dfs { !it.isColumnGroup() }

    public fun String.allDfs(includeGroups: Boolean = false): ColumnSet<Any?> = toColumnAccessor().allDfs(includeGroups)

    public fun KProperty<*>.allDfs(includeGroups: Boolean = false): ColumnSet<Any?> =
        toColumnAccessor().allDfs(includeGroups)

    // endregion

    // region allAfter

    // excluding current
    public fun SingleColumn<*>.allAfter(colPath: ColumnPath): ColumnSet<Any?> {
        var take = false
        return children {
            if (take) true
            else {
                take = colPath == it.path
                false
            }
        }
    }

    public fun SingleColumn<*>.allAfter(colName: String): ColumnSet<Any?> = allAfter(pathOf(colName))
    public fun SingleColumn<*>.allAfter(column: AnyColumnReference): ColumnSet<Any?> = allAfter(column.path())
    public fun SingleColumn<*>.allAfter(column: KProperty<*>): ColumnSet<Any?> =
        allAfter(column.toColumnAccessor().path())

    public fun String.allAfter(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allAfter(colPath)
    public fun String.allAfter(colName: String): ColumnSet<Any?> = toColumnAccessor().allAfter(colName)
    public fun String.allAfter(column: AnyColumnReference): ColumnSet<Any?> = toColumnAccessor().allAfter(column)
    public fun String.allAfter(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allAfter(column)

    public fun KProperty<*>.allAfter(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allAfter(colPath)
    public fun KProperty<*>.allAfter(colName: String): ColumnSet<Any?> = toColumnAccessor().allAfter(colName)
    public fun KProperty<*>.allAfter(column: AnyColumnReference): ColumnSet<Any?> = toColumnAccessor().allAfter(column)
    public fun KProperty<*>.allAfter(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allAfter(column)

    // endregion

    // region allFrom

    // including current
    public fun SingleColumn<*>.allFrom(colPath: ColumnPath): ColumnSet<Any?> {
        var take = false
        return children {
            if (take) true
            else {
                take = colPath == it.path
                take
            }
        }
    }

    public fun SingleColumn<*>.allFrom(colName: String): ColumnSet<Any?> = allFrom(pathOf(colName))
    public fun SingleColumn<*>.allFrom(column: AnyColumnReference): ColumnSet<Any?> = allFrom(column.path())
    public fun SingleColumn<*>.allFrom(column: KProperty<*>): ColumnSet<Any?> =
        allFrom(column.toColumnAccessor().path())

    public fun String.allFrom(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allFrom(colPath)
    public fun String.allFrom(colName: String): ColumnSet<Any?> = toColumnAccessor().allFrom(colName)
    public fun String.allFrom(column: AnyColumnReference): ColumnSet<Any?> = toColumnAccessor().allFrom(column)
    public fun String.allFrom(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allFrom(column)

    public fun KProperty<*>.allFrom(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allFrom(colPath)
    public fun KProperty<*>.allFrom(colName: String): ColumnSet<Any?> = toColumnAccessor().allFrom(colName)
    public fun KProperty<*>.allFrom(column: AnyColumnReference): ColumnSet<Any?> = toColumnAccessor().allFrom(column)
    public fun KProperty<*>.allFrom(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allFrom(column)

    // endregion

    // region allBefore

    // excluding current
    public fun SingleColumn<*>.allBefore(colPath: ColumnPath): ColumnSet<Any?> {
        var take = true
        return children {
            if (!take) false
            else {
                take = colPath != it.path
                take
            }
        }
    }

    public fun SingleColumn<*>.allBefore(colName: String): ColumnSet<Any?> = allBefore(pathOf(colName))
    public fun SingleColumn<*>.allBefore(column: AnyColumnReference): ColumnSet<Any?> = allBefore(column.path())
    public fun SingleColumn<*>.allBefore(column: KProperty<*>): ColumnSet<Any?> =
        allBefore(column.toColumnAccessor().path())

    public fun String.allBefore(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allBefore(colPath)
    public fun String.allBefore(colName: String): ColumnSet<Any?> = toColumnAccessor().allBefore(colName)
    public fun String.allBefore(column: AnyColumnReference): ColumnSet<Any?> = toColumnAccessor().allBefore(column)
    public fun String.allBefore(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allBefore(column)

    public fun KProperty<*>.allBefore(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allBefore(colPath)
    public fun KProperty<*>.allBefore(colName: String): ColumnSet<Any?> = toColumnAccessor().allBefore(colName)
    public fun KProperty<*>.allBefore(column: AnyColumnReference): ColumnSet<Any?> =
        toColumnAccessor().allBefore(column)

    public fun KProperty<*>.allBefore(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allBefore(column)

    // endregion

    // region allUpTo

    // including current
    public fun SingleColumn<*>.allUpTo(colPath: ColumnPath): ColumnSet<Any?> {
        var take = true
        return children {
            if (!take) false
            else {
                take = colPath != it.path
                true
            }
        }
    }

    public fun SingleColumn<*>.allUpTo(colName: String): ColumnSet<Any?> = allUpTo(pathOf(colName))
    public fun SingleColumn<*>.allUpTo(column: AnyColumnReference): ColumnSet<Any?> = allUpTo(column.path())
    public fun SingleColumn<*>.allUpTo(column: KProperty<*>): ColumnSet<Any?> =
        allUpTo(column.toColumnAccessor().path())

    public fun String.allUpTo(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allUpTo(colPath)
    public fun String.allUpTo(colName: String): ColumnSet<Any?> = toColumnAccessor().allUpTo(colName)
    public fun String.allUpTo(column: AnyColumnReference): ColumnSet<Any?> = toColumnAccessor().allUpTo(column)
    public fun String.allUpTo(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allUpTo(column)

    public fun KProperty<*>.allUpTo(colPath: ColumnPath): ColumnSet<Any?> = toColumnAccessor().allUpTo(colPath)
    public fun KProperty<*>.allUpTo(colName: String): ColumnSet<Any?> = toColumnAccessor().allUpTo(colName)
    public fun KProperty<*>.allUpTo(column: AnyColumnReference): ColumnSet<Any?> = toColumnAccessor().allUpTo(column)
    public fun KProperty<*>.allUpTo(column: KProperty<*>): ColumnSet<Any?> = toColumnAccessor().allUpTo(column)

    // endregion

    // endregion

    // region groups

    public fun SingleColumn<*>.groups(filter: (ColumnGroup<*>) -> Boolean = { true }): ColumnSet<AnyRow> =
        children { it.isColumnGroup() && filter(it.asColumnGroup()) } as ColumnSet<AnyRow>

    public fun String.groups(filter: (ColumnGroup<*>) -> Boolean = { true }): ColumnSet<AnyRow> =
        toColumnAccessor().groups(filter)

    public fun KProperty<*>.groups(filter: (ColumnGroup<*>) -> Boolean = { true }): ColumnSet<AnyRow> =
        toColumnAccessor().groups(filter)

    // endregion

    // region children

    public fun ColumnSet<*>.children(predicate: (ColumnWithPath<Any?>) -> Boolean = { true }): ColumnSet<Any?> =
        transform { it.flatMap { it.children().filter { predicate(it) } } }

    public fun ColumnGroupReference.children(): ColumnSet<Any?> = transformSingle { it.children() }

    // endregion

    public operator fun <C> List<DataColumn<C>>.get(range: IntRange): ColumnSet<C> =
        ColumnsList(subList(range.first, range.last + 1))

    public fun <C> col(property: KProperty<C>): ColumnAccessor<C> = property.toColumnAccessor()

    public operator fun ColumnSet<*>.get(colName: String): ColumnSet<Any?> =
        transform { it.mapNotNull { it.getChild(colName) } }

    public operator fun <C> ColumnSet<*>.get(column: ColumnReference<C>): ColumnSet<C> = cols(column)
    public operator fun <C> ColumnSet<*>.get(column: KProperty<C>): ColumnSet<C> = cols(column)

    public fun SingleColumn<AnyRow>.take(n: Int): ColumnSet<*> = transformSingle { it.children().take(n) }
    public fun SingleColumn<AnyRow>.takeLast(n: Int): ColumnSet<*> = transformSingle { it.children().takeLast(n) }
    public fun SingleColumn<AnyRow>.drop(n: Int): ColumnSet<*> = transformSingle { it.children().drop(n) }
    public fun SingleColumn<AnyRow>.dropLast(n: Int = 1): ColumnSet<*> = transformSingle { it.children().dropLast(n) }

    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }
    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }
    public fun <C> ColumnSet<C>.takeLast(n: Int): ColumnSet<C> = transform { it.takeLast(n) }
    public fun <C> ColumnSet<C>.top(): ColumnSet<C> = transform { it.top() }
    public fun <C> ColumnSet<C>.takeWhile(predicate: Predicate<ColumnWithPath<C>>): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    public fun <C> ColumnSet<C>.takeLastWhile(predicate: Predicate<ColumnWithPath<C>>): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    public fun <C> ColumnSet<C>.filter(predicate: Predicate<ColumnWithPath<C>>): ColumnSet<C> =
        transform { it.filter(predicate) }

    public fun ColumnSet<*>.nameContains(text: CharSequence): ColumnSet<Any?> = cols { it.name.contains(text) }
    public fun ColumnSet<*>.nameContains(regex: Regex): ColumnSet<Any?> = cols { it.name.contains(regex) }
    public fun ColumnSet<*>.startsWith(prefix: CharSequence): ColumnSet<Any?> = cols { it.name.startsWith(prefix) }
    public fun ColumnSet<*>.endsWith(suffix: CharSequence): ColumnSet<Any?> = cols { it.name.endsWith(suffix) }

    public fun <C> ColumnSet<C>.except(vararg other: ColumnSet<*>): ColumnSet<*> = except(other.toColumnSet())
    public fun <C> ColumnSet<C>.except(vararg other: String): ColumnSet<*> = except(other.toColumnSet())

    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C> = transform { it.filter { !it.hasNulls } } as ColumnSet<C>

    public infix fun <C> ColumnSet<C>.except(other: ColumnSet<*>): ColumnSet<*> =
        createColumnSet { resolve(it).allColumnsExcept(other.resolve(it)) }

    public infix fun <C> ColumnSet<C>.except(selector: ColumnsSelector<T, *>): ColumnSet<C> =
        except(selector.toColumns()) as ColumnSet<C>

    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnSet<C> =
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

    // region String
    public infix fun String.and(other: String): ColumnSet<Any?> = toColumnAccessor() and other.toColumnAccessor()
    public infix fun <C> String.and(other: ColumnSet<C>): ColumnSet<Any?> = toColumnAccessor() and other
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<Any?> = toColumnAccessor() and other
    public infix fun <C> String.and(other: ColumnsSelector<T, C>): ColumnSet<Any?> = toColumnAccessor() and other()

    // endregion

    // region KProperty
    public infix fun <C> KProperty<C>.and(other: ColumnSet<C>): ColumnSet<C> = toColumnAccessor() and other
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<Any?> = toColumnAccessor() and other
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    public infix fun <C> KProperty<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = toColumnAccessor() and other()

    // endregion

    // region ColumnSet

    public infix fun <C> ColumnSet<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()
    public infix fun <C> ColumnSet<C>.and(other: String): ColumnSet<Any?> = this and other.toColumnAccessor()
    public infix fun <C> ColumnSet<C>.and(other: ColumnSet<C>): ColumnSet<C> = ColumnsList(this, other)
    public infix fun <C> ColumnSet<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this and other()

    // endregion

    // region ColumnsSelector

    public infix fun <C> ColumnsSelector<T, C>.and(other: KProperty<C>): ColumnSet<C> = this() and other
    public infix fun <C> ColumnsSelector<T, C>.and(other: String): ColumnSet<Any?> = this() and other
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnSet<C>): ColumnSet<C> = this() and other
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this() and other

    // endregion

    public fun <C> ColumnSet<C>.distinct(): ColumnSet<C> = DistinctColumnSet(this)

    public fun <C> String.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
        toColumnAccessor().dfsOf(type, predicate)

    public fun <C> KProperty<*>.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
        toColumnAccessor().dfsOf(type, predicate)

    /**
     * ## Cols Of
     * Get columns by a given type and an optional filter.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ## ‎
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { (Type::myColumnGroup)().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
     * ## ‎
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     * (TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325))
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ## This Cols Of Overload
     * Get sub-columns of the column with this name by [type] with a [filter].
     * For example:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> String.colsOf(type: KType, filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<Any?> =
        toColumnAccessor().colsOf(type, filter)

    /**
     * ## Cols Of
     * Get columns by a given type and an optional filter.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ## ‎
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { (Type::myColumnGroup)().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
     * ## ‎
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     * (TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325))
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ## This Cols Of Overload
     * Get sub-columns of the column this [KProperty Accessor][KProperty] points to by [type] with or without [filter].
     * For example:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> KProperty<*>.colsOf(type: KType, filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<Any?> =
        toColumnAccessor().colsOf(type, filter)
}

/**
 * ## Column Expression
 * Create a temporary new column by defining an expression to fill up each row.
 *
 * See [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] for more information.
 *
 * For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
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

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> =
    { this@filter(it, it).filter(predicate) }

internal fun ColumnSet<*>.colsInternal(predicate: (AnyCol) -> Boolean) =
    transform { it.flatMap { it.children().filter { predicate(it.data) } } }

internal fun ColumnSet<*>.dfsInternal(predicate: (ColumnWithPath<*>) -> Boolean) =
    transform { it.filter { it.isColumnGroup() }.flatMap { it.children().dfs().filter(predicate) } }

public fun <C> ColumnSet<*>.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
    dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

public inline fun <reified C> ColumnSet<*>.dfsOf(noinline filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<C> =
    dfsOf(typeOf<C>(), filter) as ColumnSet<C>

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 * ## ‎
 * Alternatively, [colsOf] can also be called on existing columns:
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { (Type::myColumnGroup)().`[colsOf][colsOf]`<`[Double][Double]`>() }`
 * ## ‎
 * Finally, [colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 * (TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325))
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 */
internal interface ColsOf

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## ‎
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { (Type::myColumnGroup)().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 * ## ‎
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 * (TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325))
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## This Cols Of Overload
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
 * For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## ‎
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { (Type::myColumnGroup)().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 * ## ‎
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 * (TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325))
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## This Cols Of Overload
 * Get (sub-)columns by [type] with or without [filter].
 * For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> ColumnSet<*>.colsOf(type: KType, filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<C> =
    colsInternal { it.isSubtypeOf(type) && filter(it.cast()) } as ColumnSet<C>

/**
 * ## Cols Of
 * Get columns by a given type and an optional filter.
 *
 * For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## ‎
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { (Type::myColumnGroup)().`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 * ## ‎
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 * (TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325))
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## This Cols Of Overload
 * Get (sub-)columns by a given type with or without [filter].
 * For example:
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][colsOf]`<`[Int][Int]`>() }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public inline fun <reified C> ColumnSet<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<C> =
    colsOf(typeOf<C>(), filter)

/* TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325)
context(ColumnsSelectionDsl)
public inline fun <reified C> KProperty<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<Any?> =
    colsOf(typeOf<C>(), filter)

context(ColumnsSelectionDsl)
public inline fun <reified C> String.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<Any?> =
    colsOf(typeOf<C>(), filter)

 */
