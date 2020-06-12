package krangl.typed

import krangl.*

typealias RowSelector<T,R> = TypedDataFrameRow<T>.()->R

typealias RowFilter<T> = RowSelector<T, Boolean>

class TypedColumnsFromDataRowBuilder<T>(val dataFrame: TypedDataFrame<T>) {
    internal val columns = mutableListOf<DataCol>()

    fun add(column: DataCol) = columns.add(column)

    inline fun <reified R> add(name: String, noinline expression: TypedDataFrameRow<T>.() -> R?) = add(dataFrame.new(name, expression))

    inline infix fun <reified R> String.to(noinline expression: TypedDataFrameRow<T>.() -> R?) = add(this, expression)

    inline operator fun <reified R> String.invoke(noinline expression: TypedDataFrameRow<T>.() -> R?) = add(this, expression)
}

// add Column

operator fun TypedDataFrame<*>.plus(col: DataCol) = dataFrameOf(columns + col)
operator fun TypedDataFrame<*>.plus(col: Iterable<DataCol>) = dataFrameOf(columns + col)

inline fun <reified T, D> TypedDataFrame<D>.add(name: String, noinline expression: TypedDataFrameRow<D>.() -> T?) =
        (this + new(name, expression))

inline fun <reified T, D> GroupedDataFrame<D>.add(name: String, noinline expression: TypedDataFrameRow<D>.() -> T?) =
        modify { add(name, expression) }

inline fun <reified T> TypedDataFrame<*>.addColumn(name: String, values: List<T?>) =
        this + newColumn(name, values)

fun TypedDataFrame<*>.addColumn(name: String, col: DataCol) =
        this + col.rename(name)

fun <T> TypedDataFrame<T>.add(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit) =
    with(TypedColumnsFromDataRowBuilder(this)){
        body(this)
        dataFrameOf(this@add.columns + columns).typed<T>()
    }

fun rowNumber(columnName: String = "id") = AddRowNumberStub(columnName)

data class AddRowNumberStub(val columnName: String)

operator fun <T> TypedDataFrame<T>.plus(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit) = add(body)

// map

fun <T> TypedDataFrame<T>.map(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): DataFrame {
    val builder = TypedColumnsFromDataRowBuilder(this)
    body(builder)
    return dataFrameOf(builder.columns)
}


// group by

inline fun <reified T, D> TypedDataFrame<D>.groupBy(name: String = "key", noinline expression: TypedDataFrameRow<D>.() -> T?) =
        add(name, expression).groupBy(name)

// size

val DataFrame.size: DataFrameSize get() = DataFrameSize(ncol, nrow)

// toList

inline fun <reified C> TypedDataFrame<*>.toList() = DataFrameToListTypedStub(this, C::class)

fun TypedDataFrame<*>.toList(className: String) = DataFrameToListNamedStub(this, className)

fun <T> TypedDataFrameRow<T>.movingAverage(k: Int, selector: RowSelector<T, Double>): Double {
    var sum = .0
    var i = 0
    var r = this ?: null
    while (i < k && r != null) {
        sum += selector(r)
        r = r.prev
        i++
    }
    return sum / i
}

// merge

fun <T> Iterable<TypedDataFrame<T>>.bindRows() = bindRows<T>(toList())

private fun bindColData(dataFrames: List<TypedDataFrame<*>>, colName: String): Array<*> {
    val totalRows = dataFrames.map { it.nrow }.sum()

    val arrayList = Array<Any?>(totalRows, { 0 })

    var iter = 0

    dataFrames.forEach {
        if (it.columnNames().contains(colName)) {
            it[colName].anyValues.forEach {
                arrayList[iter++] = it
            }
        } else {
            // column is missing in `it`
            for (row in (0 until it.nrow)) {
                arrayList[iter++] = null
            }
        }
    }

    return arrayList
}

fun <T> bindRows(dataFrames: List<TypedDataFrame<*>>): TypedDataFrame<*> { // add options about NA-fill over non-overlapping columns
    val bindCols = mutableListOf<DataCol>()

    val colNames = dataFrames
            .map { it.columnNames() }
            .foldRight(emptyList<String>()) { acc, right ->
                acc + right.minus(acc)
            }

    for (colName in colNames) {
        val colDataCombined: Array<*> = bindColData(dataFrames.toList(), colName)

        val frames = dataFrames.mapNotNull { it.tryGetColumn(colName) }
        when (frames.first().toSrc()) {
            is DoubleCol -> DoubleCol(colName, colDataCombined.map { it as Double? })
            is IntCol -> IntCol(colName, colDataCombined.map { it as Int? })
            is LongCol -> LongCol(colName, colDataCombined.map { it as Long? })
            is StringCol -> StringCol(colName, colDataCombined.map { it as String? })
            is BooleanCol -> BooleanCol(colName, colDataCombined.map { it as Boolean? })
            is AnyCol -> AnyCol(colName, colDataCombined.toList())
            else -> throw UnsupportedOperationException()
        }.apply { bindCols.add(typed()) }
    }

    return dataFrameOf(bindCols).typed<T>()
}