package org.jetbrains.dataframe

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

class SpreadClause<T, K>(val df: TypedDataFrame<T>, val valueColumn: ColumnData<K>?)

class GroupSpreadClause<T, K>(val df: GroupedDataFrame<T>, val valueType: KType, val valueSelector: Reducer<T, K>)

fun <T, C> TypedDataFrame<T>.spread(valueSelector: SpreadColumnSelector<T, C>) =
        SpreadClause(this, getColumn(valueSelector))

fun <T> TypedDataFrame<T>.spreadExists(keySelector: SpreadColumnSelector<T, String?>) = SpreadClause<T, Unit>(this, null)
        .into(keySelector)

fun <T, V> SpreadClause<T, V>.into(keySelector: SpreadColumnSelector<T, String?>) =
        when (valueColumn) {
            null -> df.spreadToBool(df.getColumn(keySelector))
            else -> df.spreadToPair(df.getColumn(keySelector), valueColumn)
        }

inline fun <T, reified C> GroupedDataFrame<T>.spreadSingle(crossinline valueSelector: RowSelector<T, C>) = GroupSpreadClause(this, getType<C>()) {
    when (it.nrow) {
        0 -> null
        1 -> {
            val row = it[0]
            valueSelector(row, row)
        }
        else -> throw Exception()
    }
}

fun <T> GroupedDataFrame<T>.spreadExists(keySelector: RowSelector<T, String?>) = GroupSpreadClause(this, getType<Boolean>()) {
    it.nrow > 0
}.into(keySelector)

fun <T> GroupedDataFrame<T>.countBy(keySelector: RowSelector<T, String?>) = GroupSpreadClause(this, getType<Int>()) {
    it.nrow
}.into(keySelector)

fun <T, V> GroupSpreadClause<T, V>.into(keySelector: RowSelector<T, String?>) = df.aggregate {
    val clause = this@into
    doSpread(this, clause.df, keySelector, clause.valueType) { df, _ -> clause.valueSelector(df) }
}

internal fun <T, R> doSpread(builder: GroupAggregateBuilder<T>, grouped: GroupedDataFrame<T>, keyExpression: RowSelector<T, String?>, valueType: KType?, valueExpression: (TypedDataFrame<T>, String) -> R) {
    val df = grouped.baseDataFrame
    val nameGenerator = df.nameGenerator()
    val keyColumnName = nameGenerator.createUniqueName("KEY_EXPRESSION")
    val modifiedGroups = builder.groups.map { it.add(keyColumnName, keyExpression) }
    val keyColumnIndex = builder.groups.firstOrNull()?.ncol ?: -1
    val newColumns = modifiedGroups.fold(mutableSetOf<String?>()) { set, group -> set.addAll(group.columns[keyColumnIndex].typed<String?>().values).let { set } }
    val expectedType = when (valueType?.jvmErasure) {
        null -> null
        Any::class -> null
        else -> valueType
    }

    newColumns.filterNotNull().forEach { key ->
        val columnName = nameGenerator.createUniqueName(key)
        var hasNulls = false
        val classes = mutableSetOf<KClass<*>>()
        val values = modifiedGroups.map { group ->
            val valueRows = group.filter { it[keyColumnIndex] == columnName }.distinct()
            val value = valueExpression(valueRows, key)
            if (value == null) hasNulls = true
            else if (expectedType == null) classes.add(value.javaClass.kotlin)
            value
        }
        builder.add(ColumnDataImpl(values, columnName, expectedType?.withNullability(hasNulls)
                ?: classes.commonType(hasNulls)))
    }
}

internal fun <T> TypedDataFrame<T>.spreadToPair(keyColumn: ColumnDef<String?>, valueColumn: DataCol): TypedDataFrame<T> {

    val nameGenerator = nameGenerator()

    val (df1, keySrcColumn, keyColumnData) = extractConvertedColumn(this, keyColumn, nameGenerator)
    val (dataFrame, valueSrcColumn, valueColumnData) = extractConvertedColumn(df1, valueColumn, nameGenerator)
    val columnsToRemove = listOf(keySrcColumn, valueSrcColumn).filterNotNull()
    val groupingColumns = columns - columnsToRemove

    val keyColumnIndex = dataFrame.getColumnIndex(keyColumnData.name)
    assert(keyColumnIndex != -1)

    val valueColumnIndex = dataFrame.getColumnIndex(valueColumnData.name)
    assert(valueColumnIndex != -1)

    return dataFrame.groupBy(groupingColumns).spreadSingle { it[valueColumnIndex] }.into { it[keyColumnIndex] as String? }
}

internal fun extractOriginalColumn(column: Column): Column = when (column) {
    is ConvertedColumn<*> -> extractOriginalColumn(column.source)
    else -> column
}

internal fun <T, C> extractConvertedColumn(df: TypedDataFrame<T>, col: ColumnDef<C>, nameGenerator: ColumnNameGenerator): Triple<TypedDataFrame<T>, Column?, ColumnData<C>> =
        when {
            col is ConvertedColumn<C> -> {
                val srcColumn = extractOriginalColumn(col.source)
                val columnData = col.data.ensureUniqueName(nameGenerator)
                Triple(df + columnData, srcColumn, columnData)
            }
            col.name.isEmpty() -> {
                val colData = col as ColumnData<C>
                val renamed = colData.rename(nameGenerator.createUniqueName("columnData"))
                Triple(df + renamed, null, renamed)
            }
            else -> Triple(df, col, df[col])
        }

internal fun <T> TypedDataFrame<T>.spreadToBool(col: ColumnDef<String?>): TypedDataFrame<T> {

    val nameGenerator = nameGenerator()

    val (dataFrame, srcColumn, columnData) = extractConvertedColumn(this, col, nameGenerator)

    val keyColumnIndex = dataFrame.columns.indexOf(columnData)

    val groupingColumns = srcColumn?.let { columns - it } ?: columns

    return dataFrame.groupBy(groupingColumns).spreadExists { it[keyColumnIndex] as String? }
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

    val nestColumns = getColumns(selector)
    val otherColumns = columns - nestColumns

    return groupBy(otherColumns).aggregate {
        nestColumns.forEach { col ->
            val values = groups.map { it[col].values }
            val newColumn = column(col.name, values, getType<List<*>>())
            add(newColumn)
        }
    }
}

interface MergeType
class MergeCols : MergeType
class MergeRows : MergeType

class MergeClause<T, C, R, M : MergeType>(val df: TypedDataFrame<T>, val columns: List<ColumnData<C>>, val transform: (Iterable<C>) -> R)

class GroupMergeClause<T, C, R>(val df: GroupedDataFrame<T>, val selector: RowSelector<T, C>, val transform: (Iterable<C>) -> R)

fun <T, C> TypedDataFrame<T>.mergeCols(selector: ColumnsSelector<T, C>) = MergeClause<T, C, Iterable<C>, MergeCols>(this, getColumns(selector), { it })

inline fun <T, C, reified R> MergeClause<T, C, R, MergeCols>.into(columnName: String) = df.add(columnName)
{ row ->
    transform(columns.map { it[row.index] })
} - columns

fun <T, C> MergeClause<T, C, *, MergeCols>.intoStr(columnName: String) = by().into(columnName)

fun <T, C, R, M : MergeType> MergeClause<T, C, R, M>.asStrings() = by()

fun <T, C, R> GroupMergeClause<T, C, R>.asStrings() = by()

fun <T, C, R> GroupMergeClause<T, C, R>.by(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...") =
        GroupMergeClause(df, selector) { it.joinToString(separator = separator, prefix = prefix, postfix = postfix, limit = limit, truncated = truncated) }

fun <T, C, R, M : MergeType> MergeClause<T, C, R, M>.by(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", limit: Int = -1, truncated: CharSequence = "...") =
        MergeClause<T, C, String, M>(df, columns) { it.joinToString(separator = separator, prefix = prefix, postfix = postfix, limit = limit, truncated = truncated) }

inline fun <T, C, reified R> MergeClause<T, C, R, MergeRows>.inplace() = into(columns.map { it.name })

inline fun <T, C, reified R> MergeClause<T, C, R, MergeRows>.into(vararg names: String) = into(names.toList())

inline fun <T, C, reified R> MergeClause<T, C, R, MergeRows>.into(nameGenerator: (ColumnData<C>) -> String) = into(columns.map(nameGenerator))

@JvmName("mergeRowsInto")
inline fun <T, C, reified R> MergeClause<T, C, R, MergeRows>.into(names: List<String>): TypedDataFrame<T> {

    val columnsToMerge = columns
    assert(names.size == columnsToMerge.size)
    val otherColumns = df.columns - columnsToMerge
    val grouped = df.groupBy(otherColumns)
    return grouped.aggregate {
        doMergeRows(this, grouped, this@into, names)
    }
}

fun <T, R> GroupedDataFrame<T>.merge(selector: RowSelector<T, R>) = GroupMergeClause(this, selector, { it })

inline fun <T, C, reified R> GroupMergeClause<T, C, R>.into(name: String) = df.aggregate { this@into.into(name) }

fun <T, C, R> doMergeRows(builder: GroupAggregateBuilder<T>, grouped: GroupedDataFrame<T>, name: String, expression: RowSelector<T, C>, transform: (Iterable<C>)->R, type: KType) {

    val values = grouped.groups.map { transform(it.rows.map { expression(it, it) }) }
    val newColumn = column(name, values, type)
    builder.add(newColumn)
}

inline fun <T, C, reified R> doMergeRows(builder: GroupAggregateBuilder<T>, grouped: GroupedDataFrame<T>, clause: MergeClause<T, C, R, MergeRows>, names: List<String>) {

    val columnsToMerge = clause.columns
    assert(names.size == columnsToMerge.size)
    val nameGenerator = grouped.nameGenerator()
    columnsToMerge.forEachIndexed { i, col ->
        val values = grouped.groups.map { clause.transform(it[col].values) }
        val newColumn = column(nameGenerator.createUniqueName(names[i]), values, getType<R>())
        builder.add(newColumn)
    }
}

inline fun <T, C, R, reified V, M : MergeType> MergeClause<T, C, R, M>.by(crossinline transform: (R) -> V) = MergeClause<T, C, V, M>(df, columns) { transform(this@by.transform(it)) }

inline fun <T, C, R, V> GroupMergeClause<T, C, R>.by(crossinline transform: (R) -> V) = GroupMergeClause<T, C, V>(df, selector) { transform(this@by.transform(it)) }

internal class ColumnDataCollector(initCapacity: Int = 0) {
    private val classes = mutableSetOf<KClass<*>>()
    private var hasNulls = false
    private val values = ArrayList<Any?>(initCapacity)

    fun add(value: Any?) {
        if (value == null) hasNulls = true
        else classes.add(value.javaClass.kotlin)
        values.add(value)
    }

    fun toColumn(name: String) = column(name, values, classes.commonParent().createStarProjectedType(hasNulls))
}

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
                val newName = nameGenerator.createUniqueName(columnNameGenerator(columnCollectors.size))
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

    val columnIndex = df.columns.indexOf(column)
    val lists = column.map(transform)
    val outputRowsCount = lists.values.sumBy { list ->
        list?.size ?: 0
    }
    val ncol = df.ncol
    val outputColumnsData = Array<Array<Any?>>(ncol) { arrayOfNulls(outputRowsCount) }
    var dstRow = 0
    val classes = mutableSetOf<KClass<*>>()
    var hasNulls = false
    for (srcRow in 0 until df.nrow) {
        lists[srcRow]?.forEach { value ->
            if (value == null) hasNulls = true
            else classes.add(value.javaClass.kotlin)
            for (col in 0 until ncol) {
                outputColumnsData[col][dstRow] = if (col == columnIndex) value else df.columns[col][srcRow]
            }
            dstRow++
        }
    }
    val resultColumns = df.columns.mapIndexed { index, col ->
        val type = if (index == columnIndex) classes.commonType(hasNulls) else col.type
        column(col.name, outputColumnsData[index].asList(), type)
    }
    return dataFrameOf(resultColumns).typed()
}