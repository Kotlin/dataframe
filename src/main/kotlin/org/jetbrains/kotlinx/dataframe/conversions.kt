package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.GroupedDataFrame
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.frameColumn
import org.jetbrains.kotlinx.dataframe.api.isComparable
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.Columns
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.GroupedDataFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.ManyImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.asTable
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.columns.isTable
import org.jetbrains.kotlinx.dataframe.impl.columns.typed
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.owner
import kotlin.reflect.KProperty
import kotlin.reflect.full.withNullability

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

public fun AnyColumn.toDataFrame(): AnyFrame = dataFrameOf(listOf(this))

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

public fun AnyColumn.asFrame(): AnyFrame = when (this) {
    is ColumnGroup<*> -> df
    is ColumnWithPath<*> -> data.asFrame()
    else -> error("Can not extract DataFrame from ${javaClass.kotlin}")
}

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

// region Columns

@JvmName("asNumbersAny")
public fun Columns<Any>.asNumbers(): Columns<Number> = this as Columns<Number>

@JvmName("asNumbersAny?")
public fun Columns<Any?>.asNumbers(): Columns<Number?> = this as Columns<Number?>

public fun <T> Columns<T>.asComparable(): Columns<Comparable<T>> = this as Columns<Comparable<T>>

// endregion

// region Iterable

public fun Iterable<AnyFrame?>.toColumn(): FrameColumn<Any?> = columnOf(this)

public fun Iterable<AnyFrame?>.toColumn(name: String): FrameColumn<Any?> = DataColumn.create(name, toList())

public fun <T> Iterable<DataFrame<T>?>.toFrameColumn(name: String): FrameColumn<T> =
    DataColumn.create(name, asList())

public inline fun <reified T> Iterable<T>.toColumn(name: String = ""): ValueColumn<T> =
    asList().let { DataColumn.create(name, it, getType<T>().withNullability(it.any { it == null })) }

public fun Iterable<Any?>.toColumnGuessType(name: String = ""): AnyCol =
    guessColumnType(name, asList())

public inline fun <reified T> Iterable<T>.toColumn(ref: ColumnReference<T>): ValueColumn<T> =
    toColumn(ref.name())

public fun <T> Iterable<T>.toMany(): Many<T> = when (this) {
    is Many<T> -> this
    is List<T> -> ManyImpl(this)
    else -> ManyImpl(toList())
}

// endregion

// region Sequence

public fun <T> Sequence<T>.toMany(): Many<T> = toList().toMany()

// endregion

// region DataFrame

public fun <T> AnyFrame.typed(): DataFrame<T> = this as DataFrame<T>

public fun AnyFrame.toMap(): Map<String, List<Any?>> = columns().associateBy({ it.name }, { it.toList() })

// region as GroupedDataFrame

public fun <T> DataFrame<T>.asGroupedDataFrame(groupedColumnName: String): GroupedDataFrame<T, T> =
    GroupedDataFrameImpl(this, frameColumn(groupedColumnName).typed()) { none() }

public fun <T, G> DataFrame<T>.asGroupedDataFrame(groupedColumn: ColumnReference<DataFrame<G>?>): GroupedDataFrame<T, G> =
    GroupedDataFrameImpl(this, frameColumn(groupedColumn.name()).typed()) { none() }

public fun <T> DataFrame<T>.asGroupedDataFrame(): GroupedDataFrame<T, T> {
    val groupCol = columns().single { it.isTable() }.asTable() as FrameColumn<T>
    return asGroupedDataFrame { groupCol }
}

public fun <T, G> DataFrame<T>.asGroupedDataFrame(selector: ColumnSelector<T, DataFrame<G>?>): GroupedDataFrame<T, G> {
    val column = column(selector).asTable()
    return GroupedDataFrameImpl(this, column) { none() }
}

// endregion

// endregion

// region DataRow

public fun <T> AnyRow.typed(): DataRow<T> = this as DataRow<T>

public fun <T> DataRow<T>.toDataFrame(): DataFrame<T> = owner[index..index]

// endregion

// region Array

public inline fun <reified T> Array<T>.toValueColumn(name: String): ValueColumn<T> = DataColumn.create(name, this.asList(), getType<T>())

// endregion
