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
import org.jetbrains.kotlinx.dataframe.documentation.ConstructorsDocs
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
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
 * Creates a [ColumnDelegate] for the `by` property syntax of a column reference:
 * ```kotlin
 * val age by column<Int>()
 * ```
 * The referenced column name is taken from the property name; the [ColumnName] annotation overrides it.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun <T> column(): ColumnDelegate<T> = ColumnDelegate()

/**
 * Creates a [ColumnAccessor] for the column with the given [name].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column.
 */
public fun <T> column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(name)

/**
 * Creates a [ColumnAccessor] for the column at the given [path].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced column.
 */
public fun <T> column(path: ColumnPath): ColumnAccessor<T> = ColumnAccessorImpl(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> column(property: KProperty<T>): ColumnAccessor<T> = ColumnAccessorImpl(property.name)

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a column reference nested in the receiver
 * column group: the referenced column path is the receiver's path with the property name appended:
 * ```kotlin
 * val personal = columnGroup("personal")
 * val age by personal.column<Int>() // references personal/age
 * ```
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun <T> ColumnReference<DataRow<*>>.column(): ColumnDelegate<T> = ColumnDelegate(this)

/**
 * Creates a [ColumnAccessor] for the column with the given [name] nested in the receiver column group:
 * the created path is the receiver's path with [name] appended.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column.
 */
public fun <T> ColumnReference<DataRow<*>>.column(name: String): ColumnAccessor<T> = ColumnAccessorImpl(path() + name)

/**
 * Creates a [ColumnAccessor] for the column at the given [path] nested in the receiver column group:
 * the created path is the receiver's path with [path] appended.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
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
 * Creates a reference to a column computed from a row by [expression].
 * The reference can be stored and reused in operations like `select` and `add`:
 * ```kotlin
 * // `this` is an untyped DataRow, so the value is cast to Int
 * val isAdult = column<Boolean>("isAdult") { (this["age"] as Int) > 18 }
 * val withColumn = dataFrameOf("age")(20, 15).add { add(isAdult) }
 * ```
 * Inside [expression], `this` is the current [DataRow].
 *
 * For more information: {@include [DocumentationUrls.Expr]}
 *
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
 * @param [name] Name of the computed column.
 * @param [expression] The [RowExpression] computing the value of the column for each row.
 */
public inline fun <reified T> column(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: RowExpression<Any?, T>,
): ColumnReference<T> = createComputedColumnReference(name, typeOf<T>(), infer, expression)

/**
 * Creates a reference to a column computed from a row by [expression].
 * Inside [expression], `this` is a [DataRow]`<T>` typed by the schema marker of [df].
 * The resulting reference is not tied to [df]: it can be used with any [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Expr]}
 *
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
 * @param [df] The [DataFrame] whose schema marker types [expression]. It is not used otherwise.
 * @param [name] Name of the computed column.
 * @param [expression] The [RowExpression] computing the value of the column for each row.
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
 * Creates a [ColumnDelegate] for the `by` property syntax of a value column reference.
 * Without a type argument, the referenced values have type `Any?`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun valueColumn(): ColumnDelegate<Any?> = column()

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a value column reference.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
@JvmName("valueColumnTyped")
public fun <T> valueColumn(): ColumnDelegate<T> = column()

/**
 * Creates a [ColumnAccessor] for the value column with the given [name].
 * Without a type argument, the referenced values have type `Any?`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column.
 */
public fun valueColumn(name: String): ColumnAccessor<Any?> = column(name)

/**
 * Creates a [ColumnAccessor] for the value column with the given [name].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column.
 */
@JvmName("valueColumnTyped")
public fun <T> valueColumn(name: String): ColumnAccessor<T> = column(name)

/**
 * Creates a [ColumnAccessor] for the value column at the given [path].
 * Without a type argument, the referenced values have type `Any?`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced column.
 */
public fun valueColumn(path: ColumnPath): ColumnAccessor<Any?> = column(path)

/**
 * Creates a [ColumnAccessor] for the value column at the given [path].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced column.
 */
@JvmName("valueColumnTyped")
public fun <T> valueColumn(path: ColumnPath): ColumnAccessor<T> = column(path)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> valueColumn(property: KProperty<T>): ColumnAccessor<T> = column(property.name)

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a value column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * Without a type argument, the referenced values have type `Any?`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun ColumnReference<DataRow<*>>.valueColumn(): ColumnDelegate<Any?> = ColumnDelegate(this)

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a value column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
@JvmName("valueColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(): ColumnDelegate<T> = ColumnDelegate(this)

/**
 * Creates a [ColumnAccessor] for the value column with the given [name] nested in the receiver column group:
 * the created path is the receiver's path with [name] appended.
 * Without a type argument, the referenced values have type `Any?`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column.
 */
public fun ColumnReference<DataRow<*>>.valueColumn(name: String): ColumnAccessor<Any?> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [ColumnAccessor] for the value column with the given [name] nested in the receiver column group:
 * the created path is the receiver's path with [name] appended.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column.
 */
@JvmName("valueColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.valueColumn(name: String): ColumnAccessor<T> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [ColumnAccessor] for the value column at the given [path] nested in the receiver column group:
 * the created path is the receiver's path with [path] appended.
 * Without a type argument, the referenced values have type `Any?`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced column, relative to the receiver.
 */
public fun ColumnReference<DataRow<*>>.valueColumn(path: ColumnPath): ColumnAccessor<Any?> =
    ColumnAccessorImpl(this.path() + path)

/**
 * Creates a [ColumnAccessor] for the value column at the given [path] nested in the receiver column group:
 * the created path is the receiver's path with [path] appended.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
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
 * Creates a [ColumnDelegate] for the `by` property syntax of a column group reference.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * Without a type argument, the referenced rows have type [DataRow]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun columnGroup(): ColumnDelegate<DataRow<*>> = column()

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a column group reference.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
@JvmName("columnGroupTyped")
public fun <T> columnGroup(): ColumnDelegate<DataRow<T>> = column()

/**
 * Creates a [ColumnAccessor] for the column group with the given [name].
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * Without a type argument, the referenced rows have type [DataRow]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column group.
 */
public fun columnGroup(name: String): ColumnAccessor<DataRow<*>> = column(name)

/**
 * Creates a [ColumnAccessor] for the column group with the given [name].
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column group.
 */
@JvmName("columnGroupTyped")
public fun <T> columnGroup(name: String): ColumnAccessor<DataRow<T>> = column(name)

/**
 * Creates a [ColumnAccessor] for the column group at the given [path].
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * Without a type argument, the referenced rows have type [DataRow]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced column group.
 */
public fun columnGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = column(path)

/**
 * Creates a [ColumnAccessor] for the column group at the given [path].
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
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
 * Creates a [ColumnDelegate] for the `by` property syntax of a column group reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * Without a type argument, the referenced rows have type [DataRow]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun ColumnReference<DataRow<*>>.columnGroup(): ColumnDelegate<DataRow<*>> = ColumnDelegate(this)

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a column group reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
@JvmName("columnGroupTyped")
public fun <T> ColumnReference<DataRow<*>>.columnGroup(): ColumnDelegate<DataRow<T>> = ColumnDelegate(this)

/**
 * Creates a [ColumnAccessor] for the column group with the given [name] nested in the receiver column group:
 * the created path is the receiver's path with [name] appended.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * Without a type argument, the referenced rows have type [DataRow]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column group.
 */
public fun ColumnReference<DataRow<*>>.columnGroup(name: String): ColumnAccessor<DataRow<*>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [ColumnAccessor] for the column group with the given [name] nested in the receiver column group:
 * the created path is the receiver's path with [name] appended.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced column group.
 */
@JvmName("columnGroupTyped")
public fun <T> ColumnReference<DataRow<*>>.columnGroup(name: String): ColumnAccessor<DataRow<T>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [ColumnAccessor] for the column group at the given [path] nested in the receiver column group:
 * the created path is the receiver's path with [path] appended.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * Without a type argument, the referenced rows have type [DataRow]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced column group, relative to the receiver.
 */
public fun ColumnReference<DataRow<*>>.columnGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
    ColumnAccessorImpl(this.path() + path)

/**
 * Creates a [ColumnAccessor] for the column group at the given [path] nested in the receiver column group:
 * the created path is the receiver's path with [path] appended.
 * The cells of a column group are themselves rows; see [ColumnGroup].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
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
 * Creates a [ColumnDelegate] for the `by` property syntax of a frame column reference.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * Without a type argument, the referenced frames have type [DataFrame]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun frameColumn(): ColumnDelegate<DataFrame<*>> = column()

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a frame column reference.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
@JvmName("frameColumnTyped")
public fun <T> frameColumn(): ColumnDelegate<DataFrame<T>> = column()

/**
 * Creates a [ColumnAccessor] for the frame column with the given [name].
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * Without a type argument, the referenced frames have type [DataFrame]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced frame column.
 */
public fun frameColumn(name: String): ColumnAccessor<DataFrame<*>> = column(name)

/**
 * Creates a [ColumnAccessor] for the frame column with the given [name].
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced frame column.
 */
@JvmName("frameColumnTyped")
public fun <T> frameColumn(name: String): ColumnAccessor<DataFrame<T>> = column(name)

/**
 * Creates a [ColumnAccessor] for the frame column at the given [path].
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * Without a type argument, the referenced frames have type [DataFrame]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced frame column.
 */
public fun frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<*>> = column(path)

/**
 * Creates a [ColumnAccessor] for the frame column at the given [path].
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
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
 * Creates a [ColumnDelegate] for the `by` property syntax of a frame column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * Without a type argument, the referenced frames have type [DataFrame]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
public fun ColumnReference<DataRow<*>>.frameColumn(): ColumnDelegate<DataFrame<*>> = ColumnDelegate(this)

/**
 * Creates a [ColumnDelegate] for the `by` property syntax of a frame column reference nested in the
 * receiver column group: the referenced column path is the receiver's path with the property name appended.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 */
@JvmName("frameColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.frameColumn(): ColumnDelegate<DataFrame<T>> = ColumnDelegate(this)

/**
 * Creates a [ColumnAccessor] for the frame column with the given [name] nested in the receiver column group:
 * the created path is the receiver's path with [name] appended.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * Without a type argument, the referenced frames have type [DataFrame]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced frame column.
 */
public fun ColumnReference<DataRow<*>>.frameColumn(name: String): ColumnAccessor<DataFrame<*>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [ColumnAccessor] for the frame column with the given [name] nested in the receiver column group:
 * the created path is the receiver's path with [name] appended.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [name] Name of the referenced frame column.
 */
@JvmName("frameColumnTyped")
public fun <T> ColumnReference<DataRow<*>>.frameColumn(name: String): ColumnAccessor<DataFrame<T>> =
    ColumnAccessorImpl(path() + name)

/**
 * Creates a [ColumnAccessor] for the frame column at the given [path] nested in the receiver column group:
 * the created path is the receiver's path with [path] appended.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * Without a type argument, the referenced frames have type [DataFrame]`<*>`.
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
 *
 * @param [path] Path to the referenced frame column, relative to the receiver.
 */
public fun ColumnReference<DataRow<*>>.frameColumn(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
    ColumnAccessorImpl(this.path() + path)

/**
 * Creates a [ColumnAccessor] for the frame column at the given [path] nested in the receiver column group:
 * the created path is the receiver's path with [path] appended.
 * Each cell of a frame column is a whole [DataFrame]; see [FrameColumn].
 * @include [ConstructorsDocs.ColumnReferenceUsageSnippet]
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
 * The referenced column name is taken from the property name; the [ColumnName] annotation overrides it.
 * When the delegate is created on a parent column group reference, the property name is appended
 * to the parent's path.
 *
 * For more information: {@include [DocumentationUrls.ColumnSelectors]}
 */
public class ColumnDelegate<T>(private val parent: ColumnReference<DataRow<*>>? = null) {

    /**
     * Creates a [ColumnAccessor] named after [property]; the [ColumnName] annotation overrides the name.
     * With a parent column group reference, the name is appended to its path.
     *
     * @param [property] The property whose name is used for the referenced column.
     */
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): ColumnAccessor<T> = named(property.columnName)

    /**
     * Creates a [ColumnAccessor] with the given [name] instead of a property name.
     * With a parent column group reference, [name] is appended to its path.
     *
     * @param [name] Name of the referenced column.
     */
    public infix fun named(name: String): ColumnAccessor<T> =
        parent?.let { ColumnAccessorImpl(it.path() + name) } ?: ColumnAccessorImpl(name)
}

// endregion

// region create DataColumn

/**
 * Creates a [DataColumn] with the given [values] and an empty name.
 *
 * The column type is inferred from the values, with the reified type argument [T] as an upper bound:
 * `columnOf<Number>(1, 2)` has type `Int`, while `columnOf<Number>(1, 2.5)` has type `Number`.
 * When the values don't share a reifiable type, pass [T] explicitly — e.g. `columnOf<Number>(1, 2.5)`;
 * without it, such a call does not compile.
 *
 * Lists and other collections are stored as cell values: `columnOf(listOf(1, 2))` creates
 * a value column of type `List<Int>`, not a column group.
 *
 * See also `column`, `valueColumn`, `columnGroup` and `frameColumn`,
 * which create references to columns rather than columns.
 *
 * For more information: {@include [DocumentationUrls.ColumnOf]}
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
 * Creates a [ColumnGroup] with the given [values] as its children and an empty name.
 *
 * @param [values] The columns that become the children of the group.
 */
public fun columnOf(vararg values: BaseColumn<*>): DataColumn<DataRow<*>> = columnOf(values.asIterable()).forceResolve()

/**
 * {@include [DocumentationUrls.ColumnOf]}
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
 * Creates a [FrameColumn] with the given [frames] as its values and an empty name.
 *
 * @param [frames] The frames that become the values of the column.
 */
@Refine
@Interpretable("ColumnOfFrames")
public fun <T> columnOf(vararg frames: DataFrame<T>): FrameColumn<T> = columnOf(frames.asIterable()).forceResolve()

/**
 * Creates a [ColumnGroup] with the given [columns] as its children and an empty name.
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
 * Creates a [FrameColumn] with the given [frames] as its values and an empty name.
 *
 * @param [frames] The frames that become the values of the column.
 */
public fun <T> columnOf(frames: Iterable<DataFrame<T>>): FrameColumn<T> =
    DataColumn.createFrameColumn(
        name = "",
        groups = frames.toList(),
    ).forceResolve()

/**
 * Creates a [DataColumn] of type [T] from [values] with an empty name.
 *
 * Unlike `columnOf`, the declared type [T] is used as is, without inference from the values:
 * `column(listOf<Number>(1, 2))` has type `Number`, while `columnOf(1, 2)` has type `Int`.
 * To create an empty column with a declared type, pass an empty list: `column(emptyList<Int>())`.
 *
 * For more information: {@include [DocumentationUrls.ColumnOf]}
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
 * Creates a new [DataFrame] with the given [columns].
 *
 * @include [ConstructorsDocs.DataFrameOfContractSnippet]
 *
 * @param [columns] Columns for the [DataFrame].
 */
public fun dataFrameOf(columns: Iterable<BaseColumn<*>>): DataFrame<*> {
    val cols = columns.map { it.unbox() }
    val nrow = if (cols.isEmpty()) 0 else cols[0].size
    return DataFrameImpl<Unit>(cols, nrow)
}

/**
 * Creates a [DataFrame] from the given name-to-column [columns] pairs.
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
 * @include [ConstructorsDocs.DataFrameOfContractSnippet]
 *
 * @param [columns] The name-to-column pairs of the new [DataFrame].
 */
@Refine
@JvmName("dataFrameOfColumns")
@Interpretable("DataFrameOfPairs")
public fun dataFrameOf(vararg columns: Pair<String, BaseColumn<*>>): DataFrame<*> =
    dataFrameOf(columns.map { (name, col) -> col.rename(name) })

/**
 * Creates a [DataFrameBuilder] for a [DataFrame] whose column names are taken from [header]
 * (see [ColumnReference.name]).
 * Call the builder with the cell values in row-major order — see [DataFrameBuilder].
 *
 * For more information: {@include [DocumentationUrls.DataFrameOf]}
 *
 * @param [header] The column references providing the names of the columns.
 */
public fun dataFrameOf(vararg header: ColumnReference<*>): DataFrameBuilder = DataFrameBuilder(header.map { it.name() })

/**
 * Creates a [DataFrame] with the given [columns].
 *
 * @include [ConstructorsDocs.DataFrameOfContractSnippet]
 *
 * @param [columns] The columns of the new [DataFrame].
 */
public fun dataFrameOf(vararg columns: BaseColumn<*>): DataFrame<*> = dataFrameOf(columns.asIterable())

/**
 * Creates a [DataFrameBuilder] for a [DataFrame] with the given column [header].
 * Call the builder with the cell values in row-major order — see [DataFrameBuilder].
 *
 * For more information: {@include [DocumentationUrls.DataFrameOf]}
 *
 * @param [header] The names of the columns.
 */
@Interpretable("DataFrameOf0")
public fun dataFrameOf(vararg header: String): DataFrameBuilder = dataFrameOf(header.toList())

/**
 * Creates a [DataFrame] with the given column [header] where the values of each column
 * are produced by [fill]: it is called once per column name and returns the values for that column.
 *
 * For more information: {@include [DocumentationUrls.DataFrameOf]}
 *
 * @param [header] The names of the columns.
 * @param [fill] Produces the values of a column by its name.
 */
public inline fun <reified C> dataFrameOf(
    vararg header: String,
    crossinline fill: (String) -> Iterable<C>,
): DataFrame<*> = dataFrameOf(header.asIterable()).invoke(fill)

/**
 * Creates a [DataFrameBuilder] for a [DataFrame] with the given column [header].
 * Call the builder with the cell values in row-major order — see [DataFrameBuilder].
 *
 * For more information: {@include [DocumentationUrls.DataFrameOf]}
 *
 * @param [header] The names of the columns.
 */
public fun dataFrameOf(header: Iterable<String>): DataFrameBuilder = DataFrameBuilder(header.asList())

/**
 * Creates a [DataFrame] from the given name-to-values [columns] pairs;
 * the type of each column is inferred from its values.
 *
 * @include [ConstructorsDocs.DataFrameOfContractSnippet]
 *
 * @param [columns] The name-to-values pairs of the new [DataFrame].
 */
@Refine
@Interpretable("DataFrameOf3")
public fun dataFrameOf(vararg columns: Pair<String, List<Any?>>): DataFrame<*> =
    columns.map { it.second.toColumn(it.first, Infer.Type) }.toDataFrame()

/**
 * Creates a [DataFrame] with the given column names and the cell values in row-major order.
 */
@Deprecated(DATAFRAME_OF_WITH_VALUES, ReplaceWith("dataFrameOf(header).withValues(values)"))
public fun dataFrameOf(header: Iterable<String>, values: Iterable<Any?>): DataFrame<*> =
    dataFrameOf(header).withValues(values)

/**
 * Creates a [DataFrame] where each column is produced by [fill] from an element of [header];
 * the column name is the string representation of that element.
 *
 * For more information: {@include [DocumentationUrls.DataFrameOf]}
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
 * Creates a [DataFrameBuilder] for a [DataFrame] whose column names are the characters of [header].
 * Call the builder with the cell values in row-major order — see [DataFrameBuilder].
 *
 * For more information: {@include [DocumentationUrls.DataFrameOf]}
 *
 * @param [header] The characters naming the columns.
 */
public fun dataFrameOf(header: CharProgression): DataFrameBuilder = dataFrameOf(header.map { it.toString() })

/**
 * A builder for a [DataFrame] with the given header of column names.
 *
 * Call [invoke][DataFrameBuilder.invoke] with all cell values in row-major order:
 * the values are distributed to the columns row by row, and the type of each column
 * is inferred from its values:
 * ```kotlin
 * val df = dataFrameOf("a", "b")(1, 2, 3, 4) // a: [1, 3], b: [2, 4]
 * ```
 *
 * Alternatively, [withColumns] or [invoke][DataFrameBuilder.invoke] with a values builder
 * create the columns one by one, and the [fill], [nulls] and `random*` functions create
 * constant or random frames.
 *
 * For more information: {@include [DocumentationUrls.DataFrameOf]}
 */
public class DataFrameBuilder(private val header: List<String>) {

    /**
     * Creates a [DataFrame] from [columns], renaming them to the header names.
     *
     * @throws [IllegalArgumentException] if the number of [columns] differs from the number of header names.
     */
    public operator fun invoke(vararg columns: DataColumn<*>): DataFrame<*> = invoke(columns.asIterable())

    /**
     * Creates a [DataFrame] from [columns], renaming them to the header names.
     *
     * @throws [IllegalArgumentException] if the number of [columns] differs from the number of header names.
     */
    public operator fun invoke(columns: Iterable<DataColumn<*>>): DataFrame<*> {
        val cols = columns.asList()
        require(cols.size == header.size) { "Number of columns differs from number of column names" }
        return cols.mapIndexed { i, col ->
            col.rename(header[i])
        }.toDataFrame()
    }

    /**
     * Creates a [DataFrame] by distributing [values] to the columns in row-major order:
     * the values are filled row by row, and the type of each column is inferred from its values.
     *
     * @throws [IllegalArgumentException] if the number of [values] is not divisible by the number of
     * header names — in particular, when the header is empty.
     */
    @Refine
    @Interpretable("DataFrameBuilderInvoke0")
    public operator fun invoke(vararg values: Any?): DataFrame<*> = withValues(values.asIterable())

    /** Distributes [values] to the columns in row-major order. */
    @JvmName("invoke1")
    internal fun withValues(values: Iterable<Any?>): DataFrame<*> =
        (header to values.asList()).withValuesImpl().map { (name, values) ->
            DataColumn.createByInference(name, values)
        }.toDataFrame()

    /**
     * Creates a [DataFrame] from the values of [args], distributed to the columns in row-major order.
     *
     * @throws [IllegalArgumentException] if the number of values is not divisible by the number of
     * header names.
     */
    public operator fun invoke(args: Sequence<Any?>): DataFrame<*> = invoke(*args.toList().toTypedArray())

    /**
     * Creates a [DataFrame] by building each column with [columnBuilder];
     * the created column is renamed to its header name.
     *
     * @param [columnBuilder] Builds a column by its header name.
     */
    public fun withColumns(columnBuilder: (String) -> DataColumn<*>): DataFrame<*> =
        header
            .map { columnBuilder(it) named it } // create the columns and make sure to rename them to the given header
            .toDataFrame()

    /**
     * Creates a [DataFrame] where the values of each column are produced by [valuesBuilder]
     * for that column's name; the column type comes from [T].
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
     * Creates a [DataFrame] where every column has [nrow] cells equal to [value].
     * The column type is [C]; a `null` [value] makes the columns nullable.
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
     * Creates a [DataFrame] where every column is a frame column with [nrow] copies of [dataFrame].
     *
     * @param [nrow] The number of rows in every column.
     * @param [dataFrame] The [DataFrame] stored in every cell.
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
     * Creates a [DataFrame] where every column has [nrow] `null` values of type [C]`?`.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderNulls")
    public inline fun <reified C> nulls(nrow: Int): DataFrame<*> = fill<C?>(nrow, null)

    /**
     * Creates a [DataFrame] where every column has [nrow] values produced by [init],
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
     * Creates a [DataFrame] where every column has [nrow] values produced by [init];
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
     * Creates a [DataFrame] where every column has [nrow] random [Int] values.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomInt")
    public fun randomInt(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextInt() }

    /**
     * Creates a [DataFrame] where every column has [nrow] random [Int] values within [range].
     * Convenient for tests and demos.
     *
     * @param [range] The range of the random values (both bounds included).
     * @throws [IllegalArgumentException] if [range] is empty.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomIntRange")
    public fun randomInt(nrow: Int, range: IntRange): DataFrame<*> = fillNotNull(nrow) { Random.nextInt(range) }

    /**
     * Creates a [DataFrame] where every column has [nrow] random [Double] values between `0.0` and `1.0`.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomDouble")
    public fun randomDouble(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextDouble() }

    /**
     * Creates a [DataFrame] where every column has [nrow] random [Double] values within [range].
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
     * Creates a [DataFrame] where every column has [nrow] random [Float] values between `0.0` and `1.0`.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomFloat")
    public fun randomFloat(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextFloat() }

    /**
     * Creates a [DataFrame] where every column has [nrow] random [Long] values.
     * Convenient for tests and demos.
     *
     * @param [nrow] The number of rows in every column.
     */
    @Refine
    @Interpretable("DataFrameBuilderRandomLong")
    public fun randomLong(nrow: Int): DataFrame<*> = fillNotNull(nrow) { Random.nextLong() }

    /**
     * Creates a [DataFrame] where every column has [nrow] random [Long] values within [range].
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
     * Creates a [DataFrame] where every column has [nrow] random [Boolean] values.
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
 * {@include [DocumentationUrls.DynamicDataFrameBuilder]}
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
     * - If [checkDuplicateValues] is `true`, the method checks whether the new column has identical values
     *   to an existing column with the same name. If the values match, the column is not added.
     *
     * {@include [DocumentationUrls.DynamicDataFrameBuilder]}
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
     * - If the [checkDuplicateValues] property of the builder is `true`, the method checks whether the new column
     *   has identical values to an existing column with the same name. If the values match, the column is not added.
     *
     * {@include [DocumentationUrls.DynamicDataFrameBuilder]}
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
     * {@include [DocumentationUrls.DynamicDataFrameBuilder]}
     *
     * @param column The name of the column to retrieve.
     * @return The column corresponding to the specified name, or `null` if no such column exists.
     */
    public fun get(column: String): DataColumn<*>? = cols[column]

    /**
     * Converts the current [DynamicDataFrameBuilder] instance into a [DataFrame].
     * The resulting [DataFrame] is constructed from the columns stored in the builder.
     *
     * {@include [DocumentationUrls.DynamicDataFrameBuilder]}
     *
     * @return A [DataFrame] containing the columns defined in the [DynamicDataFrameBuilder].
     */
    public fun toDataFrame(): DataFrame<*> = cols.values.toDataFrame()
}

/**
 * Returns [DataFrame] with empty columns according to schema [T].
 *
 * To create [DataFrame] with empty columns or empty rows see [DataFrame.empty]
 *
 * {@include [DocumentationUrls.EmptyDataFrame]}
 *
 * @param T schema marker for [DataFrame]
 */
public inline fun <reified T> emptyDataFrame(): DataFrame<T> = DataFrame.emptyOf<T>()

// endregion

// region create ColumnPath

/**
 * Creates a [ColumnPath] from the given [columnNames], outermost first.
 * `pathOf()` without arguments returns the empty [ColumnPath];
 * [ColumnPath.name] of an empty path throws a [NoSuchElementException].
 *
 * For more information: {@include [DocumentationUrls.PathOf]}
 *
 * @param [columnNames] The names of the columns along the path.
 */
@Interpretable("PathOf")
public fun pathOf(vararg columnNames: String): ColumnPath =
    if (columnNames.isEmpty()) ColumnPath.EMPTY else ColumnPath(columnNames.asList())

// endregion
