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

class GroupAggregateBuilder<T>(private val dataFrame: TypedDataFrame<T>) {
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
operator fun <T> TypedDataFrame<T>.plus(col: DataCol) = (df + col).typed<T>()
operator fun <T> TypedDataFrame<T>.plus(col: Iterable<DataCol>) = dataFrameOf(df.cols + col).typed<T>()

inline fun <reified T, D> TypedDataFrame<D>.add(name: String, noinline expression: TypedDataFrameRow<D>.() -> T?) =
        (this + new(name, expression))

inline fun <reified T> DataFrame.addColumn(name: String, values: List<T?>) =
        this + newColumn(name, values)

fun DataFrame.addColumn(name: String, col: DataCol) =
        this + col.rename(name)

fun <T> TypedDataFrame<T>.add(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): TypedDataFrame<T> {
    val builder = TypedColumnsFromDataRowBuilder(this)
    body(builder)
    return dataFrameOf(columns + builder.columns).typed()
}

fun <T> TypedDataFrame<T>.addRowNumber(columnName: String = "id"): TypedDataFrame<T> {
    val col = IntCol(columnName, IntArray(nrow){it})
    return dataFrameOf(columns + col).typed()
}

operator fun <T> TypedDataFrame<T>.plus(stub: AddRowNumberStub) = addRowNumber(stub.columnName)

fun rowNumber(columnName: String = "id") = AddRowNumberStub(columnName)

data class AddRowNumberStub(val columnName: String)

operator fun <T> TypedDataFrame<T>.plus(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit) = add(body)

// map

fun <T> TypedDataFrame<T>.map(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): DataFrame {
    val builder = TypedColumnsFromDataRowBuilder(this)
    body(builder)
    return dataFrameOf(builder.columns)
}

// filter

fun <T> TypedDataFrame<T>.filter(predicate: TypedDataFrameRow<T>.(TypedDataFrameRow<T>) -> Boolean) =
        df.filter {
            rowWise { getRow ->
                BooleanArray(nrow) { index ->
                    val row = getRow(index)!!
                    predicate(row, row)
                }
            }
        }.typed<T>()

fun <T> TypedDataFrame<T>.filterNotNull(columns: ColumnSelector<T>) = getColumns(columns).let { cols ->
    filter { row -> cols.all { col -> row[col.name] != null } }
}

fun <T> TypedDataFrame<T>.filterNotNullAny(columns: ColumnSelector<T>) = getColumns(columns).let { cols ->
    filter { row -> cols.any { col -> row[col.name] != null } }
}

fun <T> TypedDataFrame<T>.filterNotNullAny() = filter { fields.any { it.second != null } }

fun <T> TypedDataFrame<T>.filterNotNull() = filter { fields.all { it.second != null } }

fun <T, D : Comparable<D>> TypedDataFrame<T>.maxBy(selector: RowSelector<T, D>) =
        rows.maxBy(selector)

fun <T, D : Comparable<D>> TypedDataFrame<T>.minBy(selector: RowSelector<T, D>) =
        rows.minBy(selector)

// group by

inline fun <reified T, D> TypedDataFrame<D>.groupBy(name: String = "key", noinline expression: TypedDataFrameRow<D>.() -> T?) =
        add(name, expression).df.groupBy(name).typed<D>()

// summarize

fun <T> TypedDataFrame<T>.aggregate(body: GroupAggregateBuilder<T>.() -> Unit): TypedDataFrame<T> {
    val builder = GroupAggregateBuilder(this)
    body(builder)
    return (this.groupedBy() + builder.columns).typed<T>()
}

// count

fun <T> TypedDataFrame<T>.count(predicate: RowFilter<T>) = rows.count(predicate)

fun <T> TypedDataFrame<T>.count() = df.count().typed<T>()

// take

fun <T> TypedDataFrame<T>.first() = rows.first()

fun <T> TypedDataFrame<T>.firstOrNull() = rows.firstOrNull()

// TODO: optimize (don't iterate through the whole data frame)
fun <T> TypedDataFrame<T>.last() = rows.last()

fun <T> TypedDataFrame<T>.lastOrNull() = rows.lastOrNull()

fun <T> TypedDataFrame<T>.take(numRows: Int = 5) = df.take(numRows).typed<T>()

fun <T> TypedDataFrame<T>.skip(numRows: Int = 5) = takeLast(nrow - numRows)

fun <T> TypedDataFrame<T>.takeLast(numRows: Int) = df.takeLast(numRows).typed<T>()

fun <T> TypedDataFrame<T>.skipLast(numRows: Int = 5) = take(nrow - numRows)

fun <T> TypedDataFrame<T>.head(numRows: Int = 5) = take(numRows)

fun <T> TypedDataFrame<T>.tail(numRows: Int = 5) = takeLast(numRows)

operator fun <T> TypedDataFrame<T>.get(range: IntRange) =
    df.filter { rowNumber.map{range.contains(it-1)}.toBooleanArray() }.typed<T>()

// size

data class DataFrameSize(val ncol: Int, val nrow: Int){
    override fun toString() = "$nrow x $ncol"
}

val TypedDataFrame<*>.size get() = DataFrameSize(ncol, nrow)

val DataFrame.size: Int get() = nrow

// toList

inline fun <reified C> TypedDataFrame<*>.toList() = df.toList<C>()
inline fun <reified C> DataFrame.toList() = DataFrameToListTypedStub(this, C::class)

fun DataFrame.toList(className: String) = DataFrameToListNamedStub(this, className)
fun TypedDataFrame<*>.toList(className: String) = df.toList(className)