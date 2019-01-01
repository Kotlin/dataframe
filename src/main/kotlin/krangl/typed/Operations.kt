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

class ValuesList<T>(val list: List<T>)

class GroupAggregateBuilder<T>(private val dataFrame: GroupedDataFrame<T>) {
    internal val columns = mutableListOf<DataCol>()

    fun <T> List<T>.wrap() = ValuesList(this)

    val groups by lazy { dataFrame.groups() }

    fun add(column: DataCol) = columns.add(column)

    fun <N:Comparable<N>> minBy(selector: RowSelector<T, N>) =
            map { minBy(selector)!! }

    fun <N:Comparable<N>> maxBy(selector: RowSelector<T, N>) =
            map { maxBy(selector)!! }

    fun <R> ValuesList<TypedDataFrameRow<T>>.map(selector: RowSelector<T, R>) =
            list.map(selector).wrap()

    fun <R> TypedDataFrame<T>.map(selector: RowSelector<T, R>) =
            rows.map(selector)

    val count by lazy { groups.map{ it.nrow }.wrap() }

    fun count(filter: RowFilter<T>) = groups.map {it.count(filter)}.wrap()

    inline fun <reified R: Comparable<R>> median(noinline selector: RowSelector<T,R>) = groups.map { it.map(selector).median() }.wrap()

    inline fun <reified R:Number> mean(noinline selector: RowSelector<T,R>) = groups.map { it.map(selector).mean() }.wrap()

    inline fun <reified R:Comparable<R>> min(noinline selector: RowSelector<T,R>) = groups.map { it.map(selector).min()!! }.wrap()

    inline fun <reified R:Comparable<R>> max(noinline selector: RowSelector<T,R>) = groups.map { it.map(selector).max()!! }.wrap()

    inline infix fun <reified R> ValuesList<R>.into(columnName: String) = add(newColumn(columnName, list))

    fun <R> map(selector: TypedDataFrame<T>.() -> R) = groups.map(selector).wrap()

    inline fun <reified R> add(name: String, noinline expression: TypedDataFrame<T>.() -> R?) = add(newColumn(name, groups.map { expression(it) }))

    inline infix fun <reified R> String.to(noinline expression: TypedDataFrame<T>.() -> R?) = add(this, expression)

    inline operator fun <reified R> String.invoke(noinline expression: TypedDataFrame<T>.() -> R?) = add(this, expression)

    infix operator fun String.invoke(column: DataCol) = add(column.rename(this))
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


inline fun <T, reified R> GroupedDataFrame<T>.map(columnName: String = "map", noinline selector: TypedDataFrame<T>.() -> R) = aggregate { map(selector) into columnName}


// group by

inline fun <reified T, D> TypedDataFrame<D>.groupBy(name: String = "key", noinline expression: TypedDataFrameRow<D>.() -> T?) =
        add(name, expression).df.groupBy(name).typed<D>()

inline fun <T, reified R: Comparable<R>> GroupedDataFrame<T>.median(columnName: String = "median", noinline selector: RowSelector<T,R>) = aggregate { median(selector) into columnName}
inline fun <T, reified R: Number> GroupedDataFrame<T>.mean(columnName: String = "mean", noinline selector: RowSelector<T,R>) = aggregate { mean(selector) into columnName}
inline fun <T, reified R: Comparable<R>> GroupedDataFrame<T>.min(columnName: String = "min", noinline selector: RowSelector<T,R>) = aggregate { min(selector) into columnName}
inline fun <T, reified R: Comparable<R>> GroupedDataFrame<T>.max(columnName: String = "max", noinline selector: RowSelector<T,R>) = aggregate { max(selector) into columnName}

// summarize

fun <T> GroupedDataFrame<T>.aggregate(body: GroupAggregateBuilder<T>.() -> Unit): TypedDataFrame<T> {
    val builder = GroupAggregateBuilder(this)
    body(builder)
    return (this.groupedBy() + builder.columns).typed<T>()
}

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
