package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
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
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region String

public fun String.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

public fun <T> String.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

// endregion

// region ColumnPath

public fun <T> ColumnPath.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

public fun ColumnPath.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

public fun ColumnPath.toColumnGroupAccessor(): ColumnAccessor<AnyRow> = ColumnAccessorImpl(this)
public fun ColumnPath.toFrameColumnAccessor(): ColumnAccessor<AnyFrame> = ColumnAccessorImpl(this)

// endregion

// region ColumnReference

public fun <T> ColumnReference<T>.toColumnAccessor(): ColumnAccessor<T> = when (this) {
    is ColumnAccessor<T> -> this
    else -> ColumnAccessorImpl(path())
}

// endregion

// region KProperty

public fun <T> KProperty<T>.toColumnAccessor(): ColumnAccessor<T> = ColumnAccessorImpl<T>(columnName)

// endregion

// region DataColumn

public fun AnyBaseCol.toDataFrame(): AnyFrame = dataFrameOf(listOf(this))

@JvmName("asNumberAnyNullable")
public fun DataColumn<Any?>.asNumbers(): ValueColumn<Number?> {
    require(isNumber())
    return this.asValues()
}

@JvmName("asNumberAny")
public fun DataColumn<Any>.asNumbers(): ValueColumn<Number> {
    require(isNumber())
    return this as ValueColumn<Number>
}

public fun <T> DataColumn<T>.asComparable(): DataColumn<Comparable<T>> {
    require(isComparable())
    return this as DataColumn<Comparable<T>>
}

public fun <T> ColumnReference<T?>.castToNotNullable(): ColumnReference<T> = cast()

public fun <T> DataColumn<T?>.castToNotNullable(): DataColumn<T> {
    require(!hasNulls()) { "Column `$name` has nulls" }
    return this as DataColumn<T>
}

public fun <T> DataColumn<T>.castToNullable(): DataColumn<T?> = cast()

public fun <T> ColumnReference<T>.castToNullable(): ColumnReference<T?> = cast()

// region to array

public inline fun <reified T> DataColumn<T>.toTypedArray(): Array<T> = toList().toTypedArray()

public fun DataColumn<Number>.toFloatArray(): FloatArray = convertToFloat().toList().toFloatArray()

public fun DataColumn<Number>.toDoubleArray(): DoubleArray = convertToDouble().toList().toDoubleArray()

public fun DataColumn<Number>.toIntArray(): IntArray = convertToInt().toList().toIntArray()

public fun DataColumn<Number>.toLongArray(): LongArray = convertToLong().toList().toLongArray()

public fun DataColumn<Number>.toShortArray(): ShortArray = convertTo<Short>().toList().toShortArray()

public fun DataColumn<Number>.toByteArray(): ByteArray = convertTo<Byte>().toList().toByteArray()

// endregion

public fun AnyCol.asColumnGroup(): ColumnGroup<*> = this as ColumnGroup<*>

public fun <T> DataColumn<DataFrame<T>>.asFrameColumn(): FrameColumn<T> = (this as AnyCol).asAnyFrameColumn().castFrameColumn()

@JvmName("asGroupedT")
public fun <T> DataColumn<DataRow<T>>.asColumnGroup(): ColumnGroup<T> = (this as AnyCol).asColumnGroup().cast()

public fun <T> DataColumn<DataRow<T>>.asDataFrame(): DataFrame<T> = asColumnGroup()

// endregion

// region ColumnGroup

public fun <T> ColumnGroup<T>.asDataColumn(): DataColumn<DataRow<T>> = this as DataColumn<DataRow<T>>

public fun <T> ColumnGroup<T>.asDataFrame(): DataFrame<T> = this

// endregion

// region FrameColumn

public fun <T> FrameColumn<T>.asDataColumn(): DataColumn<DataFrame<T>?> = this

public fun <T> FrameColumn<T>.toValueColumn(): ValueColumn<DataFrame<T>?> =
    DataColumn.createValueColumn(name, toList(), type())

// endregion

// region ColumnSet

@JvmName("asNumbersAny")
public fun ColumnSet<Any>.asNumbers(): ColumnSet<Number> = this as ColumnSet<Number>

@JvmName("asNumbersAnyNullable")
public fun ColumnSet<Any?>.asNumbers(): ColumnSet<Number?> = this as ColumnSet<Number?>

public fun <T> ColumnSet<T>.asComparable(): ColumnSet<Comparable<T>> = this as ColumnSet<Comparable<T>>

// endregion

// region Iterable

public fun <T> Iterable<DataFrame<T>>.toFrameColumn(name: String = ""): FrameColumn<T> =
    DataColumn.createFrameColumn(name, asList()).forceResolve()

public inline fun <reified T> Iterable<T>.toValueColumn(name: String = ""): ValueColumn<T> =
    DataColumn.createValueColumn(name, asList()).forceResolve()

public inline fun <reified T> Iterable<T>.toValueColumn(column: ColumnAccessor<T>): ValueColumn<T> =
    toValueColumn(column.name())

public inline fun <reified T> Iterable<T>.toValueColumn(column: KProperty<T>): ValueColumn<T> =
    toValueColumn(column.columnName)

/**
 * Indicates how [DataColumn.type] should be calculated.
 *
 * Used in [add], [insert], [convert], [map], [merge], [split] and other [DataFrame] operations
 */
public enum class Infer {

    /**
     * Use reified type argument of an inline [DataFrame] operation as [DataColumn.type].
     */
    None,

    /**
     * Use reified type argument of an inline [DataFrame] operation as [DataColumn.type], but compute [DataColumn.hasNulls] by checking column [DataColumn.values] for an actual presence of *null* values.
     */
    Nulls,

    /**
     * Infer [DataColumn.type] and [DataColumn.hasNulls] from actual [DataColumn.values] using optionally provided base type as an upper bound.
     */
    Type
}

/**
 * Indicates how [DataColumn.hasNulls] (or, more accurately, DataColumn.type.isMarkedNullable) should be initialized from
 * expected schema and actual data when reading schema-defined data formats.
 */
public enum class NullabilityOptions {
    /**
     * Use only actual data, set [DataColumn.hasNulls] to true if and only if there are null values in the column.
     * On empty dataset use False.
     */
    Infer,

    /**
     * Set [DataColumn.hasNulls] to expected value. Throw exception if column should be not nullable but there are null values.
     */
    Checking,

    /**
     * Set [DataColumn.hasNulls] to expected value by default. Change False to True if column should be not nullable but there are null values.
     */
    Widening
}

public class NullabilityException() : Exception()

/**
 * @return if column should be marked nullable for current [NullabilityOptions] value with actual [data] and [expectedNulls] per some schema/signature.
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

public inline fun <reified T> Iterable<T>.toColumn(
    name: String = "",
    infer: Infer = Infer.None
): DataColumn<T> =
    (
        if (infer == Infer.Type) DataColumn.createWithTypeInference(name, asList())
        else DataColumn.create(name, asList(), typeOf<T>(), infer)
        ).forceResolve()

public inline fun <reified T> Iterable<*>.toColumnOf(name: String = ""): DataColumn<T> =
    DataColumn.create(name, asList() as List<T>, typeOf<T>()).forceResolve()

public inline fun <reified T> Iterable<T>.toColumn(ref: ColumnReference<T>): DataColumn<T> =
    DataColumn.create(ref.name(), asList()).forceResolve()

public inline fun <reified T> Iterable<T>.toColumn(property: KProperty<T>): DataColumn<T> =
    DataColumn.create(property.columnName, asList()).forceResolve()

public fun Iterable<String>.toPath(): ColumnPath = ColumnPath(asList())

public fun Iterable<AnyBaseCol>.toColumnGroup(name: String): ColumnGroup<*> = dataFrameOf(this).asColumnGroup(name)
public fun <T> Iterable<AnyBaseCol>.toColumnGroup(column: ColumnGroupAccessor<T>): ColumnGroup<T> = dataFrameOf(this).cast<T>().asColumnGroup(column)

public fun <T> Iterable<AnyBaseCol>.toColumnGroupOf(name: String): ColumnGroup<T> = toColumnGroup(name).cast()

// endregion

// region DataFrame

public fun AnyFrame.toMap(): Map<String, List<Any?>> = columns().associateBy({ it.name }, { it.toList() })

public fun <T> DataFrame<T>.asColumnGroup(name: String = ""): ColumnGroup<T> = when (this) {
    is ColumnGroup<T> -> rename(name)
    else -> DataColumn.createColumnGroup(name, this)
}

public fun <T> DataFrame<T>.asColumnGroup(column: ColumnGroupAccessor<T>): ColumnGroup<T> = asColumnGroup(column.name)

// region as GroupedDataFrame

public fun <T> DataFrame<T>.asGroupBy(groupedColumnName: String): GroupBy<T, T> =
    GroupByImpl(this, getFrameColumn(groupedColumnName).castFrameColumn()) { none() }

public fun <T, G> DataFrame<T>.asGroupBy(groupedColumn: ColumnReference<DataFrame<G>>): GroupBy<T, G> =
    GroupByImpl(this, getFrameColumn(groupedColumn.name()).castFrameColumn()) { none() }

public fun <T> DataFrame<T>.asGroupBy(): GroupBy<T, T> {
    val groupCol = columns().single { it.isFrameColumn() }.asAnyFrameColumn().castFrameColumn<T>()
    return asGroupBy { groupCol }
}

public fun <T, G> DataFrame<T>.asGroupBy(selector: ColumnSelector<T, DataFrame<G>>): GroupBy<T, G> {
    val column = getColumn(selector).asFrameColumn()
    return GroupByImpl(this, column) { none() }
}

// endregion

// endregion

// region DataRow

public fun <T> DataRow<T>.toDataFrame(): DataFrame<T> = owner[index..index]

public fun AnyRow.toMap(): Map<String, Any?> = df().columns().map { it.name() to it[index] }.toMap()

// endregion

// region Array

public inline fun <reified T> Array<T>.toValueColumn(name: String): ValueColumn<T> =
    DataColumn.createValueColumn(name, this.asList(), typeOf<T>())

public fun Array<out String>.toPath(): ColumnPath = ColumnPath(this.asList())

// endregion
