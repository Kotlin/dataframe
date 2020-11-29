package org.jetbrains.dataframe

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

interface SpreadContext {
    class DataFrame<T>(val df: TypedDataFrame<T>) : SpreadContext
    class GroupedDataFrame<T,G>(val df: org.jetbrains.dataframe.GroupedDataFrame<T, G>) : SpreadContext
    class GroupAggregator<T>(val builder: GroupAggregateBuilder<T>): SpreadContext
}

class SpreadClause<T, K, V, C: SpreadContext>(val context: C, val keyColumn: ColumnSelector<T, K>, val valueColumn: ColumnSelector<T, *>?, val valueSelector: Reducer<T, V>, val valueType: KType, val defaultValue: Any? = null, val columnPath: (K)->List<String>?){

    companion object {
        fun <T, K> inDataFrame(df: TypedDataFrame<T>, keyColumn: ColumnSelector<T, K>) = create(SpreadContext.DataFrame(df), keyColumn)
        fun <T, G, K> inGroupedDataFrame(df: GroupedDataFrame<T, G>, keyColumn: ColumnSelector<G, K>) = create(SpreadContext.GroupedDataFrame(df), keyColumn)
        fun <T, K> inAggregator(builder: GroupAggregateBuilder<T>, keyColumn: ColumnSelector<T, K>) = create(SpreadContext.GroupAggregator(builder), keyColumn)
        fun <T, K, C:SpreadContext> create(context: C, keyColumn: ColumnSelector<T,K>) = SpreadClause(context, keyColumn, null, { true }, getType<Boolean>(), false) { listOf(it.toString()) }
    }
}

fun <T, C> TypedDataFrame<T>.spread(selector: ColumnSelector<T, C>) =
        SpreadClause.inDataFrame(this, selector)

fun <T, G, C> GroupedDataFrame<T, G>.spread(selector: ColumnSelector<G, C>) =
        SpreadClause.inGroupedDataFrame(this, selector)

internal fun <T, K, V, C:SpreadContext> SpreadClause<T, K, V, C>.addPath(keyTransform: (K)->String?) = SpreadClause(context, keyColumn, valueColumn, valueSelector, valueType, defaultValue) { keyTransform(it)?.let { listOf(it) } }

inline fun <T, K, reified V, C:SpreadContext> SpreadClause<T, K, *, C>.with(noinline valueSelector: Reducer<T, V>) = SpreadClause(context, keyColumn, valueColumn, valueSelector, getType<V>(), null, columnPath)

fun <T, K, V, C:SpreadContext> SpreadClause<T, K, V, C>.useDefault(defaultValue: V) = SpreadClause(context, keyColumn, valueColumn, valueSelector, valueType, defaultValue, columnPath)

@JvmName("useDefaultTKVC")
fun <T, K, V, C:SpreadContext> SpreadClause<T, K, ColumnData<V>, C>.useDefault(defaultValue: V): SpreadClause<T, K, ColumnData<V>, C> = SpreadClause(context, keyColumn, valueColumn, valueSelector, valueType, defaultValue, columnPath)

internal fun <T, K, V, C:SpreadContext> SpreadClause<T, K, V, *>.changeContext(newContext: C) = SpreadClause(newContext, keyColumn, valueColumn, valueSelector, valueType, defaultValue, columnPath)

inline fun <T, K, reified V> SpreadClause<T, K, *, SpreadContext.DataFrame<T>>.by(noinline columnSelector: ColumnSelector<T, V>): SpreadClause<T, K, ColumnData<V>, SpreadContext.DataFrame<T>> = SpreadClause(context, keyColumn, columnSelector, { getColumn(columnSelector) }, getType<ColumnData<V>>(), null, columnPath)

inline fun <T, K, V, reified R> SpreadClause<T, K, ColumnData<V>, SpreadContext.DataFrame<T>>.map(noinline transform: (V) -> R) = SpreadClause(context, keyColumn, valueColumn, { valueSelector(it, it).map(getType<R>(), transform) }, getType<ColumnData<R>>(), null, columnPath)

inline fun <T, K, reified V, C:SpreadContext> SpreadClause<T, K, *, C>.withSingle(noinline valueSelector: RowSelector<T, V>) = with {
    when (it.nrow) {
        0 -> null
        1 -> {
            val row = it[0]
            valueSelector(row, row)
        }
        else -> throw Exception()
    }
}

fun <T, G> GroupedDataFrame<T, G>.countBy(keySelector: ColumnSelector<G, String?>) = aggregate {
    countBy(keySelector).into { it }
}

infix fun <T, K, V> SpreadClause<T, K, V, SpreadContext.GroupAggregator<T>>.into(keyTransform: (K)->String?) = addPath(keyTransform).execute()

@JvmName("intoTKVT")
fun <T, K, V> SpreadClause<T, K, V, SpreadContext.DataFrame<T>>.into(keyTransform: (K)->String?) = addPath(keyTransform).execute()

@JvmName("intoTKVTT")
fun <T, G, K, V> SpreadClause<G, K, V, SpreadContext.GroupedDataFrame<T, G>>.into(keyTransform: (K)->String?) = addPath(keyTransform).execute()

@JvmName("spreadForDataFrame")
internal fun <T,K,V> SpreadClause<T,K,V,SpreadContext.DataFrame<T>>.execute(): TypedDataFrame<T> {
    val df = context.df
    val grouped = df.groupBy {
        val columnsToExclude = valueColumn?.let { keyColumn() and it()} ?: keyColumn()
        allExcept(columnsToExclude)
    }
    return grouped.aggregate {
        val clause = changeContext(SpreadContext.GroupAggregator(this))
        clause.execute()
    }
}

internal fun <T,K,V,G> SpreadClause<G,K,V,SpreadContext.GroupedDataFrame<T,G>>.execute(): TypedDataFrame<T> {
    val df = context.df
    return df.aggregate {
        val clause = changeContext(SpreadContext.GroupAggregator(this))
        clause.execute()
    }
}

internal fun <T,K,V> SpreadClause<T,K,V, SpreadContext.GroupAggregator<T>>.execute() {
    val df = context.builder.df
    val keyColumnData = df.getColumn(keyColumn)
    val isColumnType = (valueType.classifier as? KClass<*>)?.isSubclassOf(ColumnData::class) ?: false

    // TODO: extract to global type cache
    val columnDataType = if(isColumnType) valueType.arguments[0].type else null
    val columnNullableDataType = columnDataType?.withNullability(true)
    val columnListType = columnDataType?.let { createType<List<*>>(it) }
    val columnNullableListType = columnNullableDataType?.let { createType<List<*>>(it) }

    df.groupBy(keyColumnData).forEach { key, group ->
        val keyValue = keyColumnData[key.index]
        val path = columnPath(keyValue) ?: return@forEach

        val value = valueSelector(group, group)

        // is computed value is column, extract a single value or a list of values from it
        var columnValue : Any? = value
        var columnType = valueType
        if(columnDataType != null) {
            if(value == null) columnType = columnNullableDataType!!
            else {
                val col = value as? ColumnData<*>
                if(col != null){
                    if(col.size == 1) {
                        columnValue = col[0]
                        columnType = if(columnValue == null) columnNullableDataType!! else columnDataType
                    }else {
                        val list = col.toList()
                        columnValue = list
                        columnType = if(col.hasNulls) columnNullableListType!! else columnListType!!
                    }
                }
            }
        }
        columnType = columnValue?.javaClass?.kotlin?.createStarProjectedType(false) ?: getType<Any?>()
        context.builder.add(path, columnValue, columnType, defaultValue)
    }
}

class GatherClause<T, C, K, R>(val df: TypedDataFrame<T>, val selector: ColumnsSelector<T, C>, val filter: ((C) -> Boolean)? = null,
                               val nameTransform: ((String) -> K), val valueTransform: ((C) -> R))

typealias Predicate<T> = (T) -> Boolean

internal infix fun <T> (Predicate<T>).and(other: Predicate<T>): Predicate<T> = { this(it) && other(it) }

fun <T, C> TypedDataFrame<T>.gather(selector: ColumnsSelector<T, C>) = GatherClause(this, selector, null, { it }, { it })

fun <T, C, K, R> GatherClause<T, C, K, R>.where(filter: Predicate<C>) = GatherClause(df, selector, this.filter?.let { it and filter }
        ?: filter,
        nameTransform, valueTransform)

fun <T, C, K, R> GatherClause<T, C, *, R>.mapNames(transform: (String) -> K) = GatherClause(df, selector, filter, transform, valueTransform)

fun <T, C, K, R> GatherClause<T, C, K, *>.map(transform: (C) -> R) = GatherClause(df, selector, filter, nameTransform, transform)

fun <T, C : Any, K, R> GatherClause<T, C?, K, *>.mapNotNull(transform: (C) -> R) = GatherClause(df, selector, filter, nameTransform, { if (it != null) transform(it) else null })

inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String) = gatherImpl(keyColumn, null, getType<K>(), getType<R>())

inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String, valueColumn: String) = gatherImpl(keyColumn, valueColumn, getType<K>(), getType<R>())

fun <T, C, K, R> GatherClause<T, C, K, R>.gatherImpl(namesTo: String, valuesTo: String? = null, keyColumnType: KType, valueColumnType: KType): TypedDataFrame<T> {

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

fun <T> TypedDataFrame<T>.mergeRows(selector: ColumnsSelector<T, *>): TypedDataFrame<T> {

    return groupBy { allExcept(selector) }.modify {
        val updated = update(selector).with2 { row, column -> if(row.index == 0) column.toList() else emptyList() }
        updated[0..0]
    }.ungroup()
}

class MergeClause<T, C, R>(val df: TypedDataFrame<T>, val selector: ColumnsSelector<T, C>, val transform: (Iterable<C>) -> R)

fun <T, C> TypedDataFrame<T>.mergeCols(selector: ColumnsSelector<T, C>) = MergeClause(this, selector, { it })

inline fun <T, C, reified R> MergeClause<T, C, R>.into(columnName: String) = into(listOf(columnName))

inline fun <T, C, reified R> MergeClause<T, C, R>.into(columnPath: List<String>): TypedDataFrame<T> {
    val grouped = df.move(selector).into(columnPath)
    val res = grouped.update { getGroup(columnPath) }.with {
        transform(it.values.map { it as C })
    }
    return res
}

fun <T, C, R> MergeClause<T, C, R>.asStrings() = by(", ")

fun <T, C, R> MergeClause<T, C, R>.by(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...") =
        MergeClause<T, C, String>(df, selector) { it.joinToString(separator = separator, prefix = prefix, postfix = postfix, limit = limit, truncated = truncated) }

fun <T, C> MergeClause<T, C, *>.mergeRows(names: List<String>): TypedDataFrame<T> {

    val columnsToMerge = df.getColumns(selector)
    assert(names.size == columnsToMerge.size)
    val (_, removedTree) = df.doRemove(columnsToMerge)
    val groupingColumns = df.getColumns { allExcept(selector) }
    val grouped = df.groupBy(groupingColumns)
    val columnsToInsert = removedTree.allWithColumns().map { node ->
        val column = node.data.column!!
        val newName = column.name
        val newColumn = when(column){
            is GroupedColumn<*> -> {
                val data = grouped.groups.asIterable().map { it.get(column).df }
                ColumnData.createTable(newName, data, column.df)
            }
            is TableColumn<*> -> {
                val data = grouped.groups.asIterable().map { it[column].toList().union() }
                ColumnData.createTable(newName, data, column.df)
            }
            else -> {
                val data = grouped.groups.asIterable().map { it[column].toList() }
                ColumnData.create(newName, data, List::class.createType(column.type))
            }
        }
        ColumnToInsert(node.pathFromRoot(), node, newColumn)
    }
    val result = insertColumns(grouped.keys, columnsToInsert)
    return result
}

inline fun <T, C, R, reified V> MergeClause<T, C, R>.by(crossinline transform: (R) -> V) = MergeClause(df, selector) { transform(this@by.transform(it)) }

internal class ColumnDataCollector(initCapacity: Int = 0) {
    private val classes = mutableSetOf<KClass<*>>()
    private var hasNulls = false
    private val data = ArrayList<Any?>(initCapacity)

    fun add(value: Any?) {
        if (value == null) hasNulls = true
        else classes.add(value.javaClass.kotlin)
        data.add(value)
    }

    val values: List<*>
        get() = data

    fun toColumn(name: String) = column(name, data, classes.commonParent().createStarProjectedType(hasNulls))

    fun toColumn(name: String, clazz: KClass<*>) = column(name, data, clazz.createStarProjectedType(hasNulls))
}

internal class TypedColumnDataCollector<T>(initCapacity: Int = 0, val type: KType) {
    private var hasNulls = false
    private val data = ArrayList<T?>(initCapacity)

    fun add(value: T?) {
        if (value == null) hasNulls = true
        data.add(value)
    }

    val values: List<T?>
        get() = data

    fun toColumn(name: String) = column(name, data, type.withNullability(hasNulls))
}

internal inline fun <reified T> createDataCollector(initCapacity: Int = 0) = TypedColumnDataCollector<T>(initCapacity, getType<T>())

internal fun <T> createDataCollector(type: KType, initCapacity: Int = 0) = TypedColumnDataCollector<T>(initCapacity, type)

class SplitColClause<T, C, out R>(val df: TypedDataFrame<T>, val column: ColumnData<C>, val transform: (C) -> R)

fun <T, C> SplitColClause<T, C, String?>.by(vararg delimiters: Char, ignoreCase: Boolean = false, limit: Int = 0) = SplitColClause(df, column) {
    transform(it)?.split(*delimiters, ignoreCase = ignoreCase, limit = limit)
}

fun <T, C> SplitColClause<T, C, String?>.by(vararg delimiters: String, trim: Boolean = true, ignoreCase: Boolean = false, limit: Int = 0) = SplitColClause(df, column) {
    transform(it)?.split(*delimiters, ignoreCase = ignoreCase, limit = limit)?.let {
        if (trim) it.map { it.trim() }
        else it
    }
}

fun <T, C> SplitColClause<T, C, List<*>?>.into(vararg firstNames: String, nameGenerator: ((Int) -> String)? = null) = doSplitCols {
    when {
        it < firstNames.size -> firstNames[it]
        nameGenerator != null -> nameGenerator(it - firstNames.size)
        else -> throw Exception()
    }
}

fun <T, C> SplitColClause<T, C, List<*>?>.doSplitCols(columnNameGenerator: (Int) -> String): TypedDataFrame<T> {

    val nameGenerator = df.nameGenerator()
    val nrow = df.nrow
    val columnNames = mutableListOf<String>()
    val columnCollectors = mutableListOf<ColumnDataCollector>()
    for (row in 0 until nrow) {
        val list = transform(column[row])
        val listSize = list?.size ?: 0
        for (j in 0 until listSize) {
            if (columnCollectors.size <= j) {
                val newName = nameGenerator.addUnique(columnNameGenerator(columnCollectors.size))
                columnNames.add(newName)
                val collector = ColumnDataCollector(nrow)
                repeat(row) { collector.add(null) }
                columnCollectors.add(collector)
            }
            columnCollectors[j].add(list!![j])
        }
        for (j in listSize until columnCollectors.size)
            columnCollectors[j].add(null)
    }
    return df - column + columnCollectors.mapIndexed { i, col -> col.toColumn(columnNames[i]) }
}

fun <T, C> TypedDataFrame<T>.split(selector: ColumnSelector<T, C>) = SplitColClause(this, getColumn(selector), { it })

fun <T> TypedDataFrame<T>.splitRows(selector: ColumnSelector<T, List<*>?>) = split(selector).intoRows()

fun <T, C> SplitColClause<T, C, List<*>?>.intoRows(): TypedDataFrame<T> {

    val path = column.getPath()
    val transformedColumn = column.map(transform)
    val list = transformedColumn.toList()
    val outputRowsCount = list.sumBy {
        it?.size ?: 0
    }

    fun splitIntoRows(df: TypedDataFrame<*>, path: ColumnPath?, list: List<List<*>?>): TypedDataFrame<*> {

        val newColumns = df.columns.map { col ->
            if (col.isGrouped()) {
                val group = col.asGrouped()
                val newPath = path?.let { if(it.size > 0 && it[0] == col.name) it.drop(1) else null }
                val newDf = splitIntoRows(group.df, newPath, list)
                ColumnData.createGroup(col.name, newDf)
            } else {
                if (path != null && path.size == 1 && path[0] == col.name) {
                    val collector = ColumnDataCollector(outputRowsCount)
                    for(row in 0 until col.size)
                        list[row]?.forEach { collector.add(it) }
                    collector.toColumn(col.name)
                } else {
                    val collector = TypedColumnDataCollector<Any?>(outputRowsCount, col.type)
                    for (row in 0 until col.size) {
                        val l = list[row]
                        if (l != null && l.size > 0) {
                            val value = col[row]
                            repeat(l.size) {
                                collector.add(value)
                            }
                        }
                    }
                    if (col.isTable()) ColumnData.createTable(col.name, collector.values as List<TypedDataFrame<*>>, col.asTable<Any?>().df)
                    else collector.toColumn(col.name)
                }
            }
        }
        return newColumns.asDataFrame<Unit>()
    }

    return splitIntoRows(df, path, list).typed()
}