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
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region String

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] — a [<code>ColumnReference</code>][ColumnReference] to a column identified by this name.
 *
 * The accessor is not resolved against any [<code>DataFrame</code>][DataFrame] yet; use it in the [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl]
 * or anywhere a [<code>ColumnReference</code>][ColumnReference] is accepted.
 *
 * See also [<code>String.toColumnOf</code>][String.toColumnOf] (typed variant), [<code>ColumnPath.toColumnAccessor</code>][ColumnPath.toColumnAccessor] (for nested columns),
 * [<code>KProperty.toColumnAccessor</code>][KProperty.toColumnAccessor], [<code>ColumnReference.toColumnAccessor</code>][ColumnReference.toColumnAccessor].
 */
public fun String.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] statically typed as [<code>T</code>][T] to a column identified by this name.
 *
 * [<code>T</code>][T] is not checked at runtime — no column or values are inspected, only the static type of
 * the accessor changes. Useful when the schema is known.
 *
 * See also [<code>String.toColumnAccessor</code>][String.toColumnAccessor], [<code>ColumnPath.toColumnOf</code>][ColumnPath.toColumnOf].
 */
public fun <T> String.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

// endregion

// region ColumnPath

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] statically typed as [<code>T</code>][T] to a column identified by this path.
 *
 * [<code>T</code>][T] is not checked at runtime — only the static type of the accessor changes.
 *
 * See also [<code>ColumnPath.toColumnAccessor</code>][ColumnPath.toColumnAccessor], [<code>String.toColumnOf</code>][String.toColumnOf].
 */
public fun <T> ColumnPath.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] — a [<code>ColumnReference</code>][ColumnReference] to a column identified by this path.
 *
 * Unlike [<code>String.toColumnAccessor</code>][String.toColumnAccessor], the path can address a column nested inside a [<code>ColumnGroup</code>][ColumnGroup].
 *
 * See also [<code>ColumnPath.toColumnOf</code>][ColumnPath.toColumnOf], [<code>ColumnPath.toColumnGroupAccessor</code>][ColumnPath.toColumnGroupAccessor], [<code>ColumnPath.toFrameColumnAccessor</code>][ColumnPath.toFrameColumnAccessor].
 */
public fun ColumnPath.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] statically typed as [<code>DataRow</code>][DataRow]`<*>` to a column group identified by this path,
 * so it can be used where a reference to a [<code>ColumnGroup</code>][ColumnGroup] is expected.
 *
 * See also [<code>ColumnPath.toColumnAccessor</code>][ColumnPath.toColumnAccessor], [<code>ColumnPath.toFrameColumnAccessor</code>][ColumnPath.toFrameColumnAccessor].
 */
public fun ColumnPath.toColumnGroupAccessor(): ColumnAccessor<DataRow<*>> = ColumnAccessorImpl(this)

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] statically typed as [<code>DataFrame</code>][DataFrame]`<*>` to a frame column identified by this path,
 * so it can be used where a reference to a [<code>FrameColumn</code>][FrameColumn] is expected.
 *
 * See also [<code>ColumnPath.toColumnAccessor</code>][ColumnPath.toColumnAccessor], [<code>ColumnPath.toColumnGroupAccessor</code>][ColumnPath.toColumnGroupAccessor].
 */
public fun ColumnPath.toFrameColumnAccessor(): ColumnAccessor<DataFrame<*>> = ColumnAccessorImpl(this)

// endregion

// region ColumnReference

/**
 * Converts this [<code>ColumnReference</code>][ColumnReference] into a [<code>ColumnAccessor</code>][ColumnAccessor].
 *
 * If this reference is already a [<code>ColumnAccessor</code>][ColumnAccessor], the same object is returned;
 * otherwise a new accessor for the reference's [<code>path</code>][ColumnPath] is created.
 *
 * See also [<code>String.toColumnAccessor</code>][String.toColumnAccessor], [<code>ColumnPath.toColumnAccessor</code>][ColumnPath.toColumnAccessor], [<code>KProperty.toColumnAccessor</code>][KProperty.toColumnAccessor].
 */
public fun <T> ColumnReference<T>.toColumnAccessor(): ColumnAccessor<T> =
    when (this) {
        is ColumnAccessor<T> -> this
        else -> ColumnAccessorImpl(path())
    }

// endregion

// region KProperty

/**
 * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] to the column named after this property.
 *
 * The `@ColumnName` annotation, if present on the property, overrides its name.
 *
 * See also [<code>String.toColumnAccessor</code>][String.toColumnAccessor], [<code>ColumnPath.toColumnAccessor</code>][ColumnPath.toColumnAccessor], [<code>ColumnReference.toColumnAccessor</code>][ColumnReference.toColumnAccessor].
 */
public fun <T> KProperty<T>.toColumnAccessor(): ColumnAccessor<T> = ColumnAccessorImpl<T>(columnName)

// endregion

// region DataColumn

/**
 * Creates a [<code>DataFrame</code>][DataFrame] with this column as its only column.
 *
 * See also [<code>DataFrame.asColumnGroup</code>][DataFrame.asColumnGroup], [<code>Iterable.toColumnGroup</code>][Iterable.toColumnGroup].
 */
public fun BaseColumn<*>.toDataFrame(): DataFrame<*> = dataFrameOf(listOf(this))

/**
 * Casts this column to a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] of [<code>Number?</code>][Number].
 * Requires [<code>isNumber</code>][org.jetbrains.kotlinx.dataframe.api.isNumber] to be `true` for this column, otherwise throws an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted,
 * and the runtime type of the column stays the same.
 */
@JvmName("asNumberAnyNullable")
public fun DataColumn<Any?>.asNumbers(): ValueColumn<Number?> {
    require(isNumber())
    return this.asValues()
}

/**
 * Casts this column to a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] of [<code>Number</code>][Number].
 * Requires [<code>isNumber</code>][org.jetbrains.kotlinx.dataframe.api.isNumber] to be `true` for this column, otherwise throws an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted,
 * and the runtime type of the column stays the same.
 */
@JvmName("asNumberAny")
public fun DataColumn<Any>.asNumbers(): ValueColumn<Number> {
    require(isNumber())
    return this as ValueColumn<Number>
}

/**
 * Casts this column to a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] of [<code>Comparable</code>][Comparable]`<`[<code>T</code>][T]`>` so its values can be
 * ordered and compared with each other.
 * Requires [<code>valuesAreComparable</code>][org.jetbrains.kotlinx.dataframe.api.valuesAreComparable] to be `true` for this column, otherwise throws
 * an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted,
 * and the runtime type of the column stays the same.
 */
public fun <T : Any> DataColumn<T>.asComparable(): DataColumn<Comparable<T>> {
    require(valuesAreComparable())
    return this as DataColumn<Comparable<T>>
}

/**
 * Casts this column to a [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn] of [<code>Comparable</code>][Comparable]`<`[<code>T</code>][T]`>`? so its values can be
 * ordered and compared with each other.
 * Requires [<code>valuesAreComparable</code>][org.jetbrains.kotlinx.dataframe.api.valuesAreComparable] to be `true` for this column, otherwise throws
 * an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted,
 * and the runtime type of the column stays the same.
 */
@JvmName("asComparableNullable")
public fun <T : Any?> DataColumn<T?>.asComparable(): DataColumn<Comparable<T>?> {
    require(valuesAreComparable())
    return this as DataColumn<Comparable<T>?>
}

/**
 * Changes the static type of this [<code>ColumnReference</code>][ColumnReference] from [<code>T</code>][T]? to [<code>T</code>][T].
 *
 * NOTE: This doesn't check whether the referenced column actually has no nulls; it just casts.
 *
 * See also [<code>DataColumn.castToNotNullable</code>][DataColumn.castToNotNullable], which checks the column first, and
 * [<code>ColumnReference.castToNullable</code>][ColumnReference.castToNullable] for the reverse cast.
 */
public fun <T> ColumnReference<T?>.castToNotNullable(): ColumnReference<T> = cast()

/**
 * Casts this column from [<code>DataColumn</code>][DataColumn]`<`[<code>T</code>][T]?`>` to [<code>DataColumn</code>][DataColumn]`<`[<code>T</code>][T]`>`.
 * Requires the column to [<code>have no nulls</code>][DataColumn.hasNulls], otherwise throws
 * an [<code>IllegalArgumentException</code>][IllegalArgumentException].
 *
 * NOTE: This is a cast, not a conversion: no values are copied or converted.
 *
 * See also [<code>DataColumn.castToNullable</code>][DataColumn.castToNullable] for the reverse cast, [<code>ColumnReference.castToNotNullable</code>][ColumnReference.castToNotNullable].
 */
public fun <T> DataColumn<T?>.castToNotNullable(): DataColumn<T> {
    require(!hasNulls()) { "Column `$name` has nulls" }
    return this as DataColumn<T>
}

/**
 * Changes the static type of this column from [<code>DataColumn</code>][DataColumn]`<`[<code>T</code>][T]`>` to [<code>DataColumn</code>][DataColumn]`<`[<code>T</code>][T]?`>`.
 * This cast always succeeds: no values are copied or converted, and no checks are performed.
 *
 * See also [<code>DataColumn.castToNotNullable</code>][DataColumn.castToNotNullable] for the reverse cast.
 */
public fun <T> DataColumn<T>.castToNullable(): DataColumn<T?> = cast()

/**
 * Changes the static type of this [<code>ColumnReference</code>][ColumnReference] from [<code>T</code>][T] to [<code>T</code>][T]?.
 * This cast always succeeds: no checks are performed.
 *
 * See also [<code>ColumnReference.castToNotNullable</code>][ColumnReference.castToNotNullable] for the reverse cast.
 */
public fun <T> ColumnReference<T>.castToNullable(): ColumnReference<T?> = cast()

/**
 * Returns this column: with [<code>nullable</code>][nullable] = `true` it is [<code>castToNullable</code>][DataColumn.castToNullable],
 * with [<code>nullable</code>][nullable] = `false` it is [<code>castToNotNullable</code>][DataColumn.castToNotNullable], which throws
 * an [<code>IllegalArgumentException</code>][IllegalArgumentException] when the column [<code>has nulls</code>][DataColumn.hasNulls].
 *
 * NOTE: The column is not changed: [<code>type</code>][DataColumn.type] and [<code>hasNulls</code>][DataColumn.hasNulls] stay
 * the same, and the [<code>DataColumn</code>][DataColumn]`<*>` return type doesn't expose the new static nullability.
 */
public fun DataColumn<*>.setNullable(nullable: Boolean): DataColumn<*> =
    if (nullable) {
        this.castToNullable()
    } else {
        this.castToNotNullable()
    }

// region to array

/**
 * Returns all values of this column copied into a new [<code>Array</code>][Array].
 */
public inline fun <reified T> DataColumn<T>.toTypedArray(): Array<T> = toList().toTypedArray()

/**
 * Converts the values of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Number</code>][Number]`>` to Float and returns them
 * as a new primitive `FloatArray`. The values are converted the same way
 * [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.api.convertTo] does, and the returned array is a copy — changing it doesn't affect the column.
 */
public fun DataColumn<Number>.toFloatArray(): FloatArray = convertToFloat().toList().toFloatArray()

/**
 * Converts the values of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Number</code>][Number]`>` to Double and returns them
 * as a new primitive `DoubleArray`. The values are converted the same way
 * [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.api.convertTo] does, and the returned array is a copy — changing it doesn't affect the column.
 */
public fun DataColumn<Number>.toDoubleArray(): DoubleArray = convertToDouble().toList().toDoubleArray()

/**
 * Converts the values of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Number</code>][Number]`>` to Int and returns them
 * as a new primitive `IntArray`. The values are converted the same way
 * [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.api.convertTo] does, and the returned array is a copy — changing it doesn't affect the column.
 */
public fun DataColumn<Number>.toIntArray(): IntArray = convertToInt().toList().toIntArray()

/**
 * Converts the values of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Number</code>][Number]`>` to Long and returns them
 * as a new primitive `LongArray`. The values are converted the same way
 * [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.api.convertTo] does, and the returned array is a copy — changing it doesn't affect the column.
 */
public fun DataColumn<Number>.toLongArray(): LongArray = convertToLong().toList().toLongArray()

/**
 * Converts the values of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Number</code>][Number]`>` to Short and returns them
 * as a new primitive `ShortArray`. The values are converted the same way
 * [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.api.convertTo] does, and the returned array is a copy — changing it doesn't affect the column.
 */
public fun DataColumn<Number>.toShortArray(): ShortArray = convertTo<Short>().toList().toShortArray()

/**
 * Converts the values of this [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Number</code>][Number]`>` to Byte and returns them
 * as a new primitive `ByteArray`. The values are converted the same way
 * [<code>convertTo</code>][org.jetbrains.kotlinx.dataframe.api.convertTo] does, and the returned array is a copy — changing it doesn't affect the column.
 */
public fun DataColumn<Number>.toByteArray(): ByteArray = convertTo<Byte>().toList().toByteArray()

// endregion

/**
 * Casts this column to a [<code>ValueColumn</code>][ValueColumn].
 *
 * NOTE: This doesn't check whether the column is actually a [<code>ValueColumn</code>][ValueColumn]; it just casts it.
 * If the column is a [<code>ColumnGroup</code>][ColumnGroup] or a [<code>FrameColumn</code>][FrameColumn], a [<code>ClassCastException</code>][ClassCastException] is thrown.
 *
 * See also [<code>isValueColumn</code>][isValueColumn].
 */
public fun DataColumn<*>.asValueColumn(): ValueColumn<*> = this as ValueColumn<*>

/**
 * Casts this column to a [<code>ColumnGroup</code>][ColumnGroup].
 *
 * NOTE: This doesn't check whether the column is actually a [<code>ColumnGroup</code>][ColumnGroup]; it just casts it.
 * If the column is not a group, a [<code>ClassCastException</code>][ClassCastException] is thrown.
 *
 * See also [<code>isColumnGroup</code>][isColumnGroup], [<code>SingleColumn.asColumnGroup</code>][SingleColumn.asColumnGroup], [<code>DataFrame.asColumnGroup</code>][DataFrame.asColumnGroup].
 */
@JvmName("asColumnGroupUntyped")
public fun DataColumn<*>.asColumnGroup(): ColumnGroup<*> = this as ColumnGroup<*>

/**
 * Casts this column to a [<code>FrameColumn</code>][FrameColumn].
 *
 * NOTE: This doesn't check whether the column is actually a [<code>FrameColumn</code>][FrameColumn]; it just casts it.
 * If the column is not a frame column, a [<code>ClassCastException</code>][ClassCastException] is thrown.
 *
 * See also [<code>isFrameColumn</code>][isFrameColumn], [<code>DataColumn.asFrameColumn</code>][DataColumn.asFrameColumn].
 */
@JvmName("asFrameColumnUntyped")
public fun DataColumn<*>.asFrameColumn(): FrameColumn<*> = this as FrameColumn<*>

/**
 * Casts this column of [<code>DataFrame</code>][DataFrame]s to a [<code>FrameColumn</code>][FrameColumn].
 *
 * NOTE: This doesn't check whether the column is actually a [<code>FrameColumn</code>][FrameColumn]; it just casts it.
 *
 * See also [<code>AnyCol.asFrameColumn</code>][AnyCol.asFrameColumn].
 */
public fun <T> DataColumn<DataFrame<T>>.asFrameColumn(): FrameColumn<T> =
    (this as AnyCol).asAnyFrameColumn().castFrameColumn()

/**
 * Casts this column of [<code>DataRow</code>][DataRow]s to a [<code>ColumnGroup</code>][ColumnGroup].
 *
 * NOTE: This doesn't check whether the column is actually a [<code>ColumnGroup</code>][ColumnGroup]; it just casts it.
 *
 * See also [<code>AnyCol.asColumnGroup</code>][AnyCol.asColumnGroup], [<code>DataColumn.asDataFrame</code>][DataColumn.asDataFrame].
 */
@JvmName("asGroupedT")
public fun <T> DataColumn<DataRow<T>>.asColumnGroup(): ColumnGroup<T> = (this as AnyCol).asColumnGroup().cast()

/**
 * Casts this column of [<code>DataRow</code>][DataRow]s to a [<code>DataFrame</code>][DataFrame] — a view of the group's rows.
 * Same as [<code>DataColumn.asColumnGroup</code>][DataColumn.asColumnGroup].
 */
public fun <T> DataColumn<DataRow<T>>.asDataFrame(): DataFrame<T> = asColumnGroup()

// endregion

// region ColumnGroup

/**
 * Casts this [<code>ColumnGroup</code>][ColumnGroup] to a [<code>DataColumn</code>][DataColumn] of its [<code>DataRow</code>][DataRow]s.
 *
 * A [<code>ColumnGroup</code>][ColumnGroup] is both a column and a [<code>DataFrame</code>][DataFrame]; the same object is returned,
 * only its static type changes. No data is copied.
 *
 * See also [<code>FrameColumn.asDataColumn</code>][FrameColumn.asDataColumn].
 */
public fun <T> ColumnGroup<T>.asDataColumn(): DataColumn<DataRow<T>> = this as DataColumn<DataRow<T>>

/**
 * Returns this [<code>ColumnGroup</code>][ColumnGroup] typed as a [<code>DataFrame</code>][DataFrame]:
 * a [<code>ColumnGroup</code>][ColumnGroup] is already a [<code>DataFrame</code>][DataFrame], so the same object is returned and nothing is copied.
 */
public fun <T> ColumnGroup<T>.asDataFrame(): DataFrame<T> = this

// endregion

// region SingleColumn

/** ## As ColumnGroup
 *
 * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<`[<code>C</code>][C]`>>` from [<code>this</code>][this].
 * This is especially useful when you want to use [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] functions in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] but your column
 * type is not recognized as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * If you're not sure whether a column is recognized as [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or not, you can always call [<code>asColumnGroup</code>][asColumnGroup]
 * and it will return the same type if it is already a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * NOTE: This does not check whether the column is actually a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or not. It just casts it.
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`().`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]`().`[<code>firstCol</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
 *
 * @receiver The column reference to cast to a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<`[<code>C</code>][C]`>>`.
 * @param [C] The type of the (group) column.
 * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<`[<code>C</code>][C]`>>`. */
@Suppress("UNCHECKED_CAST")
public fun <C> SingleColumn<C>.asColumnGroup(): SingleColumn<DataRow<C>> = this as SingleColumn<DataRow<C>>

/** ## As ColumnGroup
 *
 * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`<`[<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<`[<code>C</code>][C]`>>` from [<code>this</code>][this].
 * This is especially useful when you want to use [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] functions in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] but your column
 * type is not recognized as a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * If you're not sure whether a column is recognized as [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or not, you can always call [<code>asColumnGroup</code>][asColumnGroup]
 * and it will return the same type if it is already a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * NOTE: This does not check whether the column is actually a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or not. It just casts it.
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>first</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`().`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.asColumnGroup]`().`[<code>firstCol</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`() }`
 *
 * @receiver The column reference to cast to a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<`[<code>C</code>][C]`>>`.
 * @param [C] The type of the (group) column.
 * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<`[<code>C</code>][C]`>>`. */
@JvmName("asColumnGroupDataRow")
public fun <C> SingleColumn<DataRow<C>>.asColumnGroup(): SingleColumn<DataRow<C>> = this

// endregion

// region FrameColumn

/**
 * Returns this [<code>FrameColumn</code>][FrameColumn] typed as a [<code>DataColumn</code>][DataColumn] of its (nullable) [<code>DataFrame</code>][DataFrame] values.
 * The same object is returned — no data is copied.
 *
 * See also [<code>ColumnGroup.asDataColumn</code>][ColumnGroup.asDataColumn].
 */
public fun <T> FrameColumn<T>.asDataColumn(): DataColumn<DataFrame<T>?> = this

/**
 * Always throws an [<code>IllegalArgumentException</code>][IllegalArgumentException]: by design, a [<code>ValueColumn</code>][ValueColumn] cannot contain
 * [<code>DataFrame</code>][DataFrame] values — such a column must be a [<code>FrameColumn</code>][FrameColumn].
 * Use [<code>asDataColumn</code>][asDataColumn] instead: it returns the same values typed as a plain [<code>DataColumn</code>][DataColumn].
 */
public fun <T> FrameColumn<T>.toValueColumn(): ValueColumn<DataFrame<T>?> =
    DataColumn.createValueColumn(name, toList(), type())

// endregion

// region ColumnSet

/**
 * Changes the static type argument of this [<code>ColumnSet</code>][ColumnSet] to [<code>Number</code>][Number]
 * without any checks or data changes.
 *
 * NOTE: This is a fully unchecked cast: no checks are performed, and the underlying columns
 * stay exactly as they are — neither their runtime types nor their values change.
 * When the columns actually contain values of another type, the wrong static type simply propagates.
 *
 * See also [<code>DataColumn.asNumbers</code>][DataColumn.asNumbers], which checks [<code>isNumber</code>][isNumber] first, and [<code>ColumnSet.asComparable</code>][ColumnSet.asComparable].
 *
 */
@JvmName("asNumbersAny")
public fun ColumnSet<Any>.asNumbers(): ColumnSet<Number> = this as ColumnSet<Number>

/**
 * Changes the static type argument of this [<code>SingleColumn</code>][SingleColumn] to [<code>Number</code>][Number]
 * without any checks or data changes.
 *
 * NOTE: This is a fully unchecked cast: no checks are performed, and the underlying columns
 * stay exactly as they are — neither their runtime types nor their values change.
 * When the columns actually contain values of another type, the wrong static type simply propagates.
 *
 * See also [<code>DataColumn.asNumbers</code>][DataColumn.asNumbers], which checks [<code>isNumber</code>][isNumber] first, and [<code>SingleColumn.asComparable</code>][SingleColumn.asComparable].
 *
 */
@JvmName("asNumbersAny")
public fun SingleColumn<Any>.asNumbers(): SingleColumn<Number> = this as SingleColumn<Number>

/**
 * Changes the static type argument of this [<code>ColumnSet</code>][ColumnSet] to [<code>Number?</code>][Number]
 * without any checks or data changes.
 *
 * NOTE: This is a fully unchecked cast: no checks are performed, and the underlying columns
 * stay exactly as they are — neither their runtime types nor their values change.
 * When the columns actually contain values of another type, the wrong static type simply propagates.
 *
 * See also [<code>DataColumn.asNumbers</code>][DataColumn.asNumbers], which checks [<code>isNumber</code>][isNumber] first, and [<code>ColumnSet.asComparable</code>][ColumnSet.asComparable].
 *
 */
@JvmName("asNumbersAnyNullable")
public fun ColumnSet<Any?>.asNumbers(): ColumnSet<Number?> = this as ColumnSet<Number?>

/**
 * Changes the static type argument of this [<code>SingleColumn</code>][SingleColumn] to [<code>Number?</code>][Number]
 * without any checks or data changes.
 *
 * NOTE: This is a fully unchecked cast: no checks are performed, and the underlying columns
 * stay exactly as they are — neither their runtime types nor their values change.
 * When the columns actually contain values of another type, the wrong static type simply propagates.
 *
 * See also [<code>DataColumn.asNumbers</code>][DataColumn.asNumbers], which checks [<code>isNumber</code>][isNumber] first, and [<code>SingleColumn.asComparable</code>][SingleColumn.asComparable].
 *
 */
@JvmName("asNumbersAnyNullable")
public fun SingleColumn<Any?>.asNumbers(): SingleColumn<Number?> = this as SingleColumn<Number?>

/**
 * Changes the static type argument of this [<code>ColumnSet</code>][ColumnSet] to [<code>Comparable</code>][Comparable]`<`[<code>T</code>][T]`>`
 * without any checks or data changes.
 *
 * NOTE: This is a fully unchecked cast: no checks are performed, and the underlying columns
 * stay exactly as they are — neither their runtime types nor their values change.
 * When the columns actually contain values of another type, the wrong static type simply propagates.
 *
 * See also [<code>DataColumn.asComparable</code>][DataColumn.asComparable], which checks [<code>valuesAreComparable</code>][valuesAreComparable] first, and [<code>ColumnSet.asNumbers</code>][ColumnSet.asNumbers].
 *
 */
public fun <T> ColumnSet<T>.asComparable(): ColumnSet<Comparable<T>> = this as ColumnSet<Comparable<T>>

/**
 * Changes the static type argument of this [<code>SingleColumn</code>][SingleColumn] to [<code>Comparable</code>][Comparable]`<`[<code>T</code>][T]`>`
 * without any checks or data changes.
 *
 * NOTE: This is a fully unchecked cast: no checks are performed, and the underlying columns
 * stay exactly as they are — neither their runtime types nor their values change.
 * When the columns actually contain values of another type, the wrong static type simply propagates.
 *
 * See also [<code>DataColumn.asComparable</code>][DataColumn.asComparable], which checks [<code>valuesAreComparable</code>][valuesAreComparable] first, and [<code>SingleColumn.asNumbers</code>][SingleColumn.asNumbers].
 *
 */
public fun <T> SingleColumn<T>.asComparable(): SingleColumn<Comparable<T>> = this as SingleColumn<Comparable<T>>

// endregion

// region Iterable

/**
 * Creates a new [<code>FrameColumn</code>][FrameColumn] named [<code>name</code>][name] with this iterable's [<code>DataFrame</code>][DataFrame]s as its values.
 *
 * See also [<code>Iterable.toValueColumn</code>][Iterable.toValueColumn], [<code>Iterable.toColumnGroup</code>][Iterable.toColumnGroup].
 */
public fun <T> Iterable<DataFrame<T>>.toFrameColumn(name: String = ""): FrameColumn<T> =
    DataColumn.createFrameColumn(name, asList()).forceResolve()

/**
 * Creates a new [<code>ValueColumn</code>][ValueColumn] named [<code>name</code>][name] with this iterable's values.
 *
 * The [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of the column is taken from the reified [T];
 * the values themselves are not checked to be of type [T].
 *
 * See also [<code>Array.toValueColumn</code>][Array.toValueColumn], [<code>Iterable.toColumn</code>][Iterable.toColumn].
 */
public inline fun <reified T> Iterable<T>.toValueColumn(name: String = ""): ValueColumn<T> =
    DataColumn.createValueColumn(name, asList()).forceResolve()

/**
 * Creates a new [<code>ValueColumn</code>][ValueColumn] with the name of [<code>column</code>][column] and this iterable's values.
 *
 * The [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of the column is taken from the reified [T];
 * the values themselves are not checked to be of type [T].
 *
 * See also [<code>Iterable.toValueColumn</code>][Iterable.toValueColumn].
 */
public inline fun <reified T> Iterable<T>.toValueColumn(column: ColumnAccessor<T>): ValueColumn<T> =
    toValueColumn(column.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <reified T> Iterable<T>.toValueColumn(column: KProperty<T>): ValueColumn<T> =
    toValueColumn(column.columnName)

/**
 * Indicates how [<code>DataColumn.type</code>][DataColumn.type] should be calculated.
 *
 * Used in [<code>add</code>][add], [<code>insert</code>][insert], [<code>convert</code>][convert], [<code>map</code>][map], [<code>merge</code>][merge], [<code>split</code>][split] and other [<code>DataFrame</code>][DataFrame] operations
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
}

/**
 * Indicates how [<code>DataColumn.hasNulls</code>][DataColumn.hasNulls] (or, more accurately, DataColumn.type.isMarkedNullable) should be initialized from
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
 * Thrown by [<code>NullabilityOptions.applyNullability</code>][NullabilityOptions.applyNullability] in the [<code>NullabilityOptions.Checking</code>][NullabilityOptions.Checking] mode
 * when the data contains `null` values although the schema expects the column to be not nullable.
 */
public class NullabilityException : Exception()

/**
 * @return whether the column should be marked nullable for the current [<code>NullabilityOptions</code>][NullabilityOptions] value
 * with actual [<code>data</code>][data] and [<code>expectedNulls</code>][expectedNulls] per some schema/signature.
 * @throws [NullabilityException] for [<code>NullabilityOptions.Checking</code>][NullabilityOptions.Checking] if [<code>expectedNulls</code>][expectedNulls] is false and [<code>data</code>][data] contains nulls.
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
 * Creates a new [<code>DataColumn</code>][DataColumn] named [<code>name</code>][name] with this iterable's values.
 *
 * By default ([<code>Infer.Nulls</code>][Infer.Nulls]), the [<code>type</code>][DataColumn.type] of the column is taken from the reified [<code>T</code>][T],
 * with its nullability computed by checking the values for `null`.
 * With [<code>Infer.None</code>][Infer.None], the nullability is taken from [<code>T</code>][T] without checking the values.
 * With [<code>Infer.Type</code>][Infer.Type], both the type and the nullability are inferred from the values themselves.
 *
 * For more information: [See `toColumn` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#tocolumn)
 *
 * For example:
 * ```kotlin
 * listOf("Alice", "Bob").toColumn("name")
 * ```
 *
 * @param [name] The name of the new column.
 * @param [infer] An [<code>enum</code>][Infer] that indicates how [<code>type</code>][DataColumn.type] should be calculated.
 * Either [<code>Infer.None</code>][Infer.None], [<code>Infer.Nulls</code>][Infer.Nulls], or [<code>Infer.Type</code>][Infer.Type].
 * @return A new [<code>DataColumn</code>][DataColumn] with this iterable's values.
 */
public inline fun <reified T> Iterable<T>.toColumn(name: String = "", infer: Infer = Infer.Nulls): DataColumn<T> =
    if (infer == Infer.Type) {
        DataColumn.createByInference(name, asList())
    } else {
        DataColumn.createByType(name, asList(), typeOf<T>(), infer)
    }.forceResolve()

/**
 * Creates a new [<code>DataColumn</code>][DataColumn] named [<code>name</code>][name] with this iterable's values, statically typed as [<code>T</code>][T].
 *
 * The values are cast to [<code>T</code>][T] unchecked — neither the values nor the nulls among them are checked.
 * Useful when the type is known from the context.
 *
 * For more information: [See `toColumnOf` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#tocolumnof)
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
 * Creates a new [<code>DataColumn</code>][DataColumn] with the name of [<code>ref</code>][ref] and this iterable's values.
 *
 * The [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of the column is taken from the reified [T];
 * the values themselves are not checked to be of type [T].
 *
 * For more information: [See `toColumn` on the documentation website.](https://kotlin.github.io/dataframe/createcolumn.html#tocolumn)
 *
 * See also [<code>Iterable.toColumn</code>][Iterable.toColumn].
 */
public inline fun <reified T> Iterable<T>.toColumn(ref: ColumnReference<T>): DataColumn<T> =
    DataColumn.createByType(ref.name(), asList()).forceResolve()

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <reified T> Iterable<T>.toColumn(property: KProperty<T>): DataColumn<T> =
    DataColumn.createByType(property.columnName, asList()).forceResolve()

/**
 * Creates a [<code>ColumnPath</code>][ColumnPath] with this iterable's names as its segments.
 *
 * See also [<code>Array.toPath</code>][Array.toPath].
 */
public fun Iterable<String>.toPath(): ColumnPath = ColumnPath(asList())

/**
 * Creates a new [<code>ColumnGroup</code>][ColumnGroup] named [<code>name</code>][name] that nests this iterable's columns.
 *
 * See also [<code>DataFrame.asColumnGroup</code>][DataFrame.asColumnGroup], [<code>Iterable.toColumnGroupOf</code>][Iterable.toColumnGroupOf].
 */
public fun Iterable<BaseColumn<*>>.toColumnGroup(name: String): ColumnGroup<*> = dataFrameOf(this).asColumnGroup(name)

/**
 * Creates a new [<code>ColumnGroup</code>][ColumnGroup] with the name of [<code>column</code>][column] that nests this iterable's columns,
 * statically typed as [<code>T</code>][T] without a runtime check.
 *
 * See also [<code>Iterable.toColumnGroup</code>][Iterable.toColumnGroup].
 */
public fun <T> Iterable<BaseColumn<*>>.toColumnGroup(column: ColumnGroupAccessor<T>): ColumnGroup<T> =
    dataFrameOf(this).cast<T>().asColumnGroup(column)

/**
 * Creates a new [<code>ColumnGroup</code>][ColumnGroup] named [<code>name</code>][name] that nests this iterable's columns,
 * statically typed as [<code>T</code>][T] without a runtime check.
 *
 * See also [<code>Iterable.toColumnGroup</code>][Iterable.toColumnGroup].
 */
public fun <T> Iterable<BaseColumn<*>>.toColumnGroupOf(name: String): ColumnGroup<T> = toColumnGroup(name).cast()

// endregion

// region DataFrame

/**
 * Returns this [<code>DataFrame</code>][DataFrame] as a [<code>Map</code>][Map] that maps each column name to a [<code>List</code>][List] of that column's values.
 *
 * The value lists are new copies — changing them doesn't affect this DataFrame.
 *
 * See also [<code>DataRow.toMap</code>][DataRow.toMap] for a single row, [<code>Map.toDataFrame</code>][Map.toDataFrame] for the reverse direction.
 */
public fun DataFrame<*>.toMap(): Map<String, List<Any?>> =
    columns().associateBy({ it.name }, { it.toList().toMutableList() })

/**
 * Wraps this [<code>DataFrame</code>][DataFrame] into a [<code>ColumnGroup</code>][ColumnGroup] — a single group column named [<code>name</code>][name]
 * that contains all of this DataFrame's columns.
 *
 * If `this` is already a [<code>ColumnGroup</code>][ColumnGroup], only the name changes (like in [<code>rename</code>][rename]);
 * otherwise a new group column is created. The nested columns are not copied.
 *
 * See also [<code>Iterable.toColumnGroup</code>][Iterable.toColumnGroup], [<code>BaseColumn.toDataFrame</code>][BaseColumn.toDataFrame], and the overload taking
 * a [<code>ColumnGroupAccessor</code>][ColumnGroupAccessor].
 */
public fun <T> DataFrame<T>.asColumnGroup(name: String = ""): ColumnGroup<T> =
    when (this) {
        is ColumnGroup<T> -> rename(name)
        else -> DataColumn.createColumnGroup(name, this)
    }

/**
 * Wraps this [<code>DataFrame</code>][DataFrame] into a [<code>ColumnGroup</code>][ColumnGroup] named as [<code>column</code>][column],
 * equivalent to [<code>asColumnGroup</code>][asColumnGroup] with the [<code>name</code>][ColumnGroupAccessor.name] of [<code>column</code>][column].
 */
public fun <T> DataFrame<T>.asColumnGroup(column: ColumnGroupAccessor<T>): ColumnGroup<T> = asColumnGroup(column.name)

// region as GroupedDataFrame

/**
 * Reinterprets this [<code>DataFrame</code>][DataFrame] as a [<code>GroupBy</code>][GroupBy] by treating the [<code>FrameColumn</code>][FrameColumn] named [<code>groupedColumnName</code>][groupedColumnName]
 * as the groups. The remaining columns of this DataFrame become the key columns of the resulting [<code>GroupBy</code>][GroupBy],
 * and the groups column is placed last in it.
 *
 * This DataFrame must contain a frame column with this name.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * For example:
 * ```kotlin
 * df.asGroupBy("data") // convert dataframe to GroupBy by interpreting the 'data' column as groups
 * ```
 *
 * @throws [IllegalArgumentException] if this DataFrame has no column named [<code>groupedColumnName</code>][groupedColumnName].
 * @throws [ClassCastException] if that column is not a [<code>FrameColumn</code>][FrameColumn].
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
 * Reinterprets this [<code>DataFrame</code>][DataFrame] as a [<code>GroupBy</code>][GroupBy] by treating its only [<code>FrameColumn</code>][FrameColumn] as the groups.
 * The remaining columns of this DataFrame become the key columns of the resulting [<code>GroupBy</code>][GroupBy],
 * and the groups column is placed last in it.
 *
 * This DataFrame must contain exactly one frame column.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
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
 * Reinterprets this [<code>DataFrame</code>][DataFrame] as a [<code>GroupBy</code>][GroupBy] by treating the column selected by [<code>selector</code>][selector]
 * as the groups. The selected column must be a [<code>FrameColumn</code>][FrameColumn]. The remaining columns of this
 * DataFrame become the key columns of the resulting [<code>GroupBy</code>][GroupBy], and the groups column is placed last in it —
 * this DataFrame itself stays unchanged.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
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
 * Returns a single-row [<code>DataFrame</code>][DataFrame] containing just this row.
 *
 * See also [<code>Map.toDataRow</code>][Map.toDataRow], which builds a [<code>DataRow</code>][DataRow] from a [<code>Map</code>][Map].
 */
public fun <T> DataRow<T>.toDataFrame(): DataFrame<T> = owner[index..index]

/**
 * Returns this row as a [<code>Map</code>][Map] that maps each column name to this row's value in that column.
 *
 * See also [<code>DataFrame.toMap</code>][DataFrame.toMap] for all rows at once, [<code>Map.toDataRow</code>][Map.toDataRow] for the reverse direction.
 */
public fun DataRow<*>.toMap(): Map<String, Any?> = df().columns().associate { it.name() to it[index] }

/**
 * Creates a single [<code>DataRow</code>][DataRow] from this map: each key becomes a column name,
 * and its value becomes the value of that column in the row.
 *
 * See also [<code>DataRow.toMap</code>][DataRow.toMap] for the reverse direction.
 */
public fun Map<String, Any?>.toDataRow(): DataRow<*> {
    val df = mapValues { listOf(it.value) }.toDataFrame()
    return DataRowImpl(0, df)
}

// endregion

// region Array

/**
 * Creates a new [<code>ValueColumn</code>][ValueColumn] named [<code>name</code>][name] with this array's values.
 *
 * The [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of the column is taken from the reified [T];
 * the values themselves are not checked to be of type [T].
 *
 * See also [<code>Iterable.toValueColumn</code>][Iterable.toValueColumn].
 */
public inline fun <reified T> Array<T>.toValueColumn(name: String): ValueColumn<T> =
    DataColumn.createValueColumn(name, this.asList(), typeOf<T>())

/**
 * Creates a [<code>ColumnPath</code>][ColumnPath] with this array's names as its segments.
 *
 * See also [<code>Iterable.toPath</code>][Iterable.toPath].
 */
public fun Array<out String>.toPath(): ColumnPath = ColumnPath(this.asList())

// endregion
