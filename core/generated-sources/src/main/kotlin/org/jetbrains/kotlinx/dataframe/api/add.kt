package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.resolveSingle
import org.jetbrains.kotlinx.dataframe.util.ADD_VARARG_COLUMNS
import org.jetbrains.kotlinx.dataframe.util.ADD_VARARG_COLUMNS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.ADD_VARARG_FRAMES
import org.jetbrains.kotlinx.dataframe.util.ADD_VARARG_FRAMES_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region Add existing columns

/**
 * Adds new [<code>columns</code>][columns] to the end of this [<code>DataFrame</code>][DataFrame] (at the top level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the new [<code>columns</code>][columns] appended to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * @param columns columns to add.
 * @throws [DuplicateColumnNamesException] if columns in an expected result have repeated names.
 * @throws [UnequalColumnSizesException] if columns in an expected result have different sizes.
 * @return new [<code>DataFrame</code>][DataFrame] with added columns.
 */
@Deprecated(
    message = ADD_VARARG_COLUMNS,
    replaceWith = ReplaceWith(ADD_VARARG_COLUMNS_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun <T> DataFrame<T>.add(vararg columns: BaseColumn<*>): DataFrame<T> = addAll(columns.asIterable())

/**
 * Adds new [<code>columns</code>][columns] to the end of this [<code>DataFrame</code>][DataFrame] (at the top level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the new [<code>columns</code>][columns] appended to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * @param columns columns to add.
 * @throws [DuplicateColumnNamesException] if columns in an expected result have repeated names.
 * @throws [UnequalColumnSizesException] if columns in an expected result have different sizes.
 * @return new [<code>DataFrame</code>][DataFrame] with added columns.
 */
public fun <T> DataFrame<T>.addAll(vararg columns: BaseColumn<*>): DataFrame<T> = addAll(columns.asIterable())

/**
 * Adds new [<code>columns</code>][columns] to the end of this [<code>DataFrame</code>][DataFrame] (at the top level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the new [<code>columns</code>][columns] appended to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html).
 *
 * @param columns columns to add.
 * @throws [DuplicateColumnNamesException] if columns in an expected result have repeated names.
 * @throws [UnequalColumnSizesException] if columns in an expected result have different sizes.
 * @return new [<code>DataFrame</code>][DataFrame] with added columns.
 */
public fun <T> DataFrame<T>.addAll(columns: Iterable<BaseColumn<*>>): DataFrame<T> =
    dataFrameOf(columns() + columns).cast()

/**
 * Adds all columns from the given [<code>dataFrames</code>][dataFrames] to the end of this [<code>DataFrame</code>][DataFrame] (at the top level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the columns from the specified
 * [<code>dataFrames</code>][dataFrames] appended to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * @param dataFrames dataFrames to get columns from.
 * @throws [DuplicateColumnNamesException] if columns in an expected result have repeated names.
 * @throws [UnequalColumnSizesException] if columns in an expected result have different sizes.
 * @return new [<code>DataFrame</code>][DataFrame] with added columns.
 */
@Deprecated(
    message = ADD_VARARG_FRAMES,
    replaceWith = ReplaceWith(ADD_VARARG_FRAMES_REPLACE),
    level = DeprecationLevel.WARNING,
)
public fun <T> DataFrame<T>.add(vararg dataFrames: DataFrame<*>): DataFrame<T> = addAll(dataFrames.asIterable())

/**
 * Adds all columns from the given [<code>dataFrames</code>][dataFrames] to the end of this [<code>DataFrame</code>][DataFrame] (at the top level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the columns from the specified
 * [<code>dataFrames</code>][dataFrames] appended to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * @param dataFrames dataFrames to get columns from.
 * @throws [DuplicateColumnNamesException] if columns in an expected result have repeated names.
 * @throws [UnequalColumnSizesException] if columns in an expected result have different sizes.
 * @return new [<code>DataFrame</code>][DataFrame] with added columns.
 */
@Refine
@Interpretable("DataFrameAddAll")
public fun <T> DataFrame<T>.addAll(vararg dataFrames: DataFrame<*>): DataFrame<T> = addAll(dataFrames.asIterable())

/**
 * Adds all columns from the given [<code>dataFrames</code>][dataFrames] to the end of this [<code>DataFrame</code>][DataFrame] (at the top level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the columns from the specified
 * [<code>dataFrames</code>][dataFrames] appended to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * @param dataFrames dataFrames to get columns from.
 * @throws [DuplicateColumnNamesException] if columns in an expected result have repeated names.
 * @throws [UnequalColumnSizesException] if columns in an expected result have different sizes.
 * @return new [<code>DataFrame</code>][DataFrame] with added columns.
 */
@JvmName("addAllFrames")
public fun <T> DataFrame<T>.addAll(dataFrames: Iterable<DataFrame<*>>): DataFrame<T> =
    addAll(dataFrames.flatMap { it.columns() })

// endregion

// region Create and add a single column

/**
 * Receiver that is used by the [<code>AddExpression</code>][AddExpression] (for instance in the [<code>add</code>][add] and [<code>update</code>][update] operations)
 * to access new (added or updated) column value in preceding row.
 */
@HasSchema(schemaArg = 0)
public interface AddDataRow<out T> : DataRow<T> {

    /**
     * Returns a new value that was already computed for some preceding row during current [<code>add</code>][add] or [<code>update</code>][update] column operation.
     *
     * Can be used to compute series of values with recurrence relations, e.g. fibonacci.
     *
     * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
     *
     * @throws IndexOutOfBoundsException when called on a successive row that doesn't have new value yet
     */
    public fun <C> DataRow<*>.newValue(): C
}

/**
 * [<code>AddExpression</code>][AddExpression] is used to express or select any instance of `R` using the given instance of [<code>AddDataRow</code>][AddDataRow]`<T>` as
 * `this` and `it`.
 *
 * Shorthand for:
 * ```kotlin
 * AddDataRow<T>.(it: AddDataRow<T>) -> R
 * ```
 */
public typealias AddExpression<T, R> = Selector<AddDataRow<T>, R>

/**
 * Creates a new column using an [<code>AddExpression</code>][AddExpression] and
 * adds a new column to the end of this [<code>DataFrame</code>][DataFrame] (at the top level).
 *
 * With an [<code>AddExpression</code>][org.jetbrains.kotlinx.dataframe.api.AddExpression], you define the value that each row in the new column should have.
 * This can be based on values from the same row in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * You can also use functions like [<code>prev</code>][org.jetbrains.kotlinx.dataframe.api.prev] and [<code>next</code>][org.jetbrains.kotlinx.dataframe.api.next] to access other rows, and combine them with
 * [<code>newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue] to reference values already computed in the new column.
 * For example, use `prev().newValue()` to access the new column value from the previous row.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the new column appended to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * ## Example
 *
 * ```kotlin
 * // Add a new column "sum" that contains the sum of values from the "firstValue"
 * // and "secondValue" columns for each row.
 * val dfWithSum = df.add("sum") { firstValue + secondValue }
 *
 * // Add a "fibonacci" column with the Fibonacci sequence:
 * // for the first two rows, the value is 1;
 * // for subsequent rows, it's the sum of the two previous Fibonacci values.
 * val dfWithFibonacci = df.add("fibonacci") {
 *     if (index() < 2) 1
 *     else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
 * }
 * ```
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * @param name name for a new column.
 * If it is empty, a unique column name will be generated.
 * Otherwise, it should be unique for original [<code>DataFrame</code>][DataFrame].
 * @param infer a value of [<code>Infer</code>][Infer] that specifies how to compute column [<code>type</code>][BaseColumn.type] for a new column.
 * Defaults to [<code>Infer.Nulls</code>][Infer.Nulls].
 * @param expression [<code>AddExpression</code>][AddExpression] that computes column value for every [<code>DataRow</code>][DataRow] of a new column.
 * @return new [<code>DataFrame</code>][DataFrame] with added column.
 *
 * @throws DuplicateColumnNamesException if [<code>DataFrame</code>][DataFrame] already contains a column with given [<code>name</code>][name]
 */
@Refine
@Interpretable("Add")
public inline fun <reified R, T> DataFrame<T>.add(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataFrame<T> = (this + mapToColumn(name, infer, expression))

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <reified R, T> DataFrame<T>.add(
    property: KProperty<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataFrame<T> = (this + mapToColumn(property, infer, expression))

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <reified R, T> DataFrame<T>.add(
    column: ColumnAccessor<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataFrame<T> = add(column.path(), infer, expression)

/**
 * Creates a new column using [<code>AddExpression</code>][AddExpression] and inserts it at the specified [<code>ColumnPath</code>][ColumnPath].
 *
 * With an [<code>AddExpression</code>][org.jetbrains.kotlinx.dataframe.api.AddExpression], you define the value that each row in the new column should have.
 * This can be based on values from the same row in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * You can also use functions like [<code>prev</code>][org.jetbrains.kotlinx.dataframe.api.prev] and [<code>next</code>][org.jetbrains.kotlinx.dataframe.api.next] to access other rows, and combine them with
 * [<code>newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue] to reference values already computed in the new column.
 * For example, use `prev().newValue()` to access the new column value from the previous row.
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the new column inserted at the given [<code>path</code>][path].
 *
 *
 * If the specified path is partially or fully missing — that is, if any segment of the path
 * does not correspond to an existing column or column group — all missing parts will be created automatically.
 *
 * ## Example
 *
 * ```kotlin
 * // Add a new column "sum" inside the "info" column group (which will be created if it doesn't exist).
 * // The column contains the sum of values from the "firstValue" and "secondValue" columns for each row.
 * val dfWithSum = df.add(pathOf("info", "sum")) { firstValue + secondValue }
 * ```
 *
 * @param path Target [<code>ColumnPath</code>][ColumnPath] for the new column.
 * If it points to a nested location,
 * intermediate columns will be created if necessary.
 * @param infer A value of [<code>Infer</code>][Infer] that specifies how to compute the column [<code>type</code>][BaseColumn.type] for the new column.
 * Defaults to [<code>Infer.Nulls</code>][Infer.Nulls].
 * @param expression An [<code>AddExpression</code>][AddExpression] that computes the column value for every [<code>DataRow</code>][DataRow] of the new column.
 * @return A new [<code>DataFrame</code>][DataFrame] with the added column.
 *
 * @throws DuplicateColumnNamesException If the [<code>DataFrame</code>][DataFrame] already contains a column at the specified [<code>path</code>][path].
 */
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

/**
 * Receiver that is used by the [<code>add</code>][add] and [<code>mapToFrame</code>][mapToFrame]
 * for adding new columns and column groups based on [<code>DataFrame</code>][DataFrame] columns and row values.
 */
public class AddDsl<T>(
    @PublishedApi internal val df: DataFrame<T>,
) : ColumnsContainer<T> by df,
    ColumnSelectionDsl<T> {

    // TODO: support adding column into path
    @PublishedApi
    internal val columns: MutableList<AnyCol> = mutableListOf<AnyCol>()

    public fun add(column: AnyColumnReference): Boolean = columns.add(column.resolveSingle(df)!!.data)

    @Interpretable("AddDslReferencePlus")
    public operator fun AnyColumnReference.unaryPlus(): Boolean = add(this)

    public operator fun String.unaryPlus(): Boolean = add(df[this])

    @PublishedApi
    internal inline fun <reified R> add(
        name: String,
        infer: Infer = Infer.Nulls,
        noinline expression: AddExpression<T, R>,
    ): Boolean = add(df.mapToColumn(name, infer, expression))

    public inline fun <reified R> expr(
        infer: Infer = Infer.Nulls,
        noinline expression: AddExpression<T, R>,
    ): DataColumn<R> = df.mapToColumn("", infer, expression)

    @Interpretable("From")
    public inline infix fun <reified R> String.from(noinline expression: AddExpression<T, R>): Boolean =
        add(this, Infer.Nulls, expression)

    // TODO: use path instead of name
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> ColumnAccessor<R>.from(noinline expression: AddExpression<T, R>): Boolean =
        name().from(expression)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(noinline expression: AddExpression<T, R>): Boolean =
        add(name, Infer.Nulls, expression)

    public infix fun String.from(column: AnyColumnReference): Boolean = add(column.rename(this))

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> ColumnAccessor<R>.from(column: ColumnReference<R>): Boolean = name() from column

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> KProperty<R>.from(column: ColumnReference<R>): Boolean = name from column

    @Interpretable("Into")
    public infix fun AnyColumnReference.into(name: String): Boolean = add(rename(name))

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <R> ColumnReference<R>.into(column: ColumnAccessor<R>): Boolean = into(column.name())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <R> ColumnReference<R>.into(column: KProperty<R>): Boolean = into(column.name)

    @Interpretable("AddDslStringInvoke")
    public operator fun String.invoke(body: AddDsl<T>.() -> Unit): Unit = group(this, body)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun AnyColumnGroupAccessor.from(body: AddDsl<T>.() -> Unit): Unit = group(this, body)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun group(column: AnyColumnGroupAccessor, body: AddDsl<T>.() -> Unit): Unit = group(column.name(), body)

    @Interpretable("AddDslNamedGroup")
    public fun group(name: String, body: AddDsl<T>.() -> Unit) {
        val dsl = AddDsl(df)
        body(dsl)
        add(dsl.columns.toColumnGroup(name))
    }

    @Interpretable("AddDslAddGroup")
    public fun group(body: AddDsl<T>.() -> Unit): AddGroup<T> = AddGroup(body)

    @Interpretable("AddDslAddGroupInto")
    public infix fun AddGroup<T>.into(groupName: String): Unit = group(groupName, body)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun AddGroup<T>.into(column: AnyColumnGroupAccessor): Unit = into(column.name())
}

/**
 * Creates new columns using the [<code>AddDsl</code>][AddDsl] builder.
 *
 * An [<code>AddDsl</code>][AddDsl] allows to add multiple new columns and column groups to a [<code>DataFrame</code>][DataFrame]
 * using concise syntax based on `from`, `into` operations and [<code>AddExpression</code>][AddExpression]s.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the newly added columns.
 *
 * ## Example
 *
 * ```kotlin
 * val dfWithAdded = df.add {
 *     // Add new column "yearOfBirth" computed as 2021 minus value in "age" column
 *     "yearOfBirth" from { 2021 - age }
 *
 *     // Add column "is adult" with result of age > 18
 *     age > 18 into "is adult"
 *
 *     // Add new column "role" using expression
 *     expr { if ( department == "IT") "developer" else "analyst" } into "role"
 *
 *     // Add column group "details"
 *     group("details") {
 *         // Add column "last name length" with length of lastName
 *         name.lastName.length() into "last name length"
 *
 *         // Add column "full name" by combining firstName and lastName
 *         "full name" from { name.firstName + " " + name.lastName }
 *     }
 * }
 * ```
 *
 * For more information: [See `add` on the documentation website.](https://kotlin.github.io/dataframe/add.html)
 *
 * @param body An [<code>AddDsl</code>][AddDsl] expression used to define new columns and column groups.
 * @return A new [<code>DataFrame</code>][DataFrame] with the added columns.
 */
@Refine
@Interpretable("AddWithDsl")
public fun <T> DataFrame<T>.add(body: AddDsl<T>.() -> Unit): DataFrame<T> {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(this@add.columns() + dsl.columns).cast()
}

/**
 * Creates a new column using [<code>AddExpression</code>][AddExpression] and
 * adds a new column to the end of each group (i.e., [<code>DataFrame</code>][DataFrame]s) of this [<code>GroupBy</code>][GroupBy] (at the top level).
 *
 * With an [<code>AddExpression</code>][org.jetbrains.kotlinx.dataframe.api.AddExpression], you define the value that each row in the new column should have.
 * This can be based on values from the same row in the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * You can also use functions like [<code>prev</code>][org.jetbrains.kotlinx.dataframe.api.prev] and [<code>next</code>][org.jetbrains.kotlinx.dataframe.api.next] to access other rows, and combine them with
 * [<code>newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue] to reference values already computed in the new column.
 * For example, use `prev().newValue()` to access the new column value from the previous row.
 *
 * Returns a new [<code>GroupBy</code>][GroupBy] with the new column
 * appended to each group [<code>DataFrame</code>][DataFrame] to the original list of [<code>DataFrame.columns</code>][DataFrame.columns].
 *
 * Check out [<code>`GroupBy grammar`</code>][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * ## Example
 *
 * ```kotlin
 * // Add a new column "sum" that contains the sum of values from the "firstValue"
 * // and "secondValue" columns for each row.
 * val gbWithSum = gb.add("sum") { firstValue + secondValue }
 *
 * // Add a "fibonacci" column with the Fibonacci sequence:
 * // for the first two rows, the value is 1;
 * // for subsequent rows, it's the sum of the two previous Fibonacci values.
 * val gbWithFibonacci = gb.add("fibonacci") {
 *     if (index() < 2) 1
 *     else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
 * }
 * ```
 *
 * @param name name for a new column.
 * If it is empty, a unique column name will be generated.
 * Otherwise, it should be unique for original group [<code>DataFrame</code>][DataFrame]s.
 * @param infer a value of [<code>Infer</code>][Infer] that specifies how to compute column [<code>type</code>][BaseColumn.type] for a new column.
 * Defaults to [<code>Infer.Nulls</code>][Infer.Nulls].
 * @param expression [<code>AddExpression</code>][AddExpression] that computes column value for every [<code>DataRow</code>][DataRow] of a new column.
 * @return new [<code>GroupBy</code>][GroupBy] with added column.
 *
 * @throws DuplicateColumnNamesException if group [<code>DataFrame</code>][DataFrame]s already contains a column with given [<code>name</code>][name].
 */
@Refine
@Interpretable("GroupByAdd")
public inline fun <reified R, T, G> GroupBy<T, G>.add(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<G, R>,
): GroupBy<T, G> = updateGroups { add(name, infer, expression) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <reified R, T, G> GroupBy<T, G>.add(
    column: ColumnAccessor<G>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<G, R>,
): GroupBy<T, G> = add(column.name(), infer, expression)

public class AddGroup<T>(internal val body: AddDsl<T>.() -> Unit)

// endregion
