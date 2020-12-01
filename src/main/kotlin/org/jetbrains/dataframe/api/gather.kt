package org.jetbrains.dataframe

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

class GatherClause<T, C, K, R>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>, val filter: ((C) -> Boolean)? = null,
                               val nameTransform: ((String) -> K), val valueTransform: ((C) -> R))

fun <T, C> DataFrame<T>.gather(selector: ColumnsSelector<T, C>) = GatherClause(this, selector, null, { it }, { it })
fun <T, C, K, R> GatherClause<T, C, K, R>.where(filter: Predicate<C>) = GatherClause(df, selector, this.filter?.let { it and filter }
        ?: filter,
        nameTransform, valueTransform)

fun <T, C, K, R> GatherClause<T, C, *, R>.mapNames(transform: (String) -> K) = GatherClause(df, selector, filter, transform, valueTransform)
fun <T, C, K, R> GatherClause<T, C, K, *>.map(transform: (C) -> R) = GatherClause(df, selector, filter, nameTransform, transform)
fun <T, C : Any, K, R> GatherClause<T, C?, K, *>.mapNotNull(transform: (C) -> R) = GatherClause(df, selector, filter, nameTransform, { if (it != null) transform(it) else null })
inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String) = gatherImpl(keyColumn, null, getType<K>(), getType<R>())
inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String, valueColumn: String) = gatherImpl(keyColumn, valueColumn, getType<K>(), getType<R>())
fun <T, C, K, R> GatherClause<T, C, K, R>.gatherImpl(namesTo: String, valuesTo: String? = null, keyColumnType: KType, valueColumnType: KType): DataFrame<T> {

    val keyColumns = df.getColumns(selector).map { df[it] }
    val otherColumns = df.columns - keyColumns
    val outputColumnsData = otherColumns.map { ArrayList<Any?>() }.toMutableList()
    val keyColumnData = ArrayList<K>()
    val valueColumnData = ArrayList<R>()
    val include = filter ?: { true }
    var hasNullValues = false
    val keys = keyColumns.map { nameTransform(it.name) }
    val classes = if (valueColumnType.jvmErasure == Any::class) mutableSetOf<KClass<*>>() else null
    (0 until df.nrow).forEach { row ->
        keyColumns.forEachIndexed { colIndex, col ->
            val value = col[row]
            if (include(value)) {
                outputColumnsData.forEachIndexed { index, list ->
                    list.add(otherColumns[index][row])
                }
                keyColumnData.add(keys[colIndex])
                if (valuesTo != null) {
                    val dstValue = valueTransform(value)
                    valueColumnData.add(dstValue)
                    if (dstValue == null)
                        hasNullValues = true
                    else if (classes != null)
                        classes.add((dstValue as Any).javaClass.kotlin)
                }
            }
        }
    }
    val resultColumns = outputColumnsData.mapIndexed { index, values ->
        val srcColumn = otherColumns[index]
        srcColumn.withValues(values, srcColumn.hasNulls)
    }.toMutableList()
    resultColumns.add(column(namesTo, keyColumnData, keyColumnType.withNullability(keys.contains(null))))
    if (valuesTo != null)
        resultColumns.add(column(valuesTo, valueColumnData, classes?.commonType(hasNullValues)
                ?: valueColumnType.withNullability(hasNullValues)))
    return dataFrameOf(resultColumns).typed()
}