package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.anyNull
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.asValues
import org.jetbrains.kotlinx.dataframe.impl.columns.forceResolve
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.util.COLUMN_REFERENCE_CAST_TO_NOT_NULLABLE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.SET_NULLABLE
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region String

/**
 * Creates a [ColumnAccessor] — a [ColumnReference] to a column identified by this name.
 *
 * The accessor is not resolved against any [DataFrame] yet; use it in the [ColumnsSelectionDsl]
 * or anywhere a [ColumnReference] is accepted.
 *
 * See also [String.toColumnOf] (typed variant), [ColumnPath.toColumnAccessor] (for nested columns),
 * [ColumnReference.toColumnAccessor].
 */
public fun String.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

/**
 * Creates a [ColumnAccessor] statically typed as [T] to a column identified by this name.
 *
 * [T] is not checked at runtime — no column or values are inspected, only the static type of
 * the accessor changes. Useful when the schema is known.
 *
 * See also [String.toColumnAccessor].
 */
public fun <T> String.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

// endregion

// region ColumnPath

/**
 * Creates a [ColumnAccessor] statically typed as [T] to a column identified by this path.
 *
 * [T] is not checked at runtime — only the static type of the accessor changes.
 *
 * See also [ColumnPath.toColumnAccessor], [String.toColumnOf].
 */
@Deprecated(DEPRECATED_ACCESS_API)
public fun <T> ColumnPath.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

/**
 * Creates a [ColumnAccessor] — a [ColumnReference] to a column identified by this path.
 *
 * Unlike [String.toColumnAccessor], the path can address a column nested inside a [ColumnGroup].
 */
public fun ColumnPath.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

/**
 * Creates a [ColumnAccessor] statically typed as [DataRow]`<*>` to a column group identified by this path,
 * so it can be used where a reference to a [ColumnGroup] is expected.
 *
 * See also [ColumnPath.toColumnAccessor], [ColumnPath.toFrameColumnAccessor].
 */
@Deprecated(DEPRECATED_ACCESS_API)
public fun ColumnPath.toColumnGroupAccessor(): ColumnAccessor<DataRow<*>> = ColumnAccessorImpl(this)

/**
 * Creates a [ColumnAccessor] statically typed as [DataFrame]`<*>` to a frame column identified by this path,
 * so it can be used where a reference to a [FrameColumn] is expected.
 *
 * See also [ColumnPath.toColumnAccessor], [ColumnPath.toColumnGroupAccessor].
 */
@Deprecated(DEPRECATED_ACCESS_API)
public fun ColumnPath.toFrameColumnAccessor(): ColumnAccessor<DataFrame<*>> = ColumnAccessorImpl(this)

// endregion

// region ColumnReference

/**
 * Converts this [ColumnReference] into a [ColumnAccessor].
 *
 * If this reference is already a [ColumnAccessor], the same object is returned;
 * otherwise a new accessor for the reference's [path][ColumnPath] is created.
 *
 * See also [String.toColumnAccessor], [ColumnPath.toColumnAccessor].
 */
public fun <T> ColumnReference<T>.toColumnAccessor(): ColumnAccessor<T> =
    when (this) {
        is ColumnAccessor<T> -> this
        else -> ColumnAccessorImpl(path())
    }

// endregion

// region KProperty

/**
 * Creates a [ColumnAccessor] to the column named after this property.
 *
 * The `@ColumnName` annotation, if present on the property, overrides its name.
 *
 * See also [String.toColumnAccessor], [ColumnPath.toColumnAccessor], [ColumnReference.toColumnAccessor].
 */
@Deprecated(DEPRECATED_ACCESS_API)
public fun <T> KProperty<T>.toColumnAccessor(): ColumnAccessor<T> = ColumnAccessorImpl<T>(columnName)

// endregion

// region DataColumn

/**
 * Creates a [DataFrame] with this column as its only column.
 *
 * See also [DataFrame.asColumnGroup], [Iterable.toColumnGroup].
 */
public fun BaseColumn<*>.toDataFrame(): DataFrame<*> = dataFrameOf(listOf(this))

/**
 * Casts this column to a [ValueColumn] of {@get [AsNumbersDocs.TYPE] [Number][Number]}.
 * Requires [isNumber] to be `true` for this column, otherwise throws an [IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted,
 * and the runtime type of the column stays the same.
 */
@ExcludeFromSources
private interface AsNumbersDocs {

    // Target type, like `[Number]`
    typealias TYPE = Nothing
}

/**
 * @include [AsNumbersDocs]
 * @set [AsNumbersDocs.TYPE] [Number?][Number]
 */
@JvmName("asNumberAnyNullable")
public fun DataColumn<Any?>.asNumbers(): ValueColumn<Number?> {
    require(isNumber())
    return this.asValues()
}

/**
 * @include [AsNumbersDocs]
 * @set [AsNumbersDocs.TYPE] [Number][Number]
 */
@JvmName("asNumberAny")
public fun DataColumn<Any>.asNumbers(): ValueColumn<Number> {
    require(isNumber())
    return this as ValueColumn<Number>
}

/**
 * Casts this column to a [DataColumn] of {@get [AsComparableDocs.TYPE] Comparable<T>} so its values can be
 * ordered and compared with each other.
 * Requires [valuesAreComparable] to be `true` for this column, otherwise throws
 * an [IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted,
 * and the runtime type of the column stays the same.
 */
@ExcludeFromSources
private interface AsComparableDocs {

    // Target type, like `[Comparable]`<`[T]`>`
    typealias TYPE = Nothing
}

/**
 * @include [AsComparableDocs]
 * @set [AsComparableDocs.TYPE] [Comparable]`<`[T]`>`
 */
public fun <T : Any> DataColumn<T>.asComparable(): DataColumn<Comparable<T>> {
    require(valuesAreComparable())
    return this as DataColumn<Comparable<T>>
}

/**
 * @include [AsComparableDocs]
 * @set [AsComparableDocs.TYPE] [Comparable]`<`[T]`>`?
 */
@JvmName("asComparableNullable")
public fun <T : Any?> DataColumn<T?>.asComparable(): DataColumn<Comparable<T>?> {
    require(valuesAreComparable())
    return this as DataColumn<Comparable<T>?>
}

/**
 * Changes the static type of this [ColumnReference] from [T]? to [T].
 *
 * NOTE: This doesn't check whether the referenced column actually has no nulls; it just casts.
 *
 * See also [DataColumn.castToNotNullable], which checks the column first, and
 * [ColumnReference.castToNullable] for the reverse cast.
 */
@Deprecated(COLUMN_REFERENCE_CAST_TO_NOT_NULLABLE, ReplaceWith("cast()"))
public fun <T> ColumnReference<T?>.castToNotNullable(): ColumnReference<T> = cast()

/**
 * Casts this column from [DataColumn]`<`[T]?`>` to [DataColumn]`<`[T]`>`.
 * Requires the column to [have no nulls][DataColumn.hasNulls], otherwise throws
 * an [IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted.
 *
 * See also [DataColumn.castToNullable] for the reverse cast.
 */
public fun <T> DataColumn<T?>.castToNotNullable(): DataColumn<T> {
    require(!hasNulls()) { "Column `$name` has nulls" }
    return this as DataColumn<T>
}

/**
 * Changes the static type of this column from [DataColumn]`<`[T]`>` to [DataColumn]`<`[T]?`>`.
 * This cast always succeeds: no values are copied or converted, and no checks are performed.
 *
 * See also [DataColumn.castToNotNullable] for the reverse cast.
 */
public fun <T> DataColumn<T>.castToNullable(): DataColumn<T?> = cast()

/**
 * Changes the static type of this [ColumnReference] from [T] to [T]?.
 * This cast always succeeds: no checks are performed.
 */
public fun <T> ColumnReference<T>.castToNullable(): ColumnReference<T?> = cast()

/**
 * Returns this column: with [nullable] = `true` it is [castToNullable][DataColumn.castToNullable],
 * with [nullable] = `false` it is [castToNotNullable][DataColumn.castToNotNullable], which throws
 * an [IllegalArgumentException] when the column [has nulls][DataColumn.hasNulls].
 *
 * NOTE: The column is not changed: [type][DataColumn.type] and [hasNulls][DataColumn.hasNulls] stay
 * the same, and the [DataColumn]`<*>` return type doesn't expose the new static nullability.
 */
@Deprecated(SET_NULLABLE)
public fun DataColumn<*>.setNullable(nullable: Boolean): DataColumn<*> =
    if (nullable) {
        this.castToNullable()
    } else {
        this.castToNotNullable()
    }

// region to array

/**
 * Converts the values of this [DataColumn]`<`[Number]`>` to {@get [ToArrayDocs.TYPE] Float} and returns them
 * as a new primitive `{@get [ToArrayDocs.TYPE] Float}Array`. The values are converted the same way
 * [convertTo] does, and the returned array is a copy — changing it doesn't affect the column.
 */
@ExcludeFromSources
private interface ToArrayDocs {

    // Target numeric type, like `[Float]`
    typealias TYPE = Nothing
}

/**
 * Returns all values of this column copied into a new [Array].
 */
public inline fun <reified T> DataColumn<T>.toTypedArray(): Array<T> = toList().toTypedArray()

/**
 * @include [ToArrayDocs]
 * @set [ToArrayDocs.TYPE] Float
 */
public fun DataColumn<Number>.toFloatArray(): FloatArray = convertToFloat().toList().toFloatArray()

/**
 * @include [ToArrayDocs]
 * @set [ToArrayDocs.TYPE] Double
 */
public fun DataColumn<Number>.toDoubleArray(): DoubleArray = convertToDouble().toList().toDoubleArray()

/**
 * @include [ToArrayDocs]
 * @set [ToArrayDocs.TYPE] Int
 */
public fun DataColumn<Number>.toIntArray(): IntArray = convertToInt().toList().toIntArray()

/**
 * @include [ToArrayDocs]
 * @set [ToArrayDocs.TYPE] Long
 */
public fun DataColumn<Number>.toLongArray(): LongArray = convertToLong().toList().toLongArray()

/**
 * @include [ToArrayDocs]
 * @set [ToArrayDocs.TYPE] Short
 */
public fun DataColumn<Number>.toShortArray(): ShortArray = convertTo<Short>().toList().toShortArray()

/**
 * @include [ToArrayDocs]
 * @set [ToArrayDocs.TYPE] Byte
 */
public fun DataColumn<Number>.toByteArray(): ByteArray = convertTo<Byte>().toList().toByteArray()

// endregion

/**
 * Casts this column to a [ValueColumn].
 *
 * NOTE: This doesn't check whether the column is actually a [ValueColumn]; it just casts it.
 * If the column is a [ColumnGroup] or a [FrameColumn], a [ClassCastException] is thrown.
 *
 * See also [isValueColumn].
 */
public fun DataColumn<*>.asValueColumn(): ValueColumn<*> = this as ValueColumn<*>

/**
 * Casts this column to a [ColumnGroup].
 *
 * NOTE: This doesn't check whether the column is actually a [ColumnGroup]; it just casts it.
 * If the column is not a group, a [ClassCastException] is thrown.
 *
 * See also [isColumnGroup], [SingleColumn.asColumnGroup], [DataFrame.asColumnGroup].
 */
@JvmName("asColumnGroupUntyped")
public fun DataColumn<*>.asColumnGroup(): ColumnGroup<*> = this as ColumnGroup<*>

/**
 * Casts this column to a [FrameColumn].
 *
 * NOTE: This doesn't check whether the column is actually a [FrameColumn]; it just casts it.
 * If the column is not a frame column, a [ClassCastException] is thrown.
 *
 * See also [isFrameColumn], [DataColumn.asFrameColumn].
 */
@JvmName("asFrameColumnUntyped")
public fun DataColumn<*>.asFrameColumn(): FrameColumn<*> = this as FrameColumn<*>

/**
 * Casts this column of [DataFrame]s to a [FrameColumn].
 *
 * NOTE: This doesn't check whether the column is actually a [FrameColumn]; it just casts it.
 *
 * See also [AnyCol.asFrameColumn].
 */
public fun <T> DataColumn<DataFrame<T>>.asFrameColumn(): FrameColumn<T> =
    (this as AnyCol).asAnyFrameColumn().castFrameColumn()

/**
 * Casts this column of [DataRow]s to a [ColumnGroup].
 *
 * NOTE: This doesn't check whether the column is actually a [ColumnGroup]; it just casts it.
 *
 * See also [AnyCol.asColumnGroup], [DataColumn.asDataFrame].
 */
@JvmName("asGroupedT")
public fun <T> DataColumn<DataRow<T>>.asColumnGroup(): ColumnGroup<T> = (this as AnyCol).asColumnGroup().cast()

/**
 * Casts this column of [DataRow]s to a [DataFrame] — a view of the group's rows.
 * Same as [DataColumn.asColumnGroup].
 */
public fun <T> DataColumn<DataRow<T>>.asDataFrame(): DataFrame<T> = asColumnGroup()

// endregion

// region ColumnGroup

/**
 * Casts this [ColumnGroup] to a [DataColumn] of its [DataRow]s.
 *
 * A [ColumnGroup] is both a column and a [DataFrame]; the same object is returned,
 * only its static type changes. No data is copied.
 *
 * See also [FrameColumn.asDataColumn].
 */
public fun <T> ColumnGroup<T>.asDataColumn(): DataColumn<DataRow<T>> = this as DataColumn<DataRow<T>>

/**
 * Returns this [ColumnGroup] typed as a [DataFrame]:
 * a [ColumnGroup] is already a [DataFrame], so the same object is returned and nothing is copied.
 */
public fun <T> ColumnGroup<T>.asDataFrame(): DataFrame<T> = this

// endregion

// region SingleColumn

/**
 * ## As ColumnGroup
 *
 * Creates a [ColumnAccessor][ColumnAccessor]`<`[DataRow][DataRow]`<`[C][C\]`>>` from [this][this\].
 * This is especially useful when you want to use [ColumnGroup] functions in the [ColumnsSelectionDsl] but your column
 * type is not recognized as a [ColumnGroup].
 * If you're not sure whether a column is recognized as [ColumnGroup] or not, you can always call [asColumnGroup][asColumnGroup\]
 * and it will return the same type if it is already a [ColumnGroup].
 *
 * NOTE: This does not check whether the column is actually a [ColumnGroup] or not. It just casts it.
 *
 * #### For example:
 *
 * `df.`[select][DataFrame.select]` { `[first][ColumnsSelectionDsl.first]`().`[asColumnGroup][SingleColumn.asColumnGroup]`().`[firstCol][ColumnsSelectionDsl.firstCol]`() }`
 *
 * @receiver The column reference to cast to a [SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 * @param [C\] The type of the (group) column.
 * @return A [SingleColumn]`<`[DataRow][DataRow]`<`[C][C\]`>>`.
 */
@ExcludeFromSources
private typealias SingleColumnAsColumnGroupDocs = Nothing

/** @include [SingleColumnAsColumnGroupDocs] */
@Suppress("UNCHECKED_CAST")
public fun <C> SingleColumn<C>.asColumnGroup(): SingleColumn<DataRow<C>> = this as SingleColumn<DataRow<C>>

/** @include [SingleColumnAsColumnGroupDocs] */
@JvmName("asColumnGroupDataRow")
public fun <C> SingleColumn<DataRow<C>>.asColumnGroup(): SingleColumn<DataRow<C>> = this

// endregion

// region FrameColumn

/**
 * Returns this [FrameColumn] typed as a [DataColumn] of its (nullable) [DataFrame] values.
 * The same object is returned — no data is copied.
 *
 * See also [ColumnGroup.asDataColumn].
 */
public fun <T> FrameColumn<T>.asDataColumn(): DataColumn<DataFrame<T>?> = this

/**
 * Always throws an [IllegalArgumentException]: by design, a [ValueColumn] cannot contain
 * [DataFrame] values — such a column must be a [FrameColumn].
 * Use [asDataColumn] instead: it returns the same values typed as a plain [DataColumn].
 */
public fun <T> FrameColumn<T>.toValueColumn(): ValueColumn<DataFrame<T>?> =
    DataColumn.createValueColumn(name, toList(), type())

// endregion

// region ColumnSet

/**
 * Changes the static type argument of this {@get [AsCastDocs.RECEIVER] [ColumnSet]} to {@get [AsCastDocs.TYPE] [Number]}
 * without any checks or data changes.
 *
 * NOTE: This is a fully unchecked cast: no checks are performed, and the underlying columns
 * stay exactly as they are — neither their runtime types nor their values change.
 * When the columns actually contain values of another type, the wrong static type simply propagates.
 */
@ExcludeFromSources
private interface AsCastDocs {

    // Receiver description, like `[ColumnSet]`
    typealias RECEIVER = Nothing

    // Target type, like `[Number]`
    typealias TYPE = Nothing
}

/**
 * @include [AsCastDocs]
 *
 * See also [DataColumn.asNumbers], which checks [isNumber] first, and [ColumnSet.asComparable].
 *
 * @set [AsCastDocs.RECEIVER] [ColumnSet]
 * @set [AsCastDocs.TYPE] [Number][Number]
 */
@JvmName("asNumbersAny")
public fun ColumnSet<Any>.asNumbers(): ColumnSet<Number> = this as ColumnSet<Number>

/**
 * @include [AsCastDocs]
 *
 * See also [DataColumn.asNumbers], which checks [isNumber] first, and [SingleColumn.asComparable].
 *
 * @set [AsCastDocs.RECEIVER] [SingleColumn]
 * @set [AsCastDocs.TYPE] [Number][Number]
 */
@JvmName("asNumbersAny")
public fun SingleColumn<Any>.asNumbers(): SingleColumn<Number> = this as SingleColumn<Number>

/**
 * @include [AsCastDocs]
 *
 * See also [DataColumn.asNumbers], which checks [isNumber] first, and [ColumnSet.asComparable].
 *
 * @set [AsCastDocs.RECEIVER] [ColumnSet]
 * @set [AsCastDocs.TYPE] [Number?][Number]
 */
@JvmName("asNumbersAnyNullable")
public fun ColumnSet<Any?>.asNumbers(): ColumnSet<Number?> = this as ColumnSet<Number?>

/**
 * @include [AsCastDocs]
 *
 * See also [DataColumn.asNumbers], which checks [isNumber] first, and [SingleColumn.asComparable].
 *
 * @set [AsCastDocs.RECEIVER] [SingleColumn]
 * @set [AsCastDocs.TYPE] [Number?][Number]
 */
@JvmName("asNumbersAnyNullable")
public fun SingleColumn<Any?>.asNumbers(): SingleColumn<Number?> = this as SingleColumn<Number?>

/**
 * @include [AsCastDocs]
 *
 * See also [DataColumn.asComparable], which checks [valuesAreComparable] first, and [ColumnSet.asNumbers].
 *
 * @set [AsCastDocs.RECEIVER] [ColumnSet]
 * @set [AsCastDocs.TYPE] [Comparable]`<`[T]`>`
 */
public fun <T> ColumnSet<T>.asComparable(): ColumnSet<Comparable<T>> = this as ColumnSet<Comparable<T>>

/**
 * @include [AsCastDocs]
 *
 * See also [DataColumn.asComparable], which checks [valuesAreComparable] first, and [SingleColumn.asNumbers].
 *
 * @set [AsCastDocs.RECEIVER] [SingleColumn]
 * @set [AsCastDocs.TYPE] [Comparable]`<`[T]`>`
 */
public fun <T> SingleColumn<T>.asComparable(): SingleColumn<Comparable<T>> = this as SingleColumn<Comparable<T>>

// endregion

// region Iterable

/**
 * The [type][DataColumn.type] of the column is taken from the reified [T\];
 * the values themselves are not checked to be of type [T\].
 */
@ExcludeFromSources
private interface TypeFromReifiedDocs

/**
 * Creates a new [FrameColumn] named [name] with this iterable's [DataFrame]s as its values.
 *
 * See also [Iterable.toValueColumn], [Iterable.toColumnGroup].
 */
public fun <T> Iterable<DataFrame<T>>.toFrameColumn(name: String = ""): FrameColumn<T> =
    DataColumn.createFrameColumn(name, asList()).forceResolve()

/**
 * Creates a new [ValueColumn] named [name] with this iterable's values.
 *
 * @include [TypeFromReifiedDocs]
 *
 * See also [Array.toValueColumn], [Iterable.toColumn].
 */
public inline fun <reified T> Iterable<T>.toValueColumn(name: String = ""): ValueColumn<T> =
    DataColumn.createValueColumn(name, asList()).forceResolve()

/**
 * Creates a new [ValueColumn] with the name of [column] and this iterable's values.
 *
 * @include [TypeFromReifiedDocs]
 *
 * See also [Iterable.toValueColumn].
 */
@Deprecated(DEPRECATED_ACCESS_API, ReplaceWith("toValueColumn(column.name())"))
@AccessApiOverload
public inline fun <reified T> Iterable<T>.toValueColumn(column: ColumnAccessor<T>): ValueColumn<T> =
    toValueColumn(column.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <reified T> Iterable<T>.toValueColumn(column: KProperty<T>): ValueColumn<T> =
    toValueColumn(column.columnName)

/**
 * Indicates how [DataColumn.type] should be calculated.
 *
 * Used in [add], [insert], [convert], [map], [merge], [split] and other [DataFrame] operations
 */
public enum class Infer {

    /**
     * Use `reified` type argument of an inline [DataFrame] operation as [DataColumn.type].
     *
     * This is the most efficient but least safe option.
     */
    None,

    /**
     * Use `reified` type argument of an inline [DataFrame] operation as [DataColumn.type],
     * but compute [DataColumn.hasNulls] by checking column [DataColumn.values] for an actual presence of `null` values.
     */
    Nulls,

    /**
     * Infer [DataColumn.type] and [DataColumn.hasNulls] from actual [DataColumn.values] using an optionally provided
     * upper bound type as a hint to speed up the search.
     *
     * This is the least efficient but safest option.
     *
     * It's useful, for instance,
     * if you have a column of type `Any?` and want its schema type to be inferred based on the actual values.
     * In many cases, letting the library infer by `reified` types is enough and more efficient.
     */
    Type,

    ;

    /**
     * @param [infer\] [An enum][org.jetbrains.kotlinx.dataframe.api.Infer] that indicates how [DataColumn.type] should be calculated.
     * Either [None], [Nulls], or [Type].
     */
    @ExcludeFromSources
    internal typealias ParamDoc = Nothing
}

/**
 * Indicates how [DataColumn.hasNulls] (or, more accurately, DataColumn.type.isMarkedNullable) should be initialized from
 * expected schema and actual data when reading schema-defined data formats.
 */
public enum class NullabilityOptions {
    /**
     * Use only actual data, set [DataColumn.hasNulls] to `true` if and only if there are null values in the column.
     * On an empty dataset, use `false`.
     */
    Infer,

    /**
     * Set [DataColumn.hasNulls] to the expected value.
     * Throws an exception if the column should be not nullable but there are null values.
     */
    Checking,

    /**
     * Set [DataColumn.hasNulls] to the expected value by default.
     * Change `false` to `true` if the column should be not nullable but there are null values.
     */
    Widening,
}

/**
 * Thrown by [NullabilityOptions.applyNullability] in the [NullabilityOptions.Checking] mode
 * when the data contains `null` values although the schema expects the column to be not nullable.
 */
public class NullabilityException : Exception()

/**
 * @return whether the column should be marked nullable for the current [NullabilityOptions] value
 * with actual [data] and [expectedNulls] per some schema/signature.
 * @throws [NullabilityException] for [NullabilityOptions.Checking] if [expectedNulls] is false and [data] contains nulls.
 */
public fun NullabilityOptions.applyNullability(data: List<Any?>, expectedNulls: Boolean): Boolean {
    val hasNulls = data.anyNull()
    return when (this) {
        NullabilityOptions.Infer -> hasNulls

        NullabilityOptions.Checking -> {
            if (!expectedNulls && hasNulls) {
                throw NullabilityException()
            }
            expectedNulls
        }

        NullabilityOptions.Widening -> {
            expectedNulls || hasNulls
        }
    }
}

/**
 * Creates a new [DataColumn] named [name] with this iterable's values.
 *
 * By default ([Infer.Nulls]), the [type][DataColumn.type] of the column is taken from the reified [T],
 * with its nullability computed by checking the values for `null`.
 * With [Infer.None], the nullability is taken from [T] without checking the values.
 * With [Infer.Type], both the type and the nullability are inferred from the values themselves.
 *
 * For more information: {@include [DocumentationUrls.ToColumn]}
 *
 * For example:
 * ```kotlin
 * listOf("Alice", "Bob").toColumn("name")
 * ```
 *
 * @param [name] The name of the new column.
 * @param [infer] An [enum][Infer] that indicates how [type][DataColumn.type] should be calculated.
 * Either [Infer.None], [Infer.Nulls], or [Infer.Type].
 * @return A new [DataColumn] with this iterable's values.
 */
public inline fun <reified T> Iterable<T>.toColumn(name: String = "", infer: Infer = Infer.Nulls): DataColumn<T> =
    if (infer == Infer.Type) {
        DataColumn.createByInference(name, asList())
    } else {
        DataColumn.createByType(name, asList(), typeOf<T>(), infer)
    }.forceResolve()

/**
 * Creates a new [DataColumn] named [name] with this iterable's values, statically typed as [T].
 *
 * The values are cast to [T] unchecked — neither the values nor the nulls among them are checked.
 * Useful when the type is known from the context.
 *
 * For more information: {@include [DocumentationUrls.ToColumnOf]}
 *
 * For example:
 * ```kotlin
 * val values: List<Any?> = listOf(1, 2.5)
 * values.toColumnOf<Number?>("data") // type: Number?
 * ```
 */
public inline fun <reified T> Iterable<*>.toColumnOf(name: String = ""): DataColumn<T> =
    DataColumn.createByType(name, asList() as List<T>, typeOf<T>()).forceResolve()

/**
 * Creates a new [DataColumn] with the name of [ref] and this iterable's values.
 *
 * @include [TypeFromReifiedDocs]
 *
 * For more information: {@include [DocumentationUrls.ToColumn]}
 *
 * See also [Iterable.toColumn].
 */
@Deprecated(DEPRECATED_ACCESS_API, ReplaceWith("toColumn(ref.name())"))
@AccessApiOverload
public inline fun <reified T> Iterable<T>.toColumn(ref: ColumnReference<T>): DataColumn<T> =
    DataColumn.createByType(ref.name(), asList()).forceResolve()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <reified T> Iterable<T>.toColumn(property: KProperty<T>): DataColumn<T> =
    DataColumn.createByType(property.columnName, asList()).forceResolve()

/**
 * Creates a [ColumnPath] with this iterable's names as its segments.
 *
 * See also [Array.toPath].
 */
public fun Iterable<String>.toPath(): ColumnPath = ColumnPath(asList())

/**
 * Creates a new [ColumnGroup] named [name] that nests this iterable's columns.
 *
 * See also [DataFrame.asColumnGroup], [Iterable.toColumnGroupOf].
 */
public fun Iterable<BaseColumn<*>>.toColumnGroup(name: String): ColumnGroup<*> = dataFrameOf(this).asColumnGroup(name)

/**
 * Creates a new [ColumnGroup] with the name of [column] that nests this iterable's columns,
 * statically typed as [T] without a runtime check.
 *
 * See also [Iterable.toColumnGroup].
 */
@Deprecated(DEPRECATED_ACCESS_API, ReplaceWith("toColumnGroupOf(column.name())"))
@AccessApiOverload
public fun <T> Iterable<BaseColumn<*>>.toColumnGroup(column: ColumnGroupAccessor<T>): ColumnGroup<T> =
    dataFrameOf(this).cast<T>().asColumnGroup(column)

/**
 * Creates a new [ColumnGroup] named [name] that nests this iterable's columns,
 * statically typed as [T] without a runtime check.
 *
 * See also [Iterable.toColumnGroup].
 */
public fun <T> Iterable<BaseColumn<*>>.toColumnGroupOf(name: String): ColumnGroup<T> = toColumnGroup(name).cast()

// endregion

// region DataFrame

/**
 * Returns this [DataFrame] as a [Map] that maps each column name to a [List] of that column's values.
 *
 * The value lists are new copies — changing them doesn't affect this DataFrame.
 *
 * See also [DataRow.toMap] for a single row, [Map.toDataFrame] for the reverse direction.
 */
public fun DataFrame<*>.toMap(): Map<String, List<Any?>> = columns().associateBy({ it.name }, { it.toList() })

/**
 * Wraps this [DataFrame] into a [ColumnGroup] — a single group column named [name]
 * that contains all of this DataFrame's columns.
 *
 * If `this` is already a [ColumnGroup], only the name changes (like in [rename]);
 * otherwise a new group column is created. The nested columns are not copied.
 *
 * See also [Iterable.toColumnGroup], [BaseColumn.toDataFrame].
 */
public fun <T> DataFrame<T>.asColumnGroup(name: String = ""): ColumnGroup<T> =
    when (this) {
        is ColumnGroup<T> -> rename(name)
        else -> DataColumn.createColumnGroup(name, this)
    }

/**
 * Wraps this [DataFrame] into a [ColumnGroup] named as [column],
 * equivalent to [asColumnGroup] with the [name][ColumnGroupAccessor.name] of [column].
 */
@Deprecated(DEPRECATED_ACCESS_API, ReplaceWith("asColumnGroup(column.name())"))
@AccessApiOverload
public fun <T> DataFrame<T>.asColumnGroup(column: ColumnGroupAccessor<T>): ColumnGroup<T> = asColumnGroup(column.name)

// region as GroupedDataFrame

/**
 * Reinterprets this [DataFrame] as a [GroupBy] by treating the [FrameColumn] named [groupedColumnName]
 * as the groups. The remaining columns of this DataFrame become the key columns of the resulting [GroupBy],
 * and the groups column is placed last in it.
 *
 * This DataFrame must contain a frame column with this name.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
 *
 * For example:
 * ```kotlin
 * df.asGroupBy("data") // convert dataframe to GroupBy by interpreting the 'data' column as groups
 * ```
 *
 * @throws [IllegalArgumentException] if this DataFrame has no column named [groupedColumnName].
 * @throws [ClassCastException] if that column is not a [FrameColumn].
 */
public fun <T> DataFrame<T>.asGroupBy(groupedColumnName: String): GroupBy<T, T> {
    val groups = getFrameColumn(groupedColumnName)
    return asGroupBy { groups.cast() }
}

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> DataFrame<T>.asGroupBy(groupedColumn: ColumnReference<DataFrame<G>>): GroupBy<T, G> {
    val groups = getFrameColumn(groupedColumn.name()).castFrameColumn<G>()
    return asGroupBy { groups }
}

/**
 * Reinterprets this [DataFrame] as a [GroupBy] by treating its only [FrameColumn] as the groups.
 * The remaining columns of this DataFrame become the key columns of the resulting [GroupBy],
 * and the groups column is placed last in it.
 *
 * This DataFrame must contain exactly one frame column.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
 *
 * @throws [NoSuchElementException] if this DataFrame has no frame column.
 * @throws [IllegalArgumentException] if this DataFrame has more than one frame column.
 */
@Refine
@Interpretable("AsGroupByDefault")
public fun <T> DataFrame<T>.asGroupBy(): GroupBy<T, T> {
    val groupCol = columns().single { it.isFrameColumn() }.asAnyFrameColumn().castFrameColumn<T>()
    return asGroupBy { groupCol }
}

/**
 * Reinterprets this [DataFrame] as a [GroupBy] by treating the column selected by [selector]
 * as the groups. The selected column must be a [FrameColumn]. The remaining columns of this
 * DataFrame become the key columns of the resulting [GroupBy], and the groups column is placed last in it —
 * this DataFrame itself stays unchanged.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
 *
 * For example, where `data` is the accessor of the frame column:
 * ```kotlin
 * df.asGroupBy { data } // convert dataframe to GroupBy by interpreting the 'data' column as groups
 * ```
 */
@Refine
@Interpretable("AsGroupBy")
public fun <T, G> DataFrame<T>.asGroupBy(selector: ColumnSelector<T, DataFrame<G>>): GroupBy<T, G> {
    val column = getColumn(selector).asFrameColumn()
    return GroupByImpl(this.move { column }.toEnd(), column) { none() }
}

// endregion

// endregion

// region DataRow

/**
 * Returns a single-row [DataFrame] containing just this row.
 *
 * See also [Map.toDataRow], which builds a [DataRow] from a [Map].
 */
public fun <T> DataRow<T>.toDataFrame(): DataFrame<T> = owner[index..index]

/**
 * Returns this row as a [Map] that maps each column name to this row's value in that column.
 *
 * See also [DataFrame.toMap] for all rows at once, [Map.toDataRow] for the reverse direction.
 */
public fun DataRow<*>.toMap(): Map<String, Any?> = df().columns().associate { it.name() to it[index] }

/**
 * Creates a single [DataRow] from this map: each key becomes a column name,
 * and its value becomes the value of that column in the row.
 *
 * See also [DataRow.toMap] for the reverse direction.
 */
public fun Map<String, Any?>.toDataRow(): DataRow<*> {
    val df = mapValues { listOf(it.value) }.toDataFrame()
    return DataRowImpl(0, df)
}

// endregion

// region Array

/**
 * Creates a new [ValueColumn] named [name] with this array's values.
 *
 * @include [TypeFromReifiedDocs]
 *
 * See also [Iterable.toValueColumn].
 */
public inline fun <reified T> Array<T>.toValueColumn(name: String): ValueColumn<T> =
    DataColumn.createValueColumn(name, this.asList(), typeOf<T>())

/**
 * Creates a [ColumnPath] with this array's names as its segments.
 *
 * See also [Iterable.toPath].
 */
public fun Array<out String>.toPath(): ColumnPath = ColumnPath(this.asList())

// endregion
