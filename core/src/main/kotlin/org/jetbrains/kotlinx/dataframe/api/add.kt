package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.resolveSingle
import kotlin.reflect.KProperty

/*
 * `add` operation adds new columns to DataFrame.
 */

// region Add existing columns

/**
 * Creates new [DataFrame] with given columns added to the end of original [DataFrame.columns] list.
 *
 * Original [DataFrame] is not modified.
 *
 * @param columns columns to add
 * @throws [DuplicateColumnNamesException] if columns in expected result have repeated names
 * @throws [UnequalColumnSizesException] if columns in expected result have different sizes
 * @return new [DataFrame] with added columns
 */
public fun <T> DataFrame<T>.add(vararg columns: AnyBaseCol): DataFrame<T> = addAll(columns.asIterable())

/**
 * Creates new [DataFrame] with given columns added to the end of original [DataFrame.columns] list.
 *
 * Original [DataFrame] is not modified.
 *
 * @param columns columns to add
 * @throws [DuplicateColumnNamesException] if columns in expected result have repeated names
 * @throws [UnequalColumnSizesException] if columns in expected result have different sizes
 * @return new [DataFrame] with added columns
 */
public fun <T> DataFrame<T>.addAll(columns: Iterable<AnyBaseCol>): DataFrame<T> =
    dataFrameOf(columns() + columns).cast()

/**
 * Creates new [DataFrame] with all columns from given [dataFrames] added to the end of original [DataFrame.columns] list.
 *
 * Original [DataFrame] is not modified.
 *
 * @param dataFrames dataFrames to get columns from
 * @throws [DuplicateColumnNamesException] if columns in expected result have repeated names
 * @throws [UnequalColumnSizesException] if columns in expected result have different sizes
 * @return new [DataFrame] with added columns
 */
public fun <T> DataFrame<T>.add(vararg dataFrames: AnyFrame): DataFrame<T> = addAll(dataFrames.asIterable())

/**
 * Creates new [DataFrame] with all columns from given [dataFrames] added to the end of original [DataFrame.columns] list.
 *
 * Original [DataFrame] is not modified.
 *
 * @param dataFrames dataFrames to get columns from
 * @throws [DuplicateColumnNamesException] if columns in expected result have repeated names
 * @throws [UnequalColumnSizesException] if columns in expected result have different sizes
 * @return new [DataFrame] with added columns
 */
@JvmName("addAllFrames")
public fun <T> DataFrame<T>.addAll(dataFrames: Iterable<AnyFrame>): DataFrame<T> =
    addAll(dataFrames.flatMap { it.columns() })

// endregion

// region Create and add a single column

/**
 * Receiver that is used by the [AddExpression] (for instance in the [add] and [update] operations)
 * to access new (added or updated) column value in preceding row.
 */
public interface AddDataRow<out T> : DataRow<T> {

    /**
     * Returns a new value that was already computed for some preceding row during current [add] or [update] column operation.
     *
     * Can be used to compute series of values with recurrence relations, e.g. fibonacci.
     *
     * @throws IndexOutOfBoundsException when called on a successive row that doesn't have new value yet
     */
    public fun <C> AnyRow.newValue(): C
}

/**
 * [AddExpression] is used to express or select any instance of `R` using the given instance of [AddDataRow]`<T>` as
 * `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * AddDataRow<T>.(it: AddDataRow<T>) -> R
 * ```
 */
public typealias AddExpression<T, R> = Selector<AddDataRow<T>, R>

/**
 * Creates new column using row [expression] and adds it to the end of [DataFrame]
 *
 * Original [DataFrame] is not modified.
 *
 * @param name name for a new column. If it is empty, a unique column name will be generated. Otherwise, it should be unique for original [DataFrame].
 * @param infer a value of [Infer] that specifies how to compute column [type][BaseColumn.type] for a new column
 * @param expression [AddExpression] that computes column value for every [DataRow]
 * @return new [DataFrame] with added column
 * @throws DuplicateColumnNamesException if [DataFrame] already contains a column with given [name]
 */
@Refine
@Interpretable("Add")
public inline fun <reified R, T> DataFrame<T>.add(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataFrame<T> = (this + mapToColumn(name, infer, expression))

@AccessApiOverload
public inline fun <reified R, T> DataFrame<T>.add(
    property: KProperty<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataFrame<T> = (this + mapToColumn(property, infer, expression))

@AccessApiOverload
public inline fun <reified R, T> DataFrame<T>.add(
    column: ColumnAccessor<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataFrame<T> = add(column.path(), infer, expression)

public inline fun <reified R, T> DataFrame<T>.add(
    path: ColumnPath,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataFrame<T> {
    val col = mapToColumn(path.name(), infer, expression)
    if (path.size == 1) return this + col
    return insertImpl(path, col)
}

// endregion

// region Create and add several columns

public class AddDsl<T>(
    @PublishedApi internal val df: DataFrame<T>,
) : ColumnsContainer<T> by df,
    ColumnSelectionDsl<T> {

    // TODO: support adding column into path
    @PublishedApi
    internal val columns: MutableList<AnyCol> = mutableListOf<AnyCol>()

    public fun add(column: AnyColumnReference): Boolean = columns.add(column.resolveSingle(df)!!.data)

    public operator fun AnyColumnReference.unaryPlus(): Boolean = add(this)

    public operator fun String.unaryPlus(): Boolean = add(df[this])

    @PublishedApi
    internal inline fun <reified R> add(
        name: String,
        infer: Infer = Infer.Nulls,
        noinline expression: RowExpression<T, R>,
    ): Boolean = add(df.mapToColumn(name, infer, expression))

    public inline fun <reified R> expr(
        infer: Infer = Infer.Nulls,
        noinline expression: RowExpression<T, R>,
    ): DataColumn<R> = df.mapToColumn("", infer, expression)

    @Interpretable("From")
    public inline infix fun <reified R> String.from(noinline expression: RowExpression<T, R>): Boolean =
        add(this, Infer.Nulls, expression)

    // TODO: use path instead of name
    @AccessApiOverload
    public inline infix fun <reified R> ColumnAccessor<R>.from(noinline expression: RowExpression<T, R>): Boolean =
        name().from(expression)

    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(noinline expression: RowExpression<T, R>): Boolean =
        add(name, Infer.Nulls, expression)

    public infix fun String.from(column: AnyColumnReference): Boolean = add(column.rename(this))

    @AccessApiOverload
    public inline infix fun <reified R> ColumnAccessor<R>.from(column: ColumnReference<R>): Boolean = name() from column

    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(column: ColumnReference<R>): Boolean = name from column

    @Interpretable("Into")
    public infix fun AnyColumnReference.into(name: String): Boolean = add(rename(name))

    @AccessApiOverload
    public infix fun <R> ColumnReference<R>.into(column: ColumnAccessor<R>): Boolean = into(column.name())

    @AccessApiOverload
    public infix fun <R> ColumnReference<R>.into(column: KProperty<R>): Boolean = into(column.name)

    @Interpretable("AddDslStringInvoke")
    public operator fun String.invoke(body: AddDsl<T>.() -> Unit): Unit = group(this, body)

    @AccessApiOverload
    public infix fun AnyColumnGroupAccessor.from(body: AddDsl<T>.() -> Unit): Unit = group(this, body)

    @AccessApiOverload
    public fun group(column: AnyColumnGroupAccessor, body: AddDsl<T>.() -> Unit): Unit = group(column.name(), body)

    @Interpretable("AddDslNamedGroup")
    public fun group(name: String, body: AddDsl<T>.() -> Unit) {
        val dsl = AddDsl(df)
        body(dsl)
        add(dsl.columns.toColumnGroup(name))
    }

    public fun group(body: AddDsl<T>.() -> Unit): AddGroup<T> = AddGroup(body)

    public infix fun AddGroup<T>.into(groupName: String): Unit = group(groupName, body)

    @AccessApiOverload
    public infix fun AddGroup<T>.into(column: AnyColumnGroupAccessor): Unit = into(column.name())
}

@Refine
@Interpretable("AddWithDsl")
public fun <T> DataFrame<T>.add(body: AddDsl<T>.() -> Unit): DataFrame<T> {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(this@add.columns() + dsl.columns).cast()
}

@Refine
@Interpretable("GroupByAdd")
public inline fun <reified R, T, G> GroupBy<T, G>.add(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<G, R>,
): GroupBy<T, G> = updateGroups { add(name, infer, expression) }

@AccessApiOverload
public inline fun <reified R, T, G> GroupBy<T, G>.add(
    column: ColumnAccessor<G>,
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<G, R>,
): GroupBy<T, G> = add(column.name(), infer, expression)

public class AddGroup<T>(internal val body: AddDsl<T>.() -> Unit)

// endregion
