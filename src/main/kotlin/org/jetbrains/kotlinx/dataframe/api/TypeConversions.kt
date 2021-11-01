package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.frameColumn
import org.jetbrains.kotlinx.dataframe.impl.GroupedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.ManyImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.index
import kotlin.reflect.KProperty

// region String

public fun String.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

public fun <T> String.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

// endregion

// region ColumnPath

public fun <T> ColumnPath.toColumnOf(): ColumnAccessor<T> = ColumnAccessorImpl(this)

public fun ColumnPath.toColumnAccessor(): ColumnAccessor<Any?> = ColumnAccessorImpl(this)

public fun ColumnPath.toGroupColumnDef(): ColumnAccessor<AnyRow> = ColumnAccessorImpl(this)

// endregion

// region KProperty

public fun <T> KProperty<T>.toColumnAccessor(): ColumnAccessor<T> = ColumnAccessorImpl<T>(name)

// endregion

// region DataColumn

public fun AnyBaseColumn.toDataFrame(): AnyFrame = dataFrameOf(listOf(this))

@JvmName("asNumberAny?")
public fun DataColumn<Any?>.asNumbers(): ValueColumn<Number?> {
    require(isNumber())
    return this as ValueColumn<Number?>
}

@JvmName("asNumberAny")
public fun DataColumn<Any>.asNumbers(): ValueColumn<Number> {
    require(isNumber())
    return this as ValueColumn<Number>
}

public fun <T> FrameColumn<T>.toDefinition(): ColumnAccessor<DataFrame<T>> = frameColumn<T>(name)
public fun <T> ColumnGroup<T>.toDefinition(): ColumnAccessor<DataRow<T>> = columnGroup<T>(name)
public fun <T> ValueColumn<T>.toDefinition(): ColumnAccessor<T> = column<T>(name)

public fun <T> DataColumn<T>.asComparable(): DataColumn<Comparable<T>> {
    require(isComparable())
    return this as DataColumn<Comparable<T>>
}

public fun <T> DataColumn<T?>.asNotNullable(): DataColumn<T> {
    require(!hasNulls())
    return this as DataColumn<T>
}

public fun <T> BaseColumn<T>.toMany(): Many<T> = values().toMany()

// endregion

// region ColumnGroup

public fun <T> ColumnGroup<T>.asDataColumn(): DataColumn<DataRow<T>> = this as DataColumn<DataRow<T>>

public fun <T> ColumnGroup<T>.asDataFrame(): DataFrame<T> = this

// endregion

// region FrameColumn

public fun <T> FrameColumn<T>.asDataColumn(): DataColumn<DataFrame<T>?> = this

// endregion

// region Columns

@JvmName("asNumbersAny")
public fun ColumnSet<Any>.asNumbers(): ColumnSet<Number> = this as ColumnSet<Number>

@JvmName("asNumbersAny?")
public fun ColumnSet<Any?>.asNumbers(): ColumnSet<Number?> = this as ColumnSet<Number?>

public fun <T> ColumnSet<T>.asComparable(): ColumnSet<Comparable<T>> = this as ColumnSet<Comparable<T>>

// endregion

// region Iterable

public fun <T> Iterable<DataFrame<T>?>.toFrameColumn(name: String = ""): FrameColumn<T> =
    DataColumn.createFrameColumn(name, asList())

public inline fun <reified T> Iterable<T>.toValueColumn(name: String = ""): ValueColumn<T> = DataColumn.createValueColumn(name, asList())

public inline fun <reified T> Iterable<T>.toColumn(
    name: String = "",
    inferNulls: Boolean? = null,
    inferType: Boolean = false
): DataColumn<T> =
    if (inferType) DataColumn.createWithTypeInference(name, asList(), nullable = inferNulls?.let { if (it) null else getType<T>().isMarkedNullable })
    else DataColumn.create(name, asList(), getType<T>(), checkForNulls = inferNulls == true)

public inline fun <reified T> Iterable<*>.toColumnOf(name: String = ""): DataColumn<T> =
    DataColumn.create(name, asList() as List<T>, getType<T>())

public inline fun <reified T> Iterable<T>.toColumn(ref: ColumnReference<T>): DataColumn<T> =
    DataColumn.create(ref.name(), asList())

public fun <T> Iterable<T>.toMany(): Many<T> = when (this) {
    is Many<T> -> this
    is List<T> -> ManyImpl(this)
    else -> ManyImpl(toList())
}

public fun Iterable<String>.toPath(): ColumnPath = ColumnPath(asList())

// endregion

// region Sequence

public fun <T> Sequence<T>.toMany(): Many<T> = toList().toMany()

// endregion

// region DataFrame

public fun AnyFrame.toMap(): Map<String, List<Any?>> = columns().associateBy({ it.name }, { it.toList() })

// region as GroupedDataFrame

public fun <T> DataFrame<T>.asGroupedDataFrame(groupedColumnName: String): GroupedDataFrame<T, T> =
    GroupedDataFrameImpl(this, frameColumn(groupedColumnName).typedFrames()) { none() }

public fun <T, G> DataFrame<T>.asGroupedDataFrame(groupedColumn: ColumnReference<DataFrame<G>?>): GroupedDataFrame<T, G> =
    GroupedDataFrameImpl(this, frameColumn(groupedColumn.name()).typedFrames()) { none() }

public fun <T> DataFrame<T>.asGroupedDataFrame(): GroupedDataFrame<T, T> {
    val groupCol = columns().single { it.isFrameColumn() }.asFrameColumn().typedFrames<T>()
    return asGroupedDataFrame { groupCol }
}

public fun <T, G> DataFrame<T>.asGroupedDataFrame(selector: ColumnSelector<T, DataFrame<G>?>): GroupedDataFrame<T, G> {
    val column = getColumn(selector).asFrameColumn()
    return GroupedDataFrameImpl(this, column) { none() }
}

// endregion

// endregion

// region DataRow

public fun <T> DataRow<T>.toDataFrame(): DataFrame<T> = owner[index..index]

// endregion

// region Array

public inline fun <reified T> Array<T>.toValueColumn(name: String): ValueColumn<T> =
    DataColumn.createValueColumn(name, this.asList(), getType<T>())

public fun Array<out String>.toPath(): ColumnPath = ColumnPath(this.asList())

// endregion

// region typed

public fun <T> AnyFrame.typed(): DataFrame<T> = this as DataFrame<T>

public fun <T> AnyRow.typed(): DataRow<T> = this as DataRow<T>

public fun <T> AnyCol.typed(): DataColumn<T> = this as DataColumn<T>

public fun <T> ValueColumn<*>.typed(): ValueColumn<T> = this as ValueColumn<T>

public fun <T> FrameColumn<*>.typedFrames(): FrameColumn<T> = this as FrameColumn<T>

public fun <T> ColumnGroup<*>.typed(): ColumnGroup<T> = this as ColumnGroup<T>

public fun <T> ColumnWithPath<*>.typed(): ColumnWithPath<T> = this as ColumnWithPath<T>

// endregion
