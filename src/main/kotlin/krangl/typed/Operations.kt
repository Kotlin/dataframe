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

operator fun DataFrame.plus(col: DataCol) = dataFrameOf(cols + col)
operator fun DataFrame.plus(col: Iterable<DataCol>) = dataFrameOf(cols + col)

inline fun <reified T, D> TypedDataFrame<D>.add(name: String, noinline expression: TypedDataFrameRow<D>.() -> T?) =
        (this + new(name, expression))

inline fun <reified T, D> GroupedDataFrame<D>.add(name: String, noinline expression: TypedDataFrameRow<D>.() -> T?) =
        modify { add(name, expression) }

inline fun <reified T> DataFrame.addColumn(name: String, values: List<T?>) =
        this + newColumn(name, values)

fun DataFrame.addColumn(name: String, col: DataCol) =
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

operator fun <T> TypedDataFrame<T>.get(range: IntRange) =
    df.filter { rowNumber.map{range.contains(it-1)}.toBooleanArray() }.typed<T>()

// size

val DataFrame.size: DataFrameSize get() = DataFrameSize(ncol, nrow)

// toList

inline fun <reified C> TypedDataFrame<*>.toList() = df.toList<C>()
inline fun <reified C> DataFrame.toList() = DataFrameToListTypedStub(this, C::class)

fun DataFrame.toList(className: String) = DataFrameToListNamedStub(this, className)
fun TypedDataFrame<*>.toList(className: String) = df.toList(className)

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

fun <T> Iterable<TypedDataFrame<T>>.bindRows() = map { it.df }.bindRows().typed<T>()
