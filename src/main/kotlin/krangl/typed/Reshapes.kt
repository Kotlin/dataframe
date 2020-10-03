package krangl.typed

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class SpreadClause<T, K>(val df: TypedDataFrame<T>, val valueColumn: TypedColData<K>?)

class GroupSpreadClause<T, K>(val df: GroupedDataFrame<T>, val valueClass: KClass<*>, val valueSelector: Reducer<T, K>)

fun <T, C> TypedDataFrame<T>.spread(keySelector: SpreadColumnSelector<T, C>) =
        SpreadClause(this, getColumn(keySelector))

fun <T> TypedDataFrame<T>.spreadExists(keySelector: SpreadColumnSelector<T, String?>) = SpreadClause<T, Unit>(this, null)
        .into(keySelector)

fun <T, V> SpreadClause<T, V>.into(keySelector: SpreadColumnSelector<T, String?>) =
        when (valueColumn) {
            null -> df.spreadToBool(df.getColumn(keySelector))
            else -> df.spreadToPair(df.getColumn(keySelector), valueColumn)
        }

inline fun <T, reified C> GroupedDataFrame<T>.spreadSingle(crossinline valueSelector: RowSelector<T, C>) = GroupSpreadClause(this, C::class) {
    when (it.nrow) {
        0 -> null
        1 -> {
            val row = it[0]
            valueSelector(row, row)
        }
        else -> throw Exception()
    }
}

fun <T> GroupedDataFrame<T>.spreadExists(keySelector: RowSelector<T, String?>) = GroupSpreadClause(this, Boolean::class) {
    it.nrow > 0
}.into(keySelector)

fun <T, V> GroupSpreadClause<T, V>.into(keySelector: RowSelector<T, String?>) = df.aggregate {
    val clause = this@into
    doSpread(this, clause.df, keySelector, clause.valueClass) { df, _ -> clause.valueSelector(df) }
}

internal fun <T, R> doSpread(builder: GroupAggregateBuilder<T>, grouped: GroupedDataFrame<T>, keyExpression: RowSelector<T, String?>, valueClass: KClass<*>?, valueExpression: (TypedDataFrame<T>, String) -> R) {
    val df = grouped.baseDataFrame
    val nameGenerator = df.nameGenerator()
    val keyColumnName = nameGenerator.createUniqueName("KEY_EXPRESSION")
    val modifiedGroups = builder.groups.map { it.add(keyColumnName, keyExpression) }
    val keyColumnIndex = builder.groups.firstOrNull()?.ncol ?: -1
    val newColumns = modifiedGroups.fold(mutableSetOf<String?>()) { set, group -> set.addAll(group.columns[keyColumnIndex].typed<String?>().values).let { set } }
    val expectedClass = when (valueClass) {
        null -> null
        Any::class -> null
        else -> valueClass
    }

    newColumns.filterNotNull().forEach { key ->
        val columnName = nameGenerator.createUniqueName(key)
        var hasNulls = false
        val classes = mutableSetOf<KClass<*>>()
        val values = modifiedGroups.map { group ->
            val valueRows = group.filter { it[keyColumnIndex] == columnName }.distinct()
            val value = valueExpression(valueRows, key)
            if (value == null) hasNulls = true
            else if (expectedClass == null) classes.add(value.javaClass.kotlin)
            value
        }
        builder.add(TypedDataCol(values, hasNulls, columnName, expectedClass ?: classes.commonParent()))
    }
}

internal fun <T> TypedDataFrame<T>.spreadToPair(keyColumn: TypedCol<String?>, valueColumn: DataCol): TypedDataFrame<T> {

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
    is ConvertedColumn<*> -> extractOriginalColumn(column.srcColumn)
    else -> column
}

internal fun <T, C> extractConvertedColumn(df: TypedDataFrame<T>, col: TypedCol<C>, nameGenerator: ColumnNameGenerator): Triple<TypedDataFrame<T>, Column?, TypedColData<C>> =
        when {
            col is ConvertedColumn<C> -> {
                val srcColumn = extractOriginalColumn(col.srcColumn)
                val columnData = col.data.ensureUniqueName(nameGenerator)
                Triple(df + columnData, srcColumn, columnData)
            }
            col.name.isEmpty() -> {
                val colData = col as TypedColData<C>
                val renamed = colData.rename(nameGenerator.createUniqueName("columnData"))
                Triple(df + renamed, null, renamed)
            }
            else -> Triple(df, col, df[col])
        }

internal fun <T> TypedDataFrame<T>.spreadToBool(col: TypedCol<String?>): TypedDataFrame<T> {

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

inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String) = gatherImpl(keyColumn, null, K::class, R::class)

inline fun <T, C, reified K, reified R> GatherClause<T, C, K, R>.into(keyColumn: String, valueColumn: String) = gatherImpl(keyColumn, valueColumn, K::class, R::class)

fun <T, C, K, R> GatherClause<T, C, K, R>.gatherImpl(namesTo: String, valuesTo: String? = null, keyColumnType: KClass<*>, valueColumnType: KClass<*>): TypedDataFrame<T> {

    val keyColumns = df.getColumns(selector).map { df[it] }
    val otherColumns = df.columns - keyColumns
    val outputColumnsData = otherColumns.map { ArrayList<Any?>() }.toMutableList()
    val keyColumnData = ArrayList<K>()
    val valueColumnData = ArrayList<R>()
    val include = filter ?: { true }
    var hasNullValues = false
    val keys = keyColumns.map { nameTransform(it.name) }
    val classes = if (valueColumnType == Any::class) mutableSetOf<KClass<*>>() else null
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
        srcColumn.withValues(values, srcColumn.nullable)
    }.toMutableList()
    resultColumns.add(column(namesTo, keyColumnData, keys.any { it == null }, keyColumnType))
    if (valuesTo != null)
        resultColumns.add(column(valuesTo, valueColumnData, hasNullValues, classes?.commonParent() ?: valueColumnType))
    return dataFrameOf(resultColumns).typed()
}

fun <T> TypedDataFrame<T>.mergeRows(selector: ColumnsSelector<T, *>): TypedDataFrame<T> {

    val nestColumns = getColumns(selector).map { this[it] }
    val otherColumns = columns - nestColumns

    return groupBy(otherColumns).aggregate {
        nestColumns.forEach { col ->
            val values = groups.map { it[col].values }
            val newColumn = column(col.name, values, false, List::class)
            add(newColumn)
        }
    }
}

fun <T> TypedDataFrame<T>.splitRows(selector: ColumnSelector<T, List<*>>): TypedDataFrame<T> {

    val nestedColumn = getColumn(selector)
    if (!nestedColumn.valueClass.isSubclassOf(List::class)) {
        throw Exception("Column ${nestedColumn.name} must contain values of type `List`")
    }
    val nestedColumnIndex = columns.indexOf(nestedColumn)
    val column = nestedColumn.typed<List<*>?>()
    val outputRowsCount = (0 until nrow).sumBy { row ->
        column[row]?.size ?: 0
    }
    val outputColumnsData = Array<Array<Any?>>(ncol) { arrayOfNulls(outputRowsCount) }
    var dstRow = 0
    val classes = mutableSetOf<KClass<*>>()
    var hasNulls = false
    for (srcRow in 0 until nrow) {
        column[srcRow]?.forEach { value ->
            if (value == null) hasNulls = true
            else classes.add(value.javaClass.kotlin)
            for (col in 0 until ncol) {
                outputColumnsData[col][dstRow] = if (col == nestedColumnIndex) value else columns[col][srcRow]
            }
            dstRow++
        }
    }
    val resultColumns = columns.mapIndexed { index, col ->
        val (nullable, clazz) = if (index == nestedColumnIndex) hasNulls to classes.commonParent() else col.nullable to col.valueClass
        column(col.name, outputColumnsData[index].asList(), nullable, clazz)
    }
    return dataFrameOf(resultColumns).typed<T>()
}

class MergeColsClause<T, C, R>(val df: TypedDataFrame<T>, val columns: List<TypedColData<C>>, val transform: (List<C>) -> R)

fun <T, C> TypedDataFrame<T>.mergeCols(selector: ColumnsSelector<T, C>) = MergeColsClause<T, C, List<C>>(this, getColumns(selector), { it })

inline fun <T, C, reified R> MergeColsClause<T, C, R>.into(columnName: String) = df.add(columnName)
{ row ->
    transform(columns.map { it[row.index] })
} - columns

fun <T, C, R> MergeColsClause<T, C, R>.by(separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "") =
        MergeColsClause(df, columns) { it.joinToString(separator = separator, prefix = prefix, postfix = postfix) }

fun <T> TypedDataFrame<T>.mergeColsOLD(newColumn: String, selector: ColumnsSelector<T, *>) = mergeCols(newColumn, { list -> list }, selector)

fun <T> TypedDataFrame<T>.mergeColsToString(newColumnName: String, separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", selector: ColumnsSelector<T, *>) = mergeCols(newColumnName, { list -> list.joinToString(separator = separator, prefix = prefix, postfix = postfix) }, selector)

internal inline fun <T, C, reified R> TypedDataFrame<T>.mergeCols(newColumn: String, crossinline transform: (List<C>) -> R, noinline selector: ColumnsSelector<T, C>): TypedDataFrame<T> {
    val nestColumns = getColumns(selector).map { this[it] }
    return add(newColumn) { row ->
        transform(nestColumns.map { it[row.index] })
    } - nestColumns
}

internal class ColumnDataCollector(initCapacity: Int = 0) {
    private val classes = mutableSetOf<KClass<*>>()
    private var hasNulls = false
    private val values = ArrayList<Any?>(initCapacity)

    fun add(value: Any?) {
        if (value == null) hasNulls = true
        else classes.add(value.javaClass.kotlin)
        values.add(value)
    }

    fun toColumn(name: String) = column(name, values, hasNulls, classes.commonParent())
}

fun <T> TypedDataFrame<T>.splitCol(vararg names: String, selector: ColumnSelector<T, *>): TypedDataFrame<T> {
    val column = getColumn(selector)

    val splitter: (Any?) -> List<Any?>?
    splitter = when (column.valueClass) {
        List::class -> { it: Any? -> it as? List<Any?> }
        String::class -> { it: Any? -> (it as? String?)?.split(",")?.map { it.trim() } }
        else -> throw Exception("Column type should be `List`")
    }

    val columnsCount = names.size
    val columnCollectors = Array(columnsCount) { ColumnDataCollector(nrow) }
    for (i in 0 until nrow) {
        val list = splitter(column[i])
        val listSize = list?.size ?: 0
        for (j in 0 until listSize) {
            columnCollectors[j].add(list!![j])
        }
        for (j in listSize until columnsCount)
            columnCollectors[j].add(null)
    }
    return this - column + columnCollectors.mapIndexed { i, col -> col.toColumn(names[i]) }
}