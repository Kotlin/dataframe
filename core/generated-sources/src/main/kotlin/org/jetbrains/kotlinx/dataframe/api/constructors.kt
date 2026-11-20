package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
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
import kotlin.random.nextLong
import kotlin.reflect.KProperty
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region create ColumnAccessor

// region column

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun <T> column(): ColumnDelegate<T> = ColumnDelegate()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> column(path: ColumnPath): ColumnAccessor<T> = ColumnAccessorImpl(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> column(property: KProperty<T>): ColumnAccessor<T> = ColumnAccessorImpl(property.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun <T> ColumnReference<DataRow<*>>.column(): ColumnDelegate<T> = ColumnDelegate(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(path() + name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.column(path: ColumnPath): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.column(property: KProperty<T>): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + property.name)

/**
 * Creates an expression column — a reference to a column computed from a row by [<code>expression</code>][expression],
 * with `this` inside [<code>expression</code>][expression] being the current [<code>DataRow</code>][DataRow].
 * The reference can be stored and reused in operations like `add`.
 * For the general column-expression mechanism, see [<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression].
 *
 * For more information: [See `expr` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#expr-column-expression)
 *
 * ```kotlin
 * val isAdult = column<Boolean>("isAdult") { getValue<Int>("age") > 18 }
 * val withColumn = peopleDf.add { add(isAdult) }
 * ```
 *
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
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
 * Creates an expression column — a reference to a column computed from a row by [<code>expression</code>][expression],
 * with `this` inside [<code>expression</code>][expression] being a [<code>DataRow</code>][DataRow]`<T>` typed by the schema marker of [<code>df</code>][df].
 * The resulting reference is not tied to [<code>df</code>][df]: it can be used with any [<code>DataFrame</code>][DataFrame].
 * For the general column-expression mechanism, see [<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression].
 *
 * For more information: [See `expr` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#expr-column-expression)
 *
 * ```kotlin
 * val isAdult = column(peopleDf, "isAdult") { age > 18 }
 * val withColumn = peopleDf.add { add(isAdult) }
 * ```
 *
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
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

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun valueColumn(): ColumnDelegate<Any?> = column()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("valueColumnTyped")
@Suppress("DEPRECATION")
public fun <T> valueColumn(): ColumnDelegate<T> = column()

@Suppress("DEPRECATION")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun valueColumn(name: String): ColumnAccessor<Any?> = column(name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("valueColumnTyped")
@Suppress("DEPRECATION")
public fun <T> valueColumn(name: String): ColumnAccessor<T> = column(name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun valueColumn(path: ColumnPath): ColumnAccessor<Any?> = column(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("valueColumnTyped")
@Suppress("DEPRECATION")
public fun <T> valueColumn(path: ColumnPath): ColumnAccessor<T> = column(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun <T> valueColumn(property: KProperty<T>): ColumnAccessor<T> = column(property.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun ColumnReference<DataRow<*>>.valueColumn(): ColumnDelegate<Any?> = ColumnDelegate(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("valueColumnTyped")
@Suppress("DEPRECATION")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(): ColumnDelegate<T> = ColumnDelegate(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun ColumnReference<DataRow<*>>.valueColumn(name: String): ColumnAccessor<Any?> =
    ColumnAccessorImpl(path() + name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("valueColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(name: String): ColumnAccessor<T> =
    ColumnAccessorImpl(path() + name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun ColumnReference<DataRow<*>>.valueColumn(path: ColumnPath): ColumnAccessor<Any?> =
    ColumnAccessorImpl(this.path() + path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("valueColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(path: ColumnPath): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> ColumnReference<DataRow<*>>.valueColumn(property: KProperty<T>): ColumnAccessor<T> =
    ColumnAccessorImpl(this.path() + property.name)

// endregion

// region columnGroup

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun columnGroup(): ColumnDelegate<DataRow<*>> = column()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("columnGroupTyped")
@Suppress("DEPRECATION")
public fun <T> columnGroup(): ColumnDelegate<DataRow<T>> = column()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun columnGroup(name: String): ColumnAccessor<DataRow<*>> = column(name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("columnGroupTyped")
@Suppress("DEPRECATION")
public fun <T> columnGroup(name: String): ColumnAccessor<DataRow<T>> = column(name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun columnGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = column(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("columnGroupTyped")
@Suppress("DEPRECATION")
public fun <T> columnGroup(path: ColumnPath): ColumnAccessor<DataRow<T>> = column(path)

@JvmName("columnGroupDataRowKProperty")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun <T> columnGroup(property: KProperty<DataRow<T>>): ColumnAccessor<DataRow<T>> = column(property)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun <T> columnGroup(property: KProperty<T>): ColumnAccessor<DataRow<T>> = column(property.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun ColumnReference<DataRow<*>>.columnGroup(): ColumnDelegate<DataRow<*>> = ColumnDelegate(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("columnGroupTyped")
@Suppress("DEPRECATION")
public fun <T> ColumnReference<DataRow<*>>.columnGroup(): ColumnDelegate<DataRow<T>> = ColumnDelegate(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun ColumnReference<DataRow<*>>.columnGroup(name: String): ColumnAccessor<DataRow<*>> =
    ColumnAccessorImpl(path() + name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("columnGroupTyped")
public fun <T> ColumnReference<DataRow<*>>.columnGroup(name: String): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(path() + name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun ColumnReference<DataRow<*>>.columnGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
    ColumnAccessorImpl(this.path() + path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
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

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun frameColumn(): ColumnDelegate<DataFrame<*>> = column()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("frameColumnTyped")
@Suppress("DEPRECATION")
public fun <T> frameColumn(): ColumnDelegate<DataFrame<T>> = column()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun frameColumn(name: String): ColumnAccessor<DataFrame<*>> = column(name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("frameColumnTyped")
@Suppress("DEPRECATION")
public fun <T> frameColumn(name: String): ColumnAccessor<DataFrame<T>> = column(name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<*>> = column(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("frameColumnTyped")
@Suppress("DEPRECATION")
public fun <T> frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<T>> = column(path)

@JvmName("frameColumnDataFrameKProperty")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun <T> frameColumn(property: KProperty<DataFrame<T>>): ColumnAccessor<DataFrame<T>> = column(property)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun <T> frameColumn(property: KProperty<List<T>>): ColumnAccessor<DataFrame<T>> = column(property.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@Suppress("DEPRECATION")
public fun ColumnReference<DataRow<*>>.frameColumn(): ColumnDelegate<DataFrame<*>> = ColumnDelegate(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("frameColumnTyped")
@Suppress("DEPRECATION")
public fun <T> ColumnReference<DataRow<*>>.frameColumn(): ColumnDelegate<DataFrame<T>> = ColumnDelegate(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun ColumnReference<DataRow<*>>.frameColumn(name: String): ColumnAccessor<DataFrame<*>> =
    ColumnAccessorImpl(path() + name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
@JvmName("frameColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.frameColumn(name: String): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(path() + name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun ColumnReference<DataRow<*>>.frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
    ColumnAccessorImpl(this.path() + path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
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

@Suppress("DEPRECATION")
@Deprecated(DEPRECATED_ACCESS_API)
public class ColumnDelegate<T>(private val parent: ColumnReference<DataRow<*>>? = null) {

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = named(property.columnName)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
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
 * This overload is on par with the more specific ones: passing [<code>DataFrame</code>][DataFrame]s creates a [<code>FrameColumn</code>][FrameColumn],
 * passing [<code>DataRow</code>][DataRow]s or columns creates a [<code>ColumnGroup</code>][ColumnGroup], anything else a [<code>ValueColumn</code>][ValueColumn].
 *
 * See also [<code>`column(values: Iterable<T>)`</code>][column] if you want the values of a list
 * to be unpacked into a column, and the other [<code>columnOf</code>][columnOf] overloads
 * creating a [<code>ColumnGroup</code>][ColumnGroup] or a [<code>FrameColumn</code>][FrameColumn] from columns or frames.
 *
 * For more information: [See `columnOf` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#columnof)
 *
 * ```kotlin
 * // Create ValueColumn with name 'student' and two elements of type String
 * val student by columnOf("Alice", "Bob")
 * ```
 * ```kotlin
 * // Mixed values need an explicit type argument
 * val weights = columnOf<Number>(1, 2.5)
 * ```
 * ```kotlin
 * // Lists are stored as cell values: a ValueColumn of List<Int>, not a ColumnGroup
 * val numbers by columnOf(listOf(1, 2), listOf(3, 4))
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
 * Each child column keeps its own name; to name the group itself, use `named`.
 *
 * ```kotlin
 * val firstName by columnOf("Alice", "Bob")
 * val lastName by columnOf("Cooper", "Marley")
 *
 * // Create ColumnGroup with two nested columns
 * val fullName by columnOf(firstName, lastName)
 * ```
 *
 * @param [values] The columns that become the children of the group.
 */
public fun columnOf(vararg values: BaseColumn<*>): DataColumn<DataRow<*>> = columnOf(values.asIterable()).forceResolve()

/**
 * Creates a [<code>ColumnGroup</code>][ColumnGroup] from the given name-to-column [<code>columns</code>][columns] pairs,
 * with each column renamed to its key and the group itself left with an empty name.
 * Since the group is built as a [<code>DataFrame</code>][DataFrame], the keys must be unique
 * and the columns must have equal sizes.
 *
 * ```kotlin
 * // A ColumnGroup: each column is renamed to its key
 * val personal = columnOf(
 *     "firstName" to columnOf("Alice", "Bob"),
 *     "lastName" to columnOf("Cooper", "Marley"),
 * )
 * ```
 *
 * @throws [DuplicateColumnNamesException] if the keys are not unique.
 * @throws [UnequalColumnSizesException] if the columns have different sizes.
 *
 * For more information: [See `columnOf` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#columnof)
 *
 * @param [columns] The name-to-column pairs of the group.
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
 * ```kotlin
 * val df1 = dataFrameOf("name", "age")(
 *     "Alice", 20,
 *     "Bob", 25,
 * )
 * val df2 = dataFrameOf("name", "temp")(
 *     "Charlie", 36.6,
 * )
 *
 * // Create FrameColumn with two elements of type DataFrame
 * val frames by columnOf(df1, df2)
 * ```
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
 * ```kotlin
 * // DataFrame with 2 columns
 * val df = dataFrameOf(
 *     "name" to columnOf("Alice", "Bob", "Charlie"),
 *     "age" to columnOf(15, 20, 22)
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
 * ```kotlin
 * // DataFrame with 2 columns and 3 rows
 * val df = dataFrameOf("name", "age")(
 *     "Alice", 15,
 *     "Bob", 20,
 *     "Charlie", 100,
 * )
 * ```
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
 * The type of each column is inferred from its values, with the reified type argument as an upper bound:
 * with `Number` as the type argument, `listOf(1, 2)` produces a column of type `Int`,
 * and only mixed values such as `listOf(1, 2.5)` produce a column of type `Number`.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The names of the columns.
 * @param [fill] Produces the values of a column by its name.
 *
 * ```kotlin
 * // The reified argument is an upper bound: with Number, the columns are inferred as Int
 * val df = dataFrameOf("a", "b") { listOf<Number>(1, 2) }
 * ```
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
 *
 * ```kotlin
 * val header = listOf("name", "age")
 * val df = dataFrameOf(header)("Alice", 20, "Bob", 25)
 * ```
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
 *
 * ```kotlin
 * val df = dataFrameOf(
 *     "name" to listOf("Alice", "Bob"),
 *     "age" to listOf(15, 20),
 * )
 * ```
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
 * The type of each column is inferred from its values, with the reified type argument as an upper bound:
 * with `Number` as the type argument, `listOf(1, 2)` produces a column of type `Int`,
 * and only mixed values such as `listOf(1, 2.5)` produce a column of type `Number`.
 *
 * For more information: [See `dataFrameOf` on the documentation website.](https://kotlin.github.io/dataframe/createdataframe.html#dataframeof)
 *
 * @param [header] The elements naming the columns.
 * @param [fill] Produces the values of a column from its header element.
 *
 * ```kotlin
 * // The reified argument is an upper bound: with Number, the columns are inferred as Int
 * val df = dataFrameOf(listOf("a", "b")) { listOf<Number>(1, 2) }
 * ```
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
 *
 * ```kotlin
 * // The characters become the column names
 * val df = dataFrameOf('a'..'c')(1, 2, 3, 4, 5, 6)
 * ```
 */
public fun dataFrameOf(header: CharProgression): DataFrameBuilder = dataFrameOf(header.map { it.toString() })

/**
 * A builder for a [<code>DataFrame</code>][DataFrame] with the given header of column names.
 *
 * Call [<code>invoke</code>][DataFrameBuilder.invoke] with all cell values in row-major order:
 * the values are distributed to the columns row by row, and the type of each column
 * is inferred from its values.
 *
 * ```kotlin
 * // DataFrame with 2 columns and 3 rows
 * val df = dataFrameOf("name", "age")(
 *     "Alice", 15,
 *     "Bob", 20,
 *     "Charlie", 100,
 * )
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
     *
     * ```kotlin
     * // The columns are renamed to the header names
     * val df = dataFrameOf("name", "age")(columnOf("Alice", "Bob"), columnOf(15, 20))
     * ```
     */
    public operator fun invoke(vararg columns: DataColumn<*>): DataFrame<*> = invoke(columns.asIterable())

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] from [<code>columns</code>][columns], renaming them to the header names.
     *
     * @throws [IllegalArgumentException] if the number of [<code>columns</code>][columns] differs from the number of header names.
     *
     * ```kotlin
     * // The columns are renamed to the header names
     * val df = dataFrameOf("name", "age")(columnOf("Alice", "Bob"), columnOf(15, 20))
     * ```
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
     *
     * ```kotlin
     * // The lambda receives the column name and builds the whole column
     * val df = dataFrameOf("a", "bb").withColumns { name -> columnOf(name.length, name.length + 1) }
     * ```
     */
    public fun withColumns(columnBuilder: (String) -> DataColumn<*>): DataFrame<*> =
        header
            .map { columnBuilder(it) named it } // create the columns and make sure to rename them to the given header
            .toDataFrame()

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where the values of each column are produced by [<code>valuesBuilder</code>][valuesBuilder]
     * for that column's name.
     *
     * The type of each column is inferred from its values, with the reified type argument as an upper bound:
     * with `Number` as the type argument, `listOf(1, 2)` produces a column of type `Int`,
     * and only mixed values such as `listOf(1, 2.5)` produce a column of type `Number`.
     *
     * @param [valuesBuilder] Produces the values of a column by its name.
     *
     * ```kotlin
     * // The lambda receives the column name and returns the values of that column
     * val df = dataFrameOf("a", "bb") { name -> listOf(name.length, name.length * 2) }
     * ```
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
     * ```kotlin
     * // Every column: 3 rows filled with 0
     * val filled = dataFrameOf("a", "b").fill(3, 0)
     * // Every column: 3 rows of null values of type Int?
     * val empty = dataFrameOf("a", "b").nulls<Int>(3)
     * ```
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
     *
     * ```kotlin
     * val userDf = dataFrameOf("name")("Alice", "Bob")
     *
     * // Every cell holds the whole DataFrame
     * val df = dataFrameOf("user1", "user2").fill(2, userDf)
     * ```
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
     * ```kotlin
     * // Every column: 3 rows filled with 0
     * val filled = dataFrameOf("a", "b").fill(3, 0)
     * // Every column: 3 rows of null values of type Int?
     * val empty = dataFrameOf("a", "b").nulls<Int>(3)
     * ```
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
     *
     * ```kotlin
     * // The lambda receives the row index and the column name
     * val df = dataFrameOf("a", "b").fillIndexed(3) { index, _ -> index }
     * ```
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
     *
     * ```kotlin
     * // The lambda receives the row index
     * val df = dataFrameOf("a", "b").fill(3) { it * it }
     * ```
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
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
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
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomIntRange")
    public fun randomInt(nrow: Int, range: IntRange): DataFrame<*> = fillNotNull(nrow) { Random.nextInt(range) }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Double</code>][Double] values between `0.0` and `1.0`.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
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
     * @throws [IllegalArgumentException] if [<code>range</code>][range] is empty or its bounds are equal — no random value can be produced.
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
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
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomFloat")
    public fun randomFloat(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextFloat() }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Long</code>][Long] values.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomLong")
    public fun randomLong(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextLong() }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Long</code>][Long] values within [<code>range</code>][range].
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     * @param [range] The range of the random values (both bounds included).
     * @throws [IllegalArgumentException] if [<code>range</code>][range] is empty.
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomLongRange")
    public fun randomLong(nrow: Int, range: LongRange): DataFrame<*> = fillNotNull(nrow) { Random.nextLong(range) }

    /**
     * Creates a [<code>DataFrame</code>][DataFrame] where every column has [<code>nrow</code>][nrow] random [<code>Boolean</code>][Boolean] values.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     *
     * ```kotlin
     * val ints = dataFrameOf("i").randomInt(3)
     * val boundedInts = dataFrameOf("bi").randomInt(3, 1..10)
     * val longs = dataFrameOf("l").randomLong(3)
     * val boundedLongs = dataFrameOf("bl").randomLong(3, 1L..10L)
     * val doubles = dataFrameOf("d").randomDouble(3)
     * val boundedDoubles = dataFrameOf("bd").randomDouble(3, 1.0..10.0)
     * val floats = dataFrameOf("f").randomFloat(3)
     * val flags = dataFrameOf("flag").randomBoolean(3)
     * ```
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
 *
 * ```kotlin
 * val path = pathOf("personal", "age")
 * ```
 */
@Interpretable("PathOf")
public fun pathOf(vararg columnNames: String): ColumnPath =
    if (columnNames.isEmpty()) ColumnPath.EMPTY else ColumnPath(columnNames.asList())

// endregion
