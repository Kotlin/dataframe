package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.DataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.UNNAMED_COLUMN_PREFIX
import org.jetbrains.kotlinx.dataframe.impl.api.withValuesImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.impl.columns.createComputedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.forceResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.unbox
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.dataframe.util.DATAFRAME_OF_WITH_VALUES
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.reflect.KProperty
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region create ColumnAccessor

// region column

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a column reference:
 * ```kotlin
 * val age by column<Int>()
 * ```
 * The referenced column name is taken from the property name; the [<code>ColumnName</code>][ColumnName] annotation overrides it.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun <T> column(): ColumnDelegate<T> = ColumnDelegate()

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column with the given [<code>name</code>][name].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column.
 */
public fun <T> column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column at the given [<code>path</code>][path].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column.
 */
public fun <T> column(path: ColumnPath): ColumnAccessor<T> = ColumnAccessorImpl(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> column(property: KProperty<T>): ColumnAccessor<T> = ColumnAccessorImpl(property.name)

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a column reference nested in the receiver
 * column group: the referenced column path is the receiver's path with the property name appended:
 * ```kotlin
 * val personal = columnGroup("personal")
 * val age by personal.column<Int>() // references personal/age
 * ```
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun <T> ColumnReference<DataRow<*>>.column(): ColumnDelegate<T> = ColumnDelegate(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column with the given [<code>name</code>][name] nested in the receiver column group:
 * the created path is the receiver's path with [<code>name</code>][name] appended.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column.
 */
public fun <T> ColumnReference<DataRow<*>>.column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(path() + name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column at the given [<code>path</code>][path] nested in the receiver column group:
 * the created path is the receiver's path with [<code>path</code>][path] appended.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column, relative to the receiver.
 */
public fun <T> ColumnReference<DataRow<*>>.column(path: ColumnPath): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.column(property: KProperty<T>): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + property.name)

/**
 * Creates a reference to a column computed from a row by [<code>expression</code>][expression].
 * The reference can be stored and reused in operations like `select` and `add`:
 * ```kotlin
 * // `this` is an untyped DataRow, so the value is cast to Int
 * val isAdult = column<Boolean>("isAdult") { (this["age"] as Int) > 18 }
 * val withColumn = dataFrameOf("age")(20, 15).add { add(isAdult) }
 * ```
 * Inside [<code>expression</code>][expression], `this` is the current [<code>DataRow</code>][DataRow].
 *
 * For more information: [See `expr` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#expr-column-expression)
 *
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [name] Name of the computed column.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] computing the value of the column for each row.
 */
public inline fun <reified T> column(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<Any?, T>,
): ColumnReference<T> = createComputedColumnReference(name, typeOf<T>(), infer, expression)

/**
 * Creates a reference to a column computed from a row by [<code>expression</code>][expression].
 * Inside [<code>expression</code>][expression], `this` is a [<code>DataRow</code>][DataRow]`<T>` typed by the schema marker of [<code>df</code>][df].
 * The resulting reference is not tied to [<code>df</code>][df]: it can be used with any [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `expr` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#expr-column-expression)
 *
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [df] The [<code>DataFrame</code>][DataFrame] whose schema marker types [<code>expression</code>][expression]. It is not used otherwise.
 * @param [name] Name of the computed column.
 * @param [expression] The [<code>RowExpression</code>][RowExpression] computing the value of the column for each row.
 */
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
public inline fun <T, reified C> column(
    df: DataFrame<T>,
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<T, C>,
): ColumnReference<C> = createComputedColumnReference(name, typeOf<C>(), infer, expression as RowExpression<Any?, C>)

// endregion

// region valueColumn

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a value column reference.
 * Without a type argument, the referenced values have type `Any?`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun valueColumn(): ColumnDelegate<Any?> = column()

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a value column reference.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
@JvmName("valueColumnTyped")
public fun <T> valueColumn(): ColumnDelegate<T> = column()

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column with the given [<code>name</code>][name].
 * Without a type argument, the referenced values have type `Any?`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column.
 */
public fun valueColumn(name: String): ColumnAccessor<Any?> = column(name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column with the given [<code>name</code>][name].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column.
 */
@JvmName("valueColumnTyped")
public fun <T> valueColumn(name: String): ColumnAccessor<T> = column(name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column at the given [<code>path</code>][path].
 * Without a type argument, the referenced values have type `Any?`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column.
 */
public fun valueColumn(path: ColumnPath): ColumnAccessor<Any?> = column(path)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column at the given [<code>path</code>][path].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column.
 */
@JvmName("valueColumnTyped")
public fun <T> valueColumn(path: ColumnPath): ColumnAccessor<T> = column(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> valueColumn(property: KProperty<T>): ColumnAccessor<T> = column(property.name)

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a value column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * Without a type argument, the referenced values have type `Any?`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun ColumnReference<DataRow<*>>.valueColumn(): ColumnDelegate<Any?> = ColumnDelegate(this)

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a value column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
@JvmName("valueColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(): ColumnDelegate<T> = ColumnDelegate(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column with the given [<code>name</code>][name] nested in the receiver column group:
 * the created path is the receiver's path with [<code>name</code>][name] appended.
 * Without a type argument, the referenced values have type `Any?`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column.
 */
public fun ColumnReference<DataRow<*>>.valueColumn(name: String): ColumnAccessor<Any?> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column with the given [<code>name</code>][name] nested in the receiver column group:
 * the created path is the receiver's path with [<code>name</code>][name] appended.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column.
 */
@JvmName("valueColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(name: String): ColumnAccessor<T> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column at the given [<code>path</code>][path] nested in the receiver column group:
 * the created path is the receiver's path with [<code>path</code>][path] appended.
 * Without a type argument, the referenced values have type `Any?`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column, relative to the receiver.
 */
public fun ColumnReference<DataRow<*>>.valueColumn(path: ColumnPath): ColumnAccessor<Any?> =
    ColumnAccessorImpl(this.path() + path)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the value column at the given [<code>path</code>][path] nested in the receiver column group:
 * the created path is the receiver's path with [<code>path</code>][path] appended.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column, relative to the receiver.
 */
@JvmName("valueColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(path: ColumnPath): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.valueColumn(property: KProperty<T>): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + property.name)

// endregion

// region columnGroup

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a column group reference.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * Without a type argument, the referenced rows have type [<code>DataRow</code>][DataRow]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun columnGroup(): ColumnDelegate<DataRow<*>> = column()

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a column group reference.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
@JvmName("columnGroupTyped")
public fun <T> columnGroup(): ColumnDelegate<DataRow<T>> = column()

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group with the given [<code>name</code>][name].
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * Without a type argument, the referenced rows have type [<code>DataRow</code>][DataRow]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column group.
 */
public fun columnGroup(name: String): ColumnAccessor<DataRow<*>> = column(name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group with the given [<code>name</code>][name].
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column group.
 */
@JvmName("columnGroupTyped")
public fun <T> columnGroup(name: String): ColumnAccessor<DataRow<T>> = column(name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group at the given [<code>path</code>][path].
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * Without a type argument, the referenced rows have type [<code>DataRow</code>][DataRow]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column group.
 */
public fun columnGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = column(path)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group at the given [<code>path</code>][path].
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column group.
 */
@JvmName("columnGroupTyped")
public fun <T> columnGroup(path: ColumnPath): ColumnAccessor<DataRow<T>> = column(path)

@JvmName("columnGroupDataRowKProperty")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> columnGroup(property: KProperty<DataRow<T>>): ColumnAccessor<DataRow<T>> = column(property)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> columnGroup(property: KProperty<T>): ColumnAccessor<DataRow<T>> = column(property.name)

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a column group reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * Without a type argument, the referenced rows have type [<code>DataRow</code>][DataRow]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun ColumnReference<DataRow<*>>.columnGroup(): ColumnDelegate<DataRow<*>> = ColumnDelegate(this)

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a column group reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
@JvmName("columnGroupTyped")
public fun <T> ColumnReference<DataRow<*>>.columnGroup(): ColumnDelegate<DataRow<T>> = ColumnDelegate(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group with the given [<code>name</code>][name] nested in the receiver column group:
 * the created path is the receiver's path with [<code>name</code>][name] appended.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * Without a type argument, the referenced rows have type [<code>DataRow</code>][DataRow]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column group.
 */
public fun ColumnReference<DataRow<*>>.columnGroup(name: String): ColumnAccessor<DataRow<*>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group with the given [<code>name</code>][name] nested in the receiver column group:
 * the created path is the receiver's path with [<code>name</code>][name] appended.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced column group.
 */
@JvmName("columnGroupTyped")
public fun <T> ColumnReference<DataRow<*>>.columnGroup(name: String): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group at the given [<code>path</code>][path] nested in the receiver column group:
 * the created path is the receiver's path with [<code>path</code>][path] appended.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * Without a type argument, the referenced rows have type [<code>DataRow</code>][DataRow]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column group, relative to the receiver.
 */
public fun ColumnReference<DataRow<*>>.columnGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
    ColumnAccessorImpl(this.path() + path)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the column group at the given [<code>path</code>][path] nested in the receiver column group:
 * the created path is the receiver's path with [<code>path</code>][path] appended.
 * The cells of a column group are themselves rows; see [<code>ColumnGroup</code>][ColumnGroup].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced column group, relative to the receiver.
 */
@JvmName("columnGroupTyped")
public fun <T> ColumnReference<DataRow<*>>.columnGroup(path: ColumnPath): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(this.path() + path)

@JvmName("columnGroupDataRowKProperty")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.columnGroup(property: KProperty<DataRow<T>>): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(this.path() + property.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.columnGroup(property: KProperty<T>): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(this.path() + property.name)

// endregion

// region frameColumn

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a frame column reference.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * Without a type argument, the referenced frames have type [<code>DataFrame</code>][DataFrame]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun frameColumn(): ColumnDelegate<DataFrame<*>> = column()

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a frame column reference.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
@JvmName("frameColumnTyped")
public fun <T> frameColumn(): ColumnDelegate<DataFrame<T>> = column()

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column with the given [<code>name</code>][name].
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * Without a type argument, the referenced frames have type [<code>DataFrame</code>][DataFrame]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced frame column.
 */
public fun frameColumn(name: String): ColumnAccessor<DataFrame<*>> = column(name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column with the given [<code>name</code>][name].
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced frame column.
 */
@JvmName("frameColumnTyped")
public fun <T> frameColumn(name: String): ColumnAccessor<DataFrame<T>> = column(name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column at the given [<code>path</code>][path].
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * Without a type argument, the referenced frames have type [<code>DataFrame</code>][DataFrame]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced frame column.
 */
public fun frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<*>> = column(path)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column at the given [<code>path</code>][path].
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced frame column.
 */
@JvmName("frameColumnTyped")
public fun <T> frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<T>> = column(path)

@JvmName("frameColumnDataFrameKProperty")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> frameColumn(property: KProperty<DataFrame<T>>): ColumnAccessor<DataFrame<T>> = column(property)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> frameColumn(property: KProperty<List<T>>): ColumnAccessor<DataFrame<T>> = column(property.name)

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a frame column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * Without a type argument, the referenced frames have type [<code>DataFrame</code>][DataFrame]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public fun ColumnReference<DataRow<*>>.frameColumn(): ColumnDelegate<DataFrame<*>> = ColumnDelegate(this)

/**
 * Creates a [<code>ColumnDelegate</code>][ColumnDelegate] for the `by` property syntax of a frame column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
@JvmName("frameColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.frameColumn(): ColumnDelegate<DataFrame<T>> = ColumnDelegate(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column with the given [<code>name</code>][name] nested in the receiver column group:
 * the created path is the receiver's path with [<code>name</code>][name] appended.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * Without a type argument, the referenced frames have type [<code>DataFrame</code>][DataFrame]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced frame column.
 */
public fun ColumnReference<DataRow<*>>.frameColumn(name: String): ColumnAccessor<DataFrame<*>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column with the given [<code>name</code>][name] nested in the receiver column group:
 * the created path is the receiver's path with [<code>name</code>][name] appended.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [name] Name of the referenced frame column.
 */
@JvmName("frameColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.frameColumn(name: String): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column at the given [<code>path</code>][path] nested in the receiver column group:
 * the created path is the receiver's path with [<code>path</code>][path] appended.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * Without a type argument, the referenced frames have type [<code>DataFrame</code>][DataFrame]`<*>`.
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced frame column, relative to the receiver.
 */
public fun ColumnReference<DataRow<*>>.frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
    ColumnAccessorImpl(this.path() + path)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column at the given [<code>path</code>][path] nested in the receiver column group:
 * the created path is the receiver's path with [<code>path</code>][path] appended.
 * Each cell of a frame column is a whole [<code>DataFrame</code>][DataFrame]; see [<code>FrameColumn</code>][FrameColumn].
 * The referenced column doesn't have to exist in a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] yet:
 * the accessor can later select it, add it, or update it — for example, in `select`, `add` or `update`.
 *
 * This creates a reference, not a column: to create a column from values, see `columnOf`.
 *
 * References can also be created with the `by` property delegate, which takes the column name
 * from the property name, or with [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.ColumnDelegate.named],
 * which sets the name at the call site.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * @param [path] Path to the referenced frame column, relative to the receiver.
 */
@JvmName("frameColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(this.path() + path)

@JvmName("frameColumnDataFrameKProperty")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.frameColumn(
    property: KProperty<DataFrame<T>>,
): ColumnAccessor<DataFrame<T>> = ColumnAccessorImpl(this.path() + property.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.frameColumn(property: KProperty<List<T>>): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(this.path() + property.name)

// endregion

/**
 * A delegate for the `by` property syntax of column references:
 * ```kotlin
 * val age by column<Int>() // reference to the column named "age"
 * ```
 * The referenced column name is taken from the property name; the [<code>ColumnName</code>][ColumnName] annotation overrides it.
 * When the delegate is created on a parent column group reference, the property name is appended
 * to the parent's path.
 *
 * For more information: [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 */
public class ColumnDelegate<T>(private val parent: ColumnReference<DataRow<*>>? = null) {

    /**
     * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] named after [<code>property</code>][property]; the [<code>ColumnName</code>][ColumnName] annotation overrides the name.
     * With a parent column group reference, the name is appended to its path.
     *
     * @param [property] The property whose name is used for the referenced column.
     */
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = named(property.columnName)

    /**
     * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] with the given [<code>name</code>][name] instead of a property name.
     * With a parent column group reference, [<code>name</code>][name] is appended to its path.
     *
     * @param [name] Name of the referenced column.
     */
    public infix fun named(name: String): ColumnAccessor<T> =
        parent?.let { ColumnAccessorImpl(it.path() + name) } ?: ColumnAccessorImpl(name)
}

// endregion

// region create DataColumn

/**
 * Creates a [<code>DataColumn</code>][DataColumn] with the given [<code>values</code>][values] and an empty name.
 *
 * The column type is inferred from the values, with the reified type argument [<code>T</code>][T] as an upper bound:
 * `columnOf<Number>(1, 2)` has type `Int`, while `columnOf<Number>(1, 2.5)` has type `Number`.
 * When the values don't share a reifiable type, pass [<code>T</code>][T] explicitly — e.g. `columnOf<Number>(1, 2.5)`;
 * without it, such a call does not compile.
 *
 * Lists and other collections are stored as cell values: `columnOf(listOf(1, 2))` creates
 * a value column of type `List<Int>`, not a column group.
 *
 * See also `column`, `valueColumn`, `columnGroup` and `frameColumn`,
 * which create references to columns rather than columns.
 *
 * For more information: [See `columnOf` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#columnof)
 *
 * Example:
 * ```kotlin
 * val numbers = columnOf(listOf(1, 2), listOf(3, 4)) // a value column of List<Int>
 * ```
 *
 * @param [values] The values of the column.
 */
public inline fun <reified T> columnOf(vararg values: T): DataColumn<T> =
    createColumnGuessingType(
        values = values.asIterable(),
        suggestedType = TypeSuggestion.InferWithUpperbound(typeOf<T>()),
        listifyValues = false,
        allColsMakesColGroup = true,
    ).forceResolve()

/**
 * Creates a [<code>ColumnGroup</code>][ColumnGroup] with the given [<code>values</code>][values] as its children and an empty name.
 *
 * @param [values] The columns that become the children of the group.
 */
public fun columnOf(vararg values: BaseColumn<*>): DataColumn<DataRow<*>> = columnOf(values.asIterable()).forceResolve()

/**
 * [See `columnOf` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#columnof)
 *
 * Example:
 * ```kotlin
 * val columnGroup = columnOf(
 *   "a" to columnOf("a1", "a2", "a3"),
 *   "b" to columnOf(1, 2, 3),
 * )
 * ```
 */
@Refine
@Interpretable("ColumnOfPairs")
public fun columnOf(vararg columns: Pair<String, BaseColumn<*>>): ColumnGroup<*> =
    dataFrameOf(
        columns.map { (name, col) ->
            col.rename(name)
        },
    ).asColumnGroup()

/**
 * Creates a [<code>FrameColumn</code>][FrameColumn] with the given [<code>frames</code>][frames] as its values and an empty name.
 *
 * @param [frames] The frames that become the values of the column.
 */
@Refine
@Interpretable("ColumnOfFrames")
public fun <T> columnOf(vararg frames: DataFrame<T>): FrameColumn<T> = columnOf(frames.asIterable()).forceResolve()

/**
 * Creates a [<code>ColumnGroup</code>][ColumnGroup] with the given [<code>columns</code>][columns] as its children and an empty name.
 *
 * @param [columns] The columns that become the children of the group.
 */
public fun columnOf(columns: Iterable<BaseColumn<*>>): DataColumn<DataRow<*>> =
    DataColumn.createColumnGroup(
        name = "",
        df = dataFrameOf(columns),
    )
        .asDataColumn()
        .forceResolve()

/**
 * Creates a [<code>FrameColumn</code>][FrameColumn] with the given [<code>frames</code>][frames] as its values and an empty name.
 *
 * @param [frames] The frames that become the values of the column.
 */
public fun <T> columnOf(frames: Iterable<DataFrame<T>>): FrameColumn<T> =
    DataColumn.createFrameColumn(
        name = "",
        groups = frames.toList(),
    ).forceResolve()

/**
 * Creates a [<code>DataColumn</code>][DataColumn] of type [<code>T</code>][T] from [<code>values</code>][values] with an empty name.
 *
 * Unlike `columnOf`, the declared type [<code>T</code>][T] is used as is, without inference from the values:
 * `column(listOf<Number>(1, 2))` has type `Number`, while `columnOf(1, 2)` has type `Int`.
 * To create an empty column with a declared type, pass an empty list: `column(emptyList<Int>())`.
 *
 * For more information: [See `columnOf` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#columnof)
 *
 * @param [values] The values of the column.
 */
public inline fun <reified T> column(values: Iterable<T>): DataColumn<T> =
    createColumnGuessingType(
        values = values,
        suggestedType = TypeSuggestion.Use(typeOf<T>()),
        allColsMakesColGroup = true,
    ).forceResolve()

// endregion

// region create DataFrame

/**
 * Creates a new [<code>DataFrame</code>][DataFrame] with the given [<code>columns</code>][columns].
 *
 * All named columns must have unique names. For columns with empty names unique column names
 * are generated: "untitled", "untitled1", "untitled2" etc.
 *
 * All columns must have equal sizes.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @throws [DuplicateColumnNamesException][org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException] if column names are not unique
 * @throws [UnequalColumnSizesException][org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException] if column sizes are not equal
 *
 * @param [columns] Columns for the [<code>DataFrame</code>][DataFrame].
 */
public fun dataFrameOf(columns: Iterable<BaseColumn<*>>): DataFrame<*> {
    val cols = columns.map { it.unbox() }
    val nrow = if (cols.isEmpty()) 0 else cols[0].size
    return DataFrameImpl<Unit>(cols, nrow)
}

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from the given name-to-column [<code>columns</code>][columns] pairs.
 *
 * Example:
 * ```kotlin
 * val df = dataFrameOf(
 *     "a" to columnOf(1, 2),
 *     "b" to columnOf(1.0, 2.0),
 *     "group" to columnOf(
 *         "nestedA" to columnOf("42", "abc"),
 *     )
 * )
 * ```
 *
 * All named columns must have unique names. For columns with empty names unique column names
 * are generated: "untitled", "untitled1", "untitled2" etc.
 *
 * All columns must have equal sizes.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @throws [DuplicateColumnNamesException][org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException] if column names are not unique
 * @throws [UnequalColumnSizesException][org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException] if column sizes are not equal
 *
 * @param [columns] The name-to-column pairs of the new [<code>DataFrame</code>][DataFrame].
 */
@Refine
@JvmName("dataFrameOfColumns")
@Interpretable("DataFrameOfPairs")
public fun dataFrameOf(vararg columns: Pair<String, BaseColumn<*>>): DataFrame<*> =
    dataFrameOf(columns.map { (name, col) -> col.rename(name) })

/**
 * Creates a [<code>DataFrameBuilder</code>][DataFrameBuilder] for a [<code>DataFrame</code>][DataFrame] whose column names are taken from [<code>header</code>][header]
 * (see [<code>ColumnReference.name</code>][ColumnReference.name]).
 * Call the builder with the cell values in row-major order — see [<code>DataFrameBuilder</code>][DataFrameBuilder].
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The column references providing the names of the columns.
 */
public fun dataFrameOf(vararg header: ColumnReference<*>): DataFrameBuilder = DataFrameBuilder(header.map { it.name() })

/**
 * Creates a [<code>DataFrame</code>][DataFrame] with the given [<code>columns</code>][columns].
 *
 * All named columns must have unique names. For columns with empty names unique column names
 * are generated: "untitled", "untitled1", "untitled2" etc.
 *
 * All columns must have equal sizes.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @throws [DuplicateColumnNamesException][org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException] if column names are not unique
 * @throws [UnequalColumnSizesException][org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException] if column sizes are not equal
 *
 * @param [columns] The columns of the new [<code>DataFrame</code>][DataFrame].
 */
public fun dataFrameOf(vararg columns: BaseColumn<*>): DataFrame<*> = dataFrameOf(columns.asIterable())

/**
 * Creates a [<code>DataFrameBuilder</code>][DataFrameBuilder] for a [<code>DataFrame</code>][DataFrame] with the given column [<code>header</code>][header].
 * Call the builder with the cell values in row-major order — see [<code>DataFrameBuilder</code>][DataFrameBuilder].
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The names of the columns.
 */
@Interpretable("DataFrameOf0")
public fun dataFrameOf(vararg header: String): DataFrameBuilder = dataFrameOf(header.toList())

/**
 * Creates a [<code>DataFrame</code>][DataFrame] with the given column [<code>header</code>][header] where the values of each column
 * are produced by [<code>fill</code>][fill]: it is called once per column name and returns the values for that column.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The names of the columns.
 * @param [fill] Produces the values of a column by its name.
 */
public inline fun <reified C> dataFrameOf(
    vararg header: String,
    crossinline fill: (String) -> Iterable<C>,
): DataFrame<*> = dataFrameOf(header.asIterable()).invoke(fill)

/**
 * Creates a [<code>DataFrameBuilder</code>][DataFrameBuilder] for a [<code>DataFrame</code>][DataFrame] with the given column [<code>header</code>][header].
 * Call the builder with the cell values in row-major order — see [<code>DataFrameBuilder</code>][DataFrameBuilder].
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The names of the columns.
 */
public fun dataFrameOf(header: Iterable<String>): DataFrameBuilder = DataFrameBuilder(header.asList())

/**
 * Creates a [<code>DataFrame</code>][DataFrame] from the given name-to-values [<code>columns</code>][columns] pairs;
 * the type of each column is inferred from its values.
 *
 * All named columns must have unique names. For columns with empty names unique column names
 * are generated: "untitled", "untitled1", "untitled2" etc.
 *
 * All columns must have equal sizes.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @throws [DuplicateColumnNamesException][org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException] if column names are not unique
 * @throws [UnequalColumnSizesException][org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException] if column sizes are not equal
 *
 * @param [columns] The name-to-values pairs of the new [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("DataFrameOf3")
public fun dataFrameOf(vararg columns: Pair<String, List<Any?>>): DataFrame<*> =
    columns.map { it.second.toColumn(it.first, Infer.Type) }.toDataFrame()

/**
 * Creates a [<code>DataFrame</code>][DataFrame] with the given column names and the cell values in row-major order.
 */
@Deprecated(DATAFRAME_OF_WITH_VALUES, ReplaceWith("dataFrameOf(header).withValues(values)"))
public fun dataFrameOf(header: Iterable<String>, values: Iterable<Any?>): DataFrame<*> =
    dataFrameOf(header).withValues(values)

/**
 * Creates a [<code>DataFrame</code>][DataFrame] where each column is produced by [<code>fill</code>][fill] from an element of [<code>header</code>][header];
 * the column name is the string representation of that element.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The elements naming the columns.
 * @param [fill] Produces the values of a column from its header element.
 */
public inline fun <T, reified C> dataFrameOf(header: Iterable<T>, fill: (T) -> Iterable<C>): DataFrame<*> =
    header.map { value ->
        DataColumn.createByInference(
            name = value.toString(),
            values = fill(value).asList(),
            suggestedType = TypeSuggestion.InferWithUpperbound(typeOf<C>()),
        )
    }.toDataFrame()

/**
 * Creates a [<code>DataFrameBuilder</code>][DataFrameBuilder] for a [<code>DataFrame</code>][DataFrame] whose column names are the characters of [<code>header</code>][header].
 * Call the builder with the cell values in row-major order — see [<code>DataFrameBuilder</code>][DataFrameBuilder].
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The characters naming the columns.
 */
public fun dataFrameOf(header: CharProgression): DataFrameBuilder = dataFrameOf(header.map { it.toString() })

/**
 * A builder for a [<code>DataFrame</code>][DataFrame] with the given header of column names.
 *
 * Call [<code>invoke</code>][DataFrameBuilder.invoke] with all cell values in row-major order:
 * the values are distributed to the columns row by row, and the type of each column
 * is inferred from its values:
 * ```kotlin
 * val df = dataFrameOf("a", "b")(1, 2, 3, 4) // a: [1, 3], b: [2, 4]
 * ```
 *
 * Alternatively, [<code>withColumns</code>][withColumns] or [<code>invoke</code>][DataFrameBuilder.invoke] with a values builder
 * create the columns one by one, and the [<code>fill</code>][fill], [<code>nulls</code>][nulls] and `random*` functions create
 * constant or random frames.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 */
public class DataFrameBuilder(private val header: List<String>) {

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] from [<code>columns</code>][columns], renaming them to the header names.
     *
     * @throws [IllegalArgumentException] if the number of [<code>columns</code>][columns] differs from the number of header names.
     */
    public operator fun invoke(vararg columns: DataColumn<*>): DataFrame<*> = invoke(columns.asIterable())

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] from [<code>columns</code>][columns], renaming them to the header names.
     *
     * @throws [IllegalArgumentException] if the number of [<code>columns</code>][columns] differs from the number of header names.
     */
    public operator fun invoke(columns: Iterable<DataColumn<*>>): DataFrame<*> {
        val cols = columns.asList()
        require(cols.size == header.size) { "Number of columns differs from number of column names" }
        return cols.mapIndexed { i, col ->
            col.rename(header[i])
        }.toDataFrame()
    }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] by distributing [<code>values</code>][values] to the columns in row-major order:
     * the values are filled row by row, and the type of each column is inferred from its values.
     *
     * @throws [IllegalArgumentException] if the number of [<code>values</code>][values] is not divisible by the number of
     * header names — in particular, when the header is empty.
     */
    @Refine
    @Interpretable("DataFrameBuilderInvoke0")
    public operator fun invoke(vararg values: Any?): DataFrame<*> = withValues(values.asIterable())

    /** Distributes [<code>values</code>][values] to the columns in row-major order. */
    @JvmName("invoke1")
    internal fun withValues(values: Iterable<Any?>): DataFrame<*> =
        (header to values.asList()).withValuesImpl().map { (name, values) ->
            DataColumn.createByInference(name, values)
        }.toDataFrame()

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] from the values of [<code>args</code>][args], distributed to the columns in row-major order.
     *
     * @throws [IllegalArgumentException] if the number of values is not divisible by the number of
     * header names.
     */
    public operator fun invoke(args: Sequence<Any?>): DataFrame<*> = invoke(*args.toList().toTypedArray())

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] by building each column with [<code>columnBuilder</code>][columnBuilder];
     * the created column is renamed to its header name.
     *
     * @param [columnBuilder] Builds a column by its header name.
     */
    public fun withColumns(columnBuilder: (String) -> DataColumn<*>): DataFrame<*> =
        header
            .map { columnBuilder(it) named it } // create the columns and make sure to rename them to the given header
            .toDataFrame()

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where the values of each column are produced by [<code>valuesBuilder</code>][valuesBuilder]
     * for that column's name; the column type comes from [<code>T</code>][T].
     *
     * @param [valuesBuilder] Produces the values of a column by its name.
     */
    public inline operator fun <reified T> invoke(crossinline valuesBuilder: (String) -> Iterable<T>): DataFrame<*> =
        withColumns { name ->
            DataColumn.createByInference(
                name = name,
                values = valuesBuilder(name).asList(),
                suggestedType = TypeSuggestion.InferWithUpperbound(typeOf<T>()),
            )
        }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] cells equal to [<code>value</code>][value].
     * The column type is [<code>C</code>][C]; a `null` [<code>value</code>][value] makes the columns nullable.
     *
     * @param [nrow] The number of rows in every column.
     * @param [value] The value of every cell.
     */
    @Refine
    @Interpretable("DataFrameBuilderFillValue")
    public inline fun <reified C> fill(nrow: Int, value: C): DataFrame<*> =
        withColumns { name ->
            DataColumn.createValueColumn(
                name = name,
                values = List(nrow) { value },
                type = typeOf<C>().withNullability(value == null),
            )
        }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column is a frame column with [<code>nrow</code>][nrow] copies of [<code>dataFrame</code>][dataFrame].
     *
     * @param [nrow] The number of rows in every column.
     * @param [dataFrame] The [<code>DataFrame</code>][DataFrame] stored in every cell.
     */
    public fun fill(nrow: Int, dataFrame: DataFrame<*>): DataFrame<*> =
        withColumns { name ->
            DataColumn.createFrameColumn(
                name = name,
                groups = List(nrow) { dataFrame },
                schema = lazy { dataFrame.schema() },
            )
        }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] `null` values of type [<code>C</code>][C]`?`.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderNulls")
    public inline fun <reified C> nulls(nrow: Int): DataFrame<*> = fill<C?>(nrow, null)

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] values produced by [<code>init</code>][init],
     * with the row index and the column name.
     *
     * @param [nrow] The number of rows in every column.
     * @param [init] Produces the value of a cell from its row index and column name.
     */
    @Refine
    @Interpretable("DataFrameBuilderFillIndexed")
    public inline fun <reified C> fillIndexed(nrow: Int, crossinline init: (Int, String) -> C): DataFrame<*> =
        withColumns { name ->
            DataColumn.createByInference(
                name = name,
                values = List(nrow) { init(it, name) },
            )
        }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] values produced by [<code>init</code>][init];
     * the column type is inferred from the values.
     *
     * @param [nrow] The number of rows in every column.
     * @param [init] Produces the value of a cell from its row index.
     */
    @Refine
    @Interpretable("DataFrameBuilderFill")
    public inline fun <reified C> fill(nrow: Int, crossinline init: (Int) -> C): DataFrame<*> =
        withColumns { name ->
            DataColumn.createByInference(
                name = name,
                values = List(nrow, init),
            )
        }

    private inline fun <reified C : Any> fillNotNull(nrow: Int, crossinline init: (Int) -> C) =
        withColumns { name ->
            DataColumn.createValueColumn(
                name = name,
                values = List(nrow, init),
                type = typeOf<C>(),
            )
        }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Int</code>][Int] values.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomInt")
    public fun randomInt(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextInt() }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Int</code>][Int] values within [<code>range</code>][range].
     * Convenient for tests and demos.
     *
     * @param [range] The range of the random values (both bounds included).
     * @throws [IllegalArgumentException] if [<code>range</code>][range] is empty.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomIntRange")
    public fun randomInt(nrow: Int, range: IntRange): DataFrame<*> = fillNotNull(nrow) { Random.nextInt(range) }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Double</code>][Double] values between `0.0` and `1.0`.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomDouble")
    public fun randomDouble(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextDouble() }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Double</code>][Double] values within [<code>range</code>][range].
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     * @param [range] The range of the random values: the lower bound is included, the upper bound is not.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomDoubleRange")
    public fun randomDouble(nrow: Int, range: ClosedRange<Double>): DataFrame<*> =
        fillNotNull(nrow) { Random.nextDouble(range.start, range.endInclusive) }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Float</code>][Float] values between `0.0` and `1.0`.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomFloat")
    public fun randomFloat(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextFloat() }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Long</code>][Long] values.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomLong")
    public fun randomLong(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextLong() }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Long</code>][Long] values within [<code>range</code>][range].
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     * @param [range] The range of the random values: the lower bound is included, the upper bound is not.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomLongRange")
    public fun randomLong(nrow: Int, range: ClosedRange<Long>): DataFrame<*> =
        fillNotNull(nrow) { Random.nextLong(range.start, range.endInclusive) }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Boolean</code>][Boolean] values.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomBoolean")
    public fun randomBoolean(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextBoolean() }
}

/**
 * A builder class for dynamically constructing a DataFrame with provided columns.
 * Allows adding columns manually while automatically handling duplicate column names by assigning unique names.
 *
 * [See `DynamicDataFrameBuilder` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dynamicdataframebuilder)
 *
 * @property checkDuplicateValues Whether to check for duplicate column (with identical names and values). If `true`,
 * doesn't add a new column if the identical one is already in the builder.
 * when adding new columns. `true` by default.
 */
public class DynamicDataFrameBuilder(private val checkDuplicateValues: Boolean = true) {
    private var cols: MutableMap<String, AnyCol> = mutableMapOf()
    private val generator = ColumnNameGenerator()

    /**
     * Adds a column to the builder, ensuring its name is unique.
     *
     * - If a column with the same name already exists, the new column is renamed to a unique name.
     * - If [<code>checkDuplicateValues</code>][checkDuplicateValues] is `true`, the method checks whether the new column has identical values
     *   to an existing column with the same name. If the values match, the column is not added.
     *
     * [See `DynamicDataFrameBuilder` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dynamicdataframebuilder)
     *
     * @param col The column to add to the DataFrame builder.
     * @return The final unique name assigned to the column.
     */
    public fun add(col: DataColumn<*>): String {
        val originalName = col.name()
        if (checkDuplicateValues && generator.contains(originalName)) {
            if (cols[originalName] == col) return originalName
        }
        val uniqueName = if (originalName.isEmpty()) {
            generator.addUnique(UNNAMED_COLUMN_PREFIX)
        } else {
            generator.addUnique(originalName)
        }
        val renamed = if (uniqueName != originalName) {
            col.rename(uniqueName)
        } else {
            col
        }
        cols.put(uniqueName, renamed)
        return uniqueName
    }

    /**
     * Adds a column to the builder from the given iterable of values, ensuring the column's name is unique.
     *
     * The method automatically converts the given iterable into a column using the specified or default name
     * and infers the type of the column's elements.
     *
     * - If a column with the same name already exists, the new column is renamed to a unique name.
     * - If the [<code>checkDuplicateValues</code>][checkDuplicateValues] property of the builder is `true`, the method checks whether the new column
     *   has identical values to an existing column with the same name. If the values match, the column is not added.
     *
     * [See `DynamicDataFrameBuilder` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dynamicdataframebuilder)
     *
     * @param T The inferred type of the elements in the column.
     * @param values The iterable collection of values to be added as a new column.
     * @param name The name of the new column. If empty, a unique name will be generated automatically.
     * @return The final unique name assigned to the column.
     */
    public inline fun <reified T> add(values: Iterable<T>, name: String = ""): String =
        add(values.toColumn(name, Infer.Type))

    /**
     * Retrieves a column from the builder by its name.
     *
     * [See `DynamicDataFrameBuilder` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dynamicdataframebuilder)
     *
     * @param column The name of the column to retrieve.
     * @return The column corresponding to the specified name, or `null` if no such column exists.
     */
    public fun get(column: String): DataColumn<*>? = cols[column]

    /**
     * Converts the current [<code>DynamicDataFrameBuilder</code>][DynamicDataFrameBuilder] instance into a [<code>DataFrame</code>][DataFrame].
     * The resulting [<code>DataFrame</code>][DataFrame] is constructed from the columns stored in the builder.
     *
     * [See `DynamicDataFrameBuilder` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dynamicdataframebuilder)
     *
     * @return A [<code>DataFrame</code>][DataFrame] containing the columns defined in the [<code>DynamicDataFrameBuilder</code>][DynamicDataFrameBuilder].
     */
    public fun toDataFrame(): DataFrame<*> = cols.values.toDataFrame()
}

/**
 * Returns [<code>DataFrame</code>][DataFrame] with empty columns according to schema [<code>T</code>][T].
 *
 * To create [<code>DataFrame</code>][DataFrame] with empty columns or empty rows see [<code>DataFrame.empty</code>][DataFrame.empty]
 *
 * [See `emptyDataFrame` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#emptydataframe)
 *
 * @param T schema marker for [<code>DataFrame</code>][DataFrame]
 */
public inline fun <reified T> emptyDataFrame(): DataFrame<T> = DataFrame.emptyOf<T>()

// endregion

// region create ColumnPath

/**
 * Creates a [<code>ColumnPath</code>][ColumnPath] from the given [<code>columnNames</code>][columnNames], outermost first.
 * `pathOf()` without arguments returns the empty [<code>ColumnPath</code>][ColumnPath];
 * [<code>ColumnPath.name</code>][ColumnPath.name] of an empty path throws a [<code>NoSuchElementException</code>][NoSuchElementException].
 *
 * For more information: [See `pathOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#column-resolvers)
 *
 * @param [columnNames] The names of the columns along the path.
 */
@Interpretable("PathOf")
public fun pathOf(vararg columnNames: String): ColumnPath =
    if (columnNames.isEmpty()) ColumnPath.EMPTY else ColumnPath(columnNames.asList())

// endregion
