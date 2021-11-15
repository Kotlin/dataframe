package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.GroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.ManyImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnAccessorImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.asValues
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

public fun <T> KProperty<T>.toColumnAccessor(): ColumnAccessor<T> = ColumnAccessorImpl<T>(name)

// endregion

// region DataColumn

public fun AnyBaseColumn.toDataFrame(): AnyFrame = dataFrameOf(listOf(this))

@JvmName("asNumberAny?")
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

public fun <T> DataColumn<T?>.asNotNullable(): DataColumn<T> {
    require(!hasNulls())
    return this as DataColumn<T>
}

public fun <T> BaseColumn<T>.toMany(): Many<T> = values().toMany()

// endregion

// region ColumnGroup

public fun <T> ColumnGroup<T>.asDataColumn(): DataColumn<DataRow<T>> = this as DataColumn<DataRow<T>>

public fun <T> ColumnGroup<T>.asDataFrame(): DataFrame<T> = df

// endregion

// region FrameColumn

public fun <T> FrameColumn<T>.asDataColumn(): DataColumn<DataFrame<T>?> = this

public fun <T> FrameColumn<T>.toValueColumn(): ValueColumn<DataFrame<T>?> = DataColumn.createValueColumn(name, toList(), type())

// endregion

// region ColumnSet

@JvmName("asNumbersAny")
public fun ColumnSet<Any>.asNumbers(): ColumnSet<Number> = this as ColumnSet<Number>

@JvmName("asNumbersAny?")
public fun ColumnSet<Any?>.asNumbers(): ColumnSet<Number?> = this as ColumnSet<Number?>

public fun <T> ColumnSet<T>.asComparable(): ColumnSet<Comparable<T>> = this as ColumnSet<Comparable<T>>

// endregion

// region Iterable

public fun <T> Iterable<DataFrame<T>>.toFrameColumn(name: String = ""): FrameColumn<T> =
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

public fun <T> DataFrame<T>.toColumnGroup(name: String): ColumnGroup<T> = DataColumn.createColumnGroup(name, this)

// region as GroupedDataFrame

public fun <T> DataFrame<T>.asGroupBy(groupedColumnName: String): GroupBy<T, T> =
    GroupByImpl(this, frameColumn(groupedColumnName).castFrameColumn()) { none() }

public fun <T, G> DataFrame<T>.asGroupBy(groupedColumn: ColumnReference<DataFrame<G>>): GroupBy<T, G> =
    GroupByImpl(this, frameColumn(groupedColumn.name()).castFrameColumn()) { none() }

public fun <T> DataFrame<T>.asGroupBy(): GroupBy<T, T> {
    val groupCol = columns().single { it.isFrameColumn() }.asFrameColumn().castFrameColumn<T>()
    return asGroupBy { groupCol }
}

public fun <T, G> DataFrame<T>.asGroupBy(selector: ColumnSelector<T, DataFrame<G>?>): GroupBy<T, G> {
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
    DataColumn.createValueColumn(name, this.asList(), getType<T>())

public fun Array<out String>.toPath(): ColumnPath = ColumnPath(this.asList())

// endregion
