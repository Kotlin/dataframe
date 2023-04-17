package org.jetbrains.kotlinx.dataframe.api

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
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.*
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.dfs
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
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
     * ## First
     * Returns the first column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
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
     * #### For example:
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
     * #### For example:
     *
     * `df.`[select][select]` { "myColumnGroup".`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [last]
     */
    public fun String.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).first(condition)

    /**
     * ## First
     * Returns the first column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
     *
     * `df.`[select][select]` { "pathTo"["myColumnGroup"].`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [last]
     */
    public fun ColumnPath.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).first(condition)

    /**
     * ## First
     * Returns the first column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[first][first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [last]
     */
    public fun KProperty<*>.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).first(condition)

    /**
     * ## Last
     * Returns the last column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
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
     * ## Last
     * Returns the last column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
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
     * #### For example:
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
     * #### For example:
     *
     * `df.`[select][select]` { "myColumnGroup".`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the last column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [first]
     */
    public fun String.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).last(condition)

    /**
     * ## Last
     * Returns the last column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
     *
     * `df.`[select][select]` { "pathTo"["myColumnGroup"].`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the last column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [first]
     */
    public fun ColumnPath.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).last(condition)

    /**
     * ## Last
     * Returns the last column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[last][last]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the last column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [first]
     */
    public fun KProperty<*>.last(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).last(condition)

    /**
     * ## Single
     * Returns the single column in this [ColumnSet] or [ColumnGroup] that adheres to the given [condition\].
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
     * ## Single
     * Returns the single column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
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
     * #### For example:
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
     * #### For example:
     *
     * `df.`[select][select]` { "myColumnGroup".`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun String.single(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).single(condition)

    /**
     * ## Single
     * Returns the single column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
     *
     * `df.`[select][select]` { "pathTo"["myColumnGroup"].`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun ColumnPath.single(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).single(condition)

    /**
     * ## Single
     * Returns the single column in this [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] that adheres to the given [condition].
     *
     * #### For example:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[single][single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @throws [IllegalArgumentException] if more than one column adheres to the given [condition].
     */
    public fun KProperty<*>.single(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        colGroup(this).single(condition)

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

            override fun resolveAfterTransform(
                context: ColumnResolutionContext,
                transform: (ColumnSet<*>) -> ColumnSet<*>,
            ): List<ColumnWithPath<Any?>> =
                process(transform(this@rangeTo) as AnyColumnReference, context)
        }

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
    public fun none(): ColumnSet<*> = ColumnsList<Any?>(emptyList())

    // region colGroupFrame
    // region col

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][select]` { `[col][col]`({@includeArg [CommonColDocs.Arg]}) }`
     *
     * `df.`[select][select]` { myColGroup.`[col][col]`<SomeType>({@includeArg [CommonColDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the column with the given argument.
     * @see [column\]
     * @see [colGroup\]
     * @see [frameCol\]
     *
     */
    private interface CommonColDocs {

        /** Example argument */
        interface Arg

        /** Optional note */
        interface Note
    }

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun col(name: String): ColumnAccessor<*> = column<Any?>(name)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    public fun <C> col(name: String): ColumnAccessor<C> = column(name)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * NOTE: For column paths, this is an identity function and can be removed.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     *
     * @param [path] The [ColumnPath] pointing to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun col(path: ColumnPath): ColumnAccessor<*> = column<Any?>(path)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [path] The [ColumnPath] pointing to the column.
     * @param [C] The type of the column.
     */
    public fun <C> col(path: ColumnPath): ColumnAccessor<C> = column(path)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [property] The [KProperty] pointing to the column.
     */
    public fun <C> col(property: KProperty<C>): ColumnAccessor<C> = column(property)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [name] The name of the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnGroupReference.col(name: String): ColumnAccessor<*> = column<Any?>(name)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [name] The name of the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     * @param [C] The type of the column.
     */
    public fun <C> ColumnGroupReference.col(name: String): ColumnAccessor<C> = column(name)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [path] The [ColumnPath] pointing to the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnGroupReference.col(path: ColumnPath): ColumnAccessor<*> = column<Any?>(path)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [path] The [ColumnPath] pointing to the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     * @param [C] The type of the column.
     */
    public fun <C> ColumnGroupReference.col(path: ColumnPath): ColumnAccessor<C> = column(path)

    /**
     * ## Column Accessor
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column with the given argument.
     * This is a shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument.
     * @see [column]
     * @see [colGroup]
     * @see [frameCol]
     *  
     * @param [property] The [KProperty] pointing to the column.
     * @receiver The [ColumnGroupReference] to get the column from.
     */
    public fun <C> ColumnGroupReference.col(property: KProperty<C>): ColumnAccessor<C> = column(property)

    public fun SingleColumn<AnyRow>.col(index: Int): SingleColumn<Any?> = getChildrenAt(index).singleImpl()

    public operator fun <C> ColumnSet<C>.get(index: Int): SingleColumn<C> = getAt(index)

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
     * `df.`[select][select]` { `[colGroup][colGroup]`({@includeArg [CommonColGroupDocs.Arg]}) }`
     *
     * `df.`[select][select]` { myColGroup.`[colGroup][colGroup]`<SomeType>({@includeArg [CommonColGroupDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup\]
     * @see [col\]
     * @see [frameCol\]
     */
    private interface CommonColGroupDocs {

        /** Example argument */
        interface Arg
    }

    @Deprecated("Use colGroup() instead.", ReplaceWith("this.colGroup(name)"))
    public fun ColumnsContainer<*>.group(name: String): ColumnGroupReference = name.toColumnOf()

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("groupUnTyped")
    public fun colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> = columnGroup<C>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> = columnGroup(property)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the column group.
     */
    public fun <C> colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> = columnGroup(property)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnGroupReference.colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroupName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [name] The name of the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnGroupReference.colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnGroupReference.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        columnGroup<Any?>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`("columnGroup"["columnGroupName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>("columnGroup"["columnGroupName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [path] The [ColumnPath] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnGroupReference.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        columnGroup<C>(path)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
     * @param [property] The [KProperty] pointing to the column group.
     * @receiver The [ColumnGroupReference] to get the column group from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> ColumnGroupReference.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(property)

    /**
     * ## Column Group Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a column group with the given argument.
     * This is a shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::columnGroupName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`<SomeType>(Type::columnGroupName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument.
     * @see [columnGroup]
     * @see [col]
     * @see [frameCol] 
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
     * `df.`[select][select]` { `[frameCol][frameCol]`({@includeArg [CommonFrameColDocs.Arg]}) }`
     *
     * `df.`[select][select]` { myColGroup.`[frameCol][frameCol]`<SomeType>({@includeArg [CommonFrameColDocs.Arg]}) }`
     *
     * @return A [ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn\]
     * @see [colGroup\]
     * @see [col\]
     */
    private interface CommonFrameColDocs {

        /** Example argument */
        interface Arg
    }

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [name] The name of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameUnTyped")
    public fun frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [name] The name of the frame column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameUnTyped")
    public fun frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> = frameColumn<C>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [property] The [KProperty] pointing to the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColKPropertyDataFrame")
    public fun <C> frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> = frameColumn(property)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [property] The [KProperty] pointing to the frame column.
     */
    public fun <C> frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> = frameColumn(property)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [name] The name of the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameUnTyped")
    public fun ColumnGroupReference.frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnName") }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [name] The name of the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnGroupReference.frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameUnTyped")
    public fun ColumnGroupReference.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
        frameColumn<Any?>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`("columnGroup"["columnName"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>("columnGroup"["columnName"]) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [path] The [ColumnPath] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnGroupReference.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        frameColumn<C>(path)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
     * @param [property] The [KProperty] pointing to the frame column.
     * @receiver The [ColumnGroupReference] to get the frame column from.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColKPropertyDataFrame")
    public fun <C> ColumnGroupReference.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        frameColumn(property)

    /**
     * ## Frame Column Accessor
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a frame column with the given argument.
     * This is a shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped.
     * The function can also be called on [ColumnGroupReferences][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`(Type::columnName) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`<SomeType>(Type::columnName) }`
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument.
     * @see [frameColumn]
     * @see [colGroup]
     * @see [col] 
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
     * Creates a subset of columns ([ColumnSet]) from a parent [ColumnSet], -[ColumnGroup], or -[DataFrame].
     * You can use either a [ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][AccessApi] (+ [ColumnPath]).
     *
     * Aside from calling [cols] directly, you can also use the [get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][DataFrame.remove]` { `[cols][cols]` { it.`[hasNulls][hasNulls]`() } }`
     *
     * `df.`[select][DataFrame.select]` { myGroupCol.`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonColsDocs.Examples]}
     *
     */
    private interface CommonColsDocs {

        /**
         * ## Cols
         * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
         * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
         * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
         *
         * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
         *
         * #### For example:
         * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
         *
         * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
         *
         * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
         *
         *
         * #### Examples for this overload:
         *
         * {@includeArg [CommonColsDocs.Examples][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonColsDocs.Examples]}
         *
         *
         * @param [predicate\] A [ColumnFilter function][ColumnFilter] that takes a [ColumnReference] and returns a [Boolean].
         * @return A [ColumnSet] containing the columns that match the given [predicate\].
         */
        interface Predicate

        /**
         * ## Cols
         * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
         * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
         * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
         *
         * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
         *
         * #### For example:
         * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
         *
         * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
         *
         * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
         *
         *
         * #### Examples for this overload:
         *
         * {@includeArg [CommonColsDocs.Examples][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.CommonColsDocs.Examples]}
         *
         *
         * @param [firstCol\] A {@includeArg [AccessorType]} that points to a column.
         * @param [otherCols\] Optional additional {@includeArg [AccessorType]}s that point to columns.
         * @return A [ColumnSet] containing the columns that [firstCol\] and [otherCols\] point to.
         */
        interface Vararg {

            interface AccessorType
        }

        /** Example argument */
        interface Examples
    }

    // region predicate

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[colsOf][colsOf]` call`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `// identity call, same as `[all][all]
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    private interface ColumnSetColsPredicateDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// identity call, same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        predicate: ColumnFilter<C> = { true },
    ): ColumnSet<C> = transformWithContext {
        dataFrameOf(it)
            .asColumnGroup()
            .cols(predicate as ColumnFilter<*>)
            .resolve(this)
    } as ColumnSet<C>

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// identity call, same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public operator fun <C> ColumnSet<C>.get(
        predicate: ColumnFilter<C> = { true },
    ): ColumnSet<C> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { `[cols][cols]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][select]` { this`[`[`][cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `df.`[select][select]` { myColumnGroup`.[cols][cols]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `// same as `[all][all]
     *
     * `df.`[select][select]` { `[cols][cols]`() }`
     *
     * `// NOTE: there's a `[DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][select]` { myColumnGroup`[`[`][cols]`{ ... }`[`]`][cols]` }`
     *
     * `// use `[cols][cols]` instead`
     * `df.`[select][select]` { myColumnGroup`.[cols][cols]` { ... } }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    private interface SingleColumnAnyRowColsPredicateDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`.[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `// same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * `// NOTE: there's a `[DataFrame.get][org.jetbrains.kotlinx.dataframe.DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ ... }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// use `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` instead`
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`.[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { ... } }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public fun SingleColumn<AnyRow>.cols(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<*> = colsInternal(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`.[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `// same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * `// NOTE: there's a `[DataFrame.get][org.jetbrains.kotlinx.dataframe.DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ ... }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// use `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` instead`
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`.[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { ... } }`
     *
     * @see [all]
     *
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public operator fun SingleColumn<AnyRow>.get(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<Any?> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "myGroupCol".`[cols][cols]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][select]` { "myGroupCol"`[`[`][cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `// same as `[all][all]
     *
     * `df.`[select][select]` { "myGroupCol".`[cols][cols]`() }`
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    private interface StringColsPredicateDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myGroupCol".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myGroupCol"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myGroupCol".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public fun String.cols(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<*> = colGroup(this).cols(predicate)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myGroupCol".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myGroupCol"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myGroupCol".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public operator fun String.get(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<Any?> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "pathTo"["myGroupCol"].`[cols][cols]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][select]` { "pathTo"["myGroupCol"]`[`[`][cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `// identity call, same as `[all][all]
     *
     * `df.`[select][select]` { "pathTo"["myGroupCol"].`[cols][cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    private interface ColumnPathColsPredicateDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["myGroupCol"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["myGroupCol"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// identity call, same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["myGroupCol"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public fun ColumnPath.cols(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<*> = colGroup(this).cols(predicate)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["myGroupCol"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["myGroupCol"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// identity call, same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["myGroupCol"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public operator fun ColumnPath.get(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<Any?> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { Type::columnGroup.`[cols][cols]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][select]` { Type::columnGroup`[`[`][cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][cols]` }`
     *
     * `// identity call, same as `[all][all]
     *
     * `df.`[select][select]` { Type::columnGroup.`[cols][cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    private interface KPropertyColsPredicateDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::columnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::columnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// identity call, same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::columnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public fun KProperty<*>.cols(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<*> = colGroup(this).cols(predicate)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::columnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::columnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// identity call, same as `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::columnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     * @see [all]
     *
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     */
    public operator fun KProperty<*>.get(
        predicate: ColumnFilter<*> = { true },
    ): ColumnSet<Any?> = cols(predicate)

    // endregion

    // region references

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface ColumnSetColsVarargColumnReferenceDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> ColumnSet<C>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = transformWithContext {
        dataFrameOf(it)
            .asColumnGroup()
            .cols(firstCol, *otherCols)
            .resolve(this)
    }

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { `[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][select]` { `[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][select]` { this`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     * `df.`[select][select]` { myColumnGroup.`[cols][cols]`(columnA, columnB) }`
     *
     * `// NOTE: there's a `[DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][select]` { myColumnGroup`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface SingleColumnColsVarargColumnReferenceDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `// NOTE: there's a `[DataFrame.get][org.jetbrains.kotlinx.dataframe.DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> SingleColumn<AnyRow>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { refs ->
        transform {
            it.flatMap { col -> refs.mapNotNull { col.getChild(it) } }
        }
    }

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `// NOTE: there's a `[DataFrame.get][org.jetbrains.kotlinx.dataframe.DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> SingleColumn<AnyRow>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "myColumnGroup".`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][select]` { "myColumnGroup".`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][select]` { "myColumnGroup"`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface StringColsVarargColumnReferenceDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> String.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> String.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "pathTo"["columnGroup"].`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][select]` { "pathTo"["columnGroup"].`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][select]` { "pathTo"["columnGroup"]`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface ColumnPathColsVarargColumnReferenceDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> ColumnPath.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> ColumnPath.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[cols][cols]`(columnA, columnB) }`
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[cols][cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][select]` { Type::myColumnGroup`[`[`][cols]`columnA, columnB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface KPropertyColsVarargColumnReferenceDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> KProperty<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> KProperty<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region names

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface ColumnSetColsVarargStringDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<C> = transformWithContext {
        dataFrameOf(it)
            .asColumnGroup()
            .cols(firstCol, *otherCols)
            .resolve(this) as List<ColumnWithPath<C>>
    }

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { `[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][select]` { this`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     *
     * `df.`[select][select]` { myColumnGroup.`[cols][cols]`("columnA", "columnB") }`
     *
     * `// NOTE: there's a `[DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][select]` { myColumnGroup`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface SingleColumnColsVarargStringDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `// NOTE: there's a `[DataFrame.get][org.jetbrains.kotlinx.dataframe.DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun SingleColumn<AnyRow>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = headPlusArray(firstCol, otherCols).let { names ->
        transform { it.flatMap { col -> names.mapNotNull { col.getChild(it) } } }
    }

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `// NOTE: there's a `[DataFrame.get][org.jetbrains.kotlinx.dataframe.DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun SingleColumn<AnyRow>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "columnGroup".`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][select]` { "columnGroup"`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface StringColsVarargStringDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "columnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "columnGroup"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun String.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "columnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "columnGroup"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun String.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "pathTo"["columnGroup"].`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][select]` { "pathTo"["columnGroup"]`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface ColumnPathColsVarargStringDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun ColumnPath.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun ColumnPath.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[cols][cols]`("columnA", "columnB") }`
     *
     * `df.`[select][select]` { Type::myColumnGroup`[`[`][cols]`"columnA", "columnB"`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface KPropertiesColsVarargStringDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun KProperty<*>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a column.
     * @param [otherCols] Optional additional [String]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun KProperty<*>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    // endregion

    // region properties

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>().`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][select]` { `[colsOf][colsOf]`<`[String][String]`>()`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface ColumnSetColsVarargKPropertyDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> ColumnSet<C>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = transformWithContext {
        dataFrameOf(it)
            .asColumnGroup()
            .cols(firstCol, *otherCols)
            .resolve(this)
    }

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { `[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][select]` { this`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     *
     * `df.`[select][select]` { myColumnGroup.`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][select]` { myColumnGroup`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface SingleColumnColsVarargKPropertyDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> SingleColumn<AnyRow>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { props ->
        transform { it.flatMap { col -> props.mapNotNull { col.getChild(it) } } }
    }

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> SingleColumn<AnyRow>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "myColumnGroup".`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][select]` { "myColumnGroup"`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface StringColsVarargKPropertyDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> String.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup".`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "myColumnGroup"`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> String.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { "pathTo"["columnGroup"].`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][select]` { "pathTo"["columnGroup"]`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface ColumnPathColsVarargKPropertyDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> ColumnPath.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"].`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> ColumnPath.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][select]` { Type::myColumnGroup.`[cols][cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][select]` { Type::myColumnGroup`[`[`][cols]`Type::colA, Type::colB`[`]`][cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    private interface KPropertyColsVarargKPropertyDocs

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public fun <C> KProperty<*>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from a parent [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], -[ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], or -[DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] or any of the `vararg` overloads for all
     * [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.hasNulls]`() } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`1, 3, 5] }`
     *
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a column.
     * @param [otherCols] Optional additional [KProperty]s that point to columns.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to. 
     */
    public operator fun <C> KProperty<*>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region indices

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<C> = transformWithContext {
        dataFrameOf(it)
            .asColumnGroup()
            .cols(firstIndex, *otherIndices)
            .resolve(this) as List<ColumnWithPath<C>>
    }

    public operator fun <C> ColumnSet<C>.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<C> = cols(firstIndex, *otherIndices)

    public fun SingleColumn<AnyRow>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = headPlusArray(firstIndex, otherIndices).let { indices ->
        transform { it.flatMap { it.children().let { children -> indices.map { children[it] } } } }
    }

    /**
     *
     */
    public operator fun SingleColumn<AnyRow>.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    public fun String.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = colGroup(this).cols(firstIndex, *otherIndices)

    public operator fun String.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    public fun ColumnPath.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = colGroup(this).cols(firstIndex, *otherIndices)

    public operator fun ColumnPath.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    public fun KProperty<*>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = colGroup(this).cols(firstIndex, *otherIndices)

    public operator fun KProperty<*>.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = cols(firstIndex, *otherIndices)

    // endregion

    // region ranges

    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(range: IntRange): ColumnSet<C> =
        transformWithContext {
            dataFrameOf(it)
                .asColumnGroup()
                .cols(range)
                .resolve(this) as List<ColumnWithPath<C>>
        }

    public operator fun <C> ColumnSet<C>.get(range: IntRange): ColumnSet<C> = cols(range)

    public fun SingleColumn<AnyRow>.cols(range: IntRange): ColumnSet<*> =
        transform { it.flatMap { it.children().subList(range.first, range.last + 1) } }

    /**
     *
     */
    public operator fun SingleColumn<AnyRow>.get(range: IntRange): ColumnSet<*> = cols(range)

    public fun String.cols(range: IntRange): ColumnSet<*> = colGroup(this).cols(range)

    public operator fun String.get(range: IntRange): ColumnSet<*> = cols(range)

    public fun ColumnPath.cols(range: IntRange): ColumnSet<*> = colGroup(this).cols(range)

    public operator fun ColumnPath.get(range: IntRange): ColumnSet<*> = cols(range)

    public fun KProperty<*>.cols(range: IntRange): ColumnSet<*> = colGroup(this).cols(range)

    public operator fun KProperty<*>.get(range: IntRange): ColumnSet<*> = cols(range)

    // endregion

    // endregion

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

    // region dfs

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<Any?> = dfsInternal(predicate)

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun String.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> = toColumnAccessor().dfs(predicate)

    @Deprecated(
        message = "dfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.cols(predicate).recursively()"),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> KProperty<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        toColumnAccessor().dfs(predicate)

    // endregion

    // region all
    public fun ColumnSet<*>.all(): ColumnSet<*> = wrap()

    public fun SingleColumn<*>.all(): ColumnSet<*> = transformSingle { it.children() }

    public fun String.all(): ColumnSet<*> = toColumnAccessor().transformSingle { it.children() }

    public fun KProperty<*>.all(): ColumnSet<*> = toColumnAccessor().transformSingle { it.children() }

    // region allDfs

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.all().recursively(includeGroups)"),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnSet<*>.allDfs(includeGroups: Boolean = false): ColumnSet<Any?> =
        if (includeGroups) dfs { true } else dfs { !it.isColumnGroup() }

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.all().recursively(includeGroups)"),
        level = DeprecationLevel.WARNING,
    )
    public fun String.allDfs(includeGroups: Boolean = false): ColumnSet<Any?> = toColumnAccessor().allDfs(includeGroups)

    @Deprecated(
        message = "allDfs is deprecated, use recursively instead.",
        replaceWith = ReplaceWith("this.all().recursively(includeGroups)"),
        level = DeprecationLevel.WARNING,
    )
    public fun KProperty<*>.allDfs(includeGroups: Boolean = false): ColumnSet<Any?> =
        toColumnAccessor().allDfs(includeGroups)

    public fun <C> ColumnSet<C>.recursively(includeGroups: Boolean = true): ColumnSet<C> = object : ColumnSet<C> {

        private fun flatten(columnSet: ColumnSet<*>): ColumnSet<*> = columnSet.transform { list ->
            list
                .filter { it.isColumnGroup() } // TODO should I include this from dfs?
                .flatMap {
                    it.children()
                        .dfs()
                        .filter { includeGroups || !it.isColumnGroup() }
                }
        }

        override fun resolve(
            context: ColumnResolutionContext,
        ): List<ColumnWithPath<C>> = this@recursively
            .resolveAfterTransform(context = context, transform = ::flatten)

        override fun resolveAfterTransform(
            context: ColumnResolutionContext,
            transform: (ColumnSet<*>) -> ColumnSet<*>,
        ): List<ColumnWithPath<C>> =
            transform(this@recursively).cast<C>()
                .resolveAfterTransform(context = context, transform = ::flatten)
    }

    public fun <C> ColumnSet<C>.rec(includeGroups: Boolean = true): ColumnSet<C> = recursively(includeGroups)

    public fun <C> ColumnSet<C>.allRecursively(includeGroups: Boolean = true): ColumnSet<C> =
        wrap().recursively(includeGroups = includeGroups)

    public fun <C> ColumnSet<C>.allRec(includeGroups: Boolean = true): ColumnSet<C> =
        allRecursively(includeGroups = includeGroups)


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

    public fun KProperty<*>.allBefore(colPath: ColumnPath): ColumnSet<Any?> =
        toColumnAccessor().allBefore(colPath)

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

    public fun SingleColumn<AnyRow>.nameContains(text: CharSequence): ColumnSet<*> = cols { it.name.contains(text) }
    public fun <C> ColumnSet<C>.nameContains(text: CharSequence): ColumnSet<C> = cols { it.name.contains(text) }
    public fun SingleColumn<AnyRow>.nameContains(regex: Regex): ColumnSet<*> = cols { it.name.contains(regex) }
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): ColumnSet<C> = cols { it.name.contains(regex) }
    public fun SingleColumn<AnyRow>.startsWith(prefix: CharSequence): ColumnSet<*> = cols { it.name.startsWith(prefix) }
    public fun <C> ColumnSet<C>.startsWith(prefix: CharSequence): ColumnSet<C> = cols { it.name.startsWith(prefix) }
    public fun SingleColumn<AnyRow>.endsWith(suffix: CharSequence): ColumnSet<*> = cols { it.name.endsWith(suffix) }
    public fun <C> ColumnSet<C>.endsWith(suffix: CharSequence): ColumnSet<C> = cols { it.name.endsWith(suffix) }

    public fun <C> ColumnSet<C>.except(vararg other: ColumnSet<*>): ColumnSet<*> = except(other.toColumnSet())
    public fun <C> ColumnSet<C>.except(vararg other: String): ColumnSet<*> = except(other.toColumnSet())

    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C> =
        transform { it.filter { !it.hasNulls() } } as ColumnSet<C>

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
     * #### For example:
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
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ## This Cols Of Overload
     * Get sub-columns of the column with this name by [type] with a [filter].
     * #### For example:
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
     * #### For example:
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
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     * ## This Cols Of Overload
     * Get sub-columns of the column this [KProperty Accessor][KProperty] points to by [type] with or without [filter].
     * #### For example:
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
 * #### For example:
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

internal fun ColumnSet<*>.colsInternal(predicate: ColumnFilter<*>) =
    transform { it.flatMap { it.children().filter { predicate(it) } } }

@Deprecated("Replaced with recursively()")
internal fun ColumnSet<*>.dfsInternal(predicate: (ColumnWithPath<*>) -> Boolean) =
    transform { it.filter { it.isColumnGroup() }.flatMap { it.children().dfs().filter(predicate) } }

@Deprecated(
    message = "Use recursively() instead",
    replaceWith = ReplaceWith(
        "this.colsOf(type, predicate).recursively()",
        "org.jetbrains.kotlinx.dataframe.columns.recursively",
        "org.jetbrains.kotlinx.dataframe.columns.recursively",
        "org.jetbrains.kotlinx.dataframe.api.colsOf",
    ),
)
public fun <C> ColumnSet<*>.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
    dfsInternal { it.isSubtypeOf(type) && predicate(it.cast()) }

@Deprecated(
    message = "Use recursively() instead",
    replaceWith = ReplaceWith(
        "this.colsOf<C>(filter).recursively()",
        "org.jetbrains.kotlinx.dataframe.columns.recursively",
        "org.jetbrains.kotlinx.dataframe.api.colsOf",
    ),
)
public inline fun <reified C> ColumnSet<*>.dfsOf(noinline filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<C> =
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
 *
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
 * #### For example:
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
 *
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
 * #### For example:
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
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## This Cols Of Overload
 * Get (sub-)columns by [type] with or without [filter].
 * #### For example:
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
 * #### For example:
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
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 * ## This Cols Of Overload
 * Get (sub-)columns by a given type with or without [filter].
 * #### For example:
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
