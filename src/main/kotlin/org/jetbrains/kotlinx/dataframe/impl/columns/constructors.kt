package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Many
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.api.AddDataRowImpl
import org.jetbrains.kotlinx.dataframe.api.AddExpression
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.emptyMany
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.guessValueType
import org.jetbrains.kotlinx.dataframe.manyOf
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

// region create DataColumn

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.newColumn(type: KType?, name: String = "", expression: AddExpression<T, R>): DataColumn<R> {
    val (nullable, values) = computeValues(this as DataFrame<T>, expression)
    if (type == null) return guessColumnType(name, values)
    return when (type.classifier) {
        DataFrame::class -> DataColumn.createFrameColumn(name, values as List<AnyFrame>) as DataColumn<R>
        DataRow::class -> DataColumn.createColumnGroup(name, (values as List<AnyRow>).concat()) as DataColumn<R>
        else -> DataColumn.createValueColumn(name, values, type.withNullability(nullable))
    }
}

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.newColumnWithActualType(name: String, expression: AddExpression<T, R>): DataColumn<R> {
    val (_, values) = computeValues(this as DataFrame<T>, expression)
    return guessColumnType(name, values)
}

internal fun <T, R> computeValues(df: DataFrame<T>, expression: AddExpression<T, R>): Pair<Boolean, List<R>> {
    var nullable = false
    val list = ArrayList<R>(df.nrow())
    df.indices().forEach {
        val row = AddDataRowImpl(it, df, list)
        val value = expression(row, row)
        if (value == null) nullable = true
        list.add(value)
    }
    return nullable to list
}

@PublishedApi
internal fun <T> createColumn(values: Iterable<T>, suggestedType: KType, guessType: Boolean = false): DataColumn<T> = when {
    values.all { it is AnyCol } -> DataColumn.createColumnGroup("", (values as Iterable<AnyCol>).toDataFrame()) as DataColumn<T>
    values.all { it == null || it is AnyFrame } -> DataColumn.createFrameColumn(
        "",
        values.map { it as? AnyFrame ?: DataFrame.empty() }
    ) as DataColumn<T>
    guessType -> guessColumnType("", values.asList(), suggestedType, suggestedTypeIsUpperBound = true).cast<T>()
    else -> DataColumn.createValueColumn("", values.toList(), suggestedType)
}

// endregion

// region create Columns

internal fun <TD, T : DataFrame<TD>, C> Selector<T, ColumnSet<C>>.toColumns(
    createReceiver: (ColumnResolutionContext) -> T
): ColumnSet<C> =
    createColumnSet {
        val receiver = createReceiver(it)
        val columnSet = this(receiver, receiver)
        columnSet.resolve(receiver, it.unresolvedColumnsPolicy)
    }

internal fun <C> createColumnSet(resolver: (ColumnResolutionContext) -> List<ColumnWithPath<C>>): ColumnSet<C> =
    object : ColumnSet<C> {
        override fun resolve(context: ColumnResolutionContext) = resolver(context)
    }

internal fun Array<out ColumnSet<*>>.toColumns(): ColumnSet<Any?> = ColumnsList(this.asList())
internal fun Array<out String>.toColumns(): ColumnSet<Any?> = map { it.toColumnAccessor() }.toColumnSet()
internal fun <C> Array<out String>.toColumnsOf(): ColumnSet<C> = toColumns() as ColumnSet<C>
internal fun Array<out String>.toComparableColumns() = toColumnsOf<Comparable<Any?>>()
internal fun String.toComparableColumn() = toColumnOf<Comparable<Any?>>()
internal fun Array<out String>.toNumberColumns() = toColumnsOf<Number>()
internal fun Array<out ColumnPath>.toColumns(): ColumnSet<Any?> = map { it.toColumnAccessor() }.toColumnSet()
internal fun <C> Iterable<ColumnSet<C>>.toColumnSet(): ColumnSet<C> = ColumnsList(asList())

@JvmName("toColumnSetC")
internal fun <C> Iterable<ColumnReference<C>>.toColumnSet(): ColumnSet<C> = ColumnsList(toList())

@PublishedApi
internal fun <C> Array<out KProperty<C>>.toColumns(): ColumnSet<C> = map { it.toColumnAccessor() }.toColumnSet()

@PublishedApi
internal fun <T> Array<out ColumnReference<T>>.toColumns(): ColumnSet<T> = asIterable().toColumnSet()
internal fun Iterable<String>.toColumns() = map { it.toColumnAccessor() }.toColumnSet()

internal fun <T, C> ColumnsSelector<T, C>.toColumns(): ColumnSet<C> = toColumns {
    object : DataFrameReceiver<T>(it.df.cast(), it.allowMissingColumns), ColumnsSelectionDsl<T> { }
}

// endregion

internal fun <T> guessColumnType(name: String, values: List<T>) = guessColumnType(name, values, null)

@PublishedApi
internal fun <T> guessColumnType(
    name: String,
    values: List<T>,
    suggestedType: KType? = null,
    suggestedTypeIsUpperBound: Boolean = false,
    defaultValue: T? = null,
    nullable: Boolean? = null,
): DataColumn<T> {
    val detectType = suggestedType == null || suggestedTypeIsUpperBound
    val type = if (detectType) guessValueType(values.asSequence(), suggestedType) else suggestedType!!

    return when (type.classifier!! as KClass<*>) {
        DataRow::class -> {
            val df = values.map { (it as AnyRow).toDataFrame() }.concat()
            DataColumn.createColumnGroup(name, df).asDataColumn().cast()
        }
        DataFrame::class -> {
            val frames = values.map {
                when (it) {
                    null -> DataFrame.empty()
                    is AnyFrame -> it
                    is AnyRow -> it.toDataFrame()
                    is List<*> -> (it as List<AnyRow>).toDataFrame()
                    else -> throw IllegalStateException()
                }
            }
            DataColumn.createFrameColumn(name, frames).asDataColumn().cast()
        }
        Many::class -> {
            val nullable = type.isMarkedNullable
            val lists = values.map {
                when (it) {
                    null -> if (nullable) null else emptyMany()
                    is Many<*> -> it
                    else -> manyOf(it)
                }
            }
            DataColumn.createValueColumn(name, lists, type, checkForNulls = false, defaultValue).cast()
        }
        else -> {
            if (nullable == null) {
                DataColumn.createValueColumn(name, values, type, checkForNulls = !detectType, defaultValue)
            } else DataColumn.createValueColumn(name, values, type.withNullability(nullable), checkForNulls = false, defaultValue)
        }
    }
}
