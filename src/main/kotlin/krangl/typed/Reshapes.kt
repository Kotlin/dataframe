package krangl.typed

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun <T> TypedDataFrame<T>.spread(columnSelector: TypedDataFrameForSpread<T>.(TypedDataFrameForSpread<T>) -> TypedCol<String?>): TypedDataFrame<T> {
    val receiver = TypedDataFrameForSpreadImpl(this)
    return when (val column = columnSelector(receiver, receiver)) {
        is TypedColumnPair<String?, *> -> spreadToPair(column.firstColumn, column.secondColumn)
        else -> spreadToBool(column)
    }
}

internal fun <T, R : Any> TypedDataFrame<T>.spreadToPair(keyColumn: TypedCol<String?>, valueColumn: TypedCol<R?>): TypedDataFrame<T> {
    val keyColumnData = this[keyColumn]
    val keys = keyColumnData.toSet()
    val nameGenerator = nameGenerator()
    val groupingColumns = columns - keyColumn - valueColumn
    val grouped = groupBy(groupingColumns)
    return grouped.aggregate {
        keys.forEach { key ->
            if (key != null) {
                val columnName = nameGenerator.createUniqueName(key)
                var hasNulls = false
                val classes = mutableSetOf<KClass<*>>()
                val values = groups.map { group ->
                    val valueRows = group.filter { it[keyColumn] == columnName }.distinct()
                    when (valueRows.nrow) {
                        0 -> {
                            hasNulls = true
                            null
                        }
                        1 -> {
                            val value = valueRows[0][valueColumn]
                            if (value == null) hasNulls = true
                            else classes.add(value.javaClass.kotlin)
                            value
                        }
                        else -> {
                            val firstRow = group.first()
                            val groupKey = groupingColumns.map { "${it.name}: ${firstRow[it]}" }.joinToString()
                            val values = valueRows.map { it[valueColumn] }.joinToString()
                            throw Exception("Different values ($values) for key '${key}' at entry [$groupKey]")
                        }
                    }
                }
                add(TypedDataCol(values, hasNulls, columnName, classes.commonParent()))
            }
        }
    }
}

internal fun <T> TypedDataFrame<T>.spreadToBool(columnDef: TypedCol<String?>): TypedDataFrame<T> {

    val column = this[columnDef]
    val values = column.toSet()
    val nameGenerator = nameGenerator()
    val grouped = groupBy(columns - column)
    return grouped.aggregate {
        values.forEach { value ->
            if (value != null) {
                val columnName = nameGenerator.createUniqueName(value)
                add(columnName) {
                    it.any { it[column] == value }
                }
            }
        }
    }
}

fun <T> TypedDataFrame<T>.gather(keyColumn: String, selector: ColumnsSelector<T>) = pivotImpl(keyColumn, null, selector)

fun <T> TypedDataFrame<T>.gather(keyColumn: String, valueColumn: String, selector: ColumnsSelector<T>) = pivotImpl(keyColumn, valueColumn, selector)

internal fun <T> TypedDataFrame<T>.pivotImpl(keyColumn: String, valueColumn: String?, selector: ColumnsSelector<T>): TypedDataFrame<T> {

    val pivotColumns = getColumns(selector).map { this[it] }
    val otherColumns = columns - pivotColumns
    val outputColumnsData = otherColumns.map { ArrayList<Any?>() }.toMutableList()
    val keyColumnData = ArrayList<String>()
    val valueColumnData = ArrayList<Any?>()
    val classes = mutableSetOf<KClass<*>>()
    var hasNullValues = false
    (0 until nrow).forEach { row ->
        pivotColumns.forEach { pivotCol ->
            val value = pivotCol[row]
            if (valueColumn != null || value == true) {
                outputColumnsData.forEachIndexed { index, list ->
                    list.add(otherColumns[index][row])
                }
                keyColumnData.add(pivotCol.name)
                if (valueColumn != null) {
                    valueColumnData.add(value)
                    if (value == null)
                        hasNullValues = true
                    else classes.add(value.javaClass.kotlin)
                }
            }
        }
    }
    val resultColumns = outputColumnsData.mapIndexed { index, values ->
        val srcColumn = otherColumns[index]
        srcColumn.withValues(values, srcColumn.nullable)
    }.toMutableList()
    resultColumns.add(column(keyColumn, keyColumnData, false, String::class))
    if (valueColumn != null)
        resultColumns.add(column(valueColumn, valueColumnData, hasNullValues, classes.commonParent()))
    return dataFrameOf(resultColumns).typed()
}

fun <T> TypedDataFrame<T>.mergeRows(selector: ColumnsSelector<T>): TypedDataFrame<T> {

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

fun <T> TypedDataFrame<T>.splitRows(selector: ColumnSelector<T>): TypedDataFrame<T> {

    val nestedColumn = getColumn(selector)
    if (!nestedColumn.valueClass.isSubclassOf(List::class)) {
        throw Exception("Column ${nestedColumn.name} must contain values of type `List`")
    }
    val nestedColumnIndex = columns.indexOf(nestedColumn)
    val column = nestedColumn.cast<List<*>?>()
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

fun <T> TypedDataFrame<T>.mergeCols(newColumn: String, selector: ColumnsSelector<T>) = mergeCols(newColumn, { list -> list }, selector)

fun <T> TypedDataFrame<T>.mergeColsToString(newColumnName: String, separator: CharSequence = ", ", prefix: CharSequence = "", postfix: CharSequence = "", selector: ColumnsSelector<T>) = mergeCols(newColumnName, { list -> list.joinToString(separator = separator, prefix = prefix, postfix = postfix) }, selector)

internal inline fun <T, reified R> TypedDataFrame<T>.mergeCols(newColumn: String, crossinline transform: (List<Any?>) -> R, selector: ColumnsSelector<T>): TypedDataFrame<T> {
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

fun <T> TypedDataFrame<T>.splitCol(vararg names: String, selector: ColumnSelector<T>): TypedDataFrame<T> {
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