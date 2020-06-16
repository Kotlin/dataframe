package krangl.typed

import krangl.DataFrame
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

typealias RowSelector<T, R> = TypedDataFrameRow<T>.() -> R

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
        with(TypedColumnsFromDataRowBuilder(this)) {
            body(this)
            dataFrameOf(this@add.columns + columns).typed<T>()
        }

fun rowNumber(columnName: String = "id") = AddRowNumberStub(columnName)

data class AddRowNumberStub(val columnName: String)

operator fun <T> TypedDataFrame<T>.plus(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit) = add(body)

// map

fun <T> TypedDataFrame<T>.map(body: TypedColumnsFromDataRowBuilder<T>.() -> Unit): UntypedDataFrame {
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

fun <T> Iterable<TypedDataFrame<T>>.merge() = merge<T>(toList())

fun commonParent(vararg classes: KClass<*>) = commonParents(classes.toList()).withMostSuperclasses()

fun Iterable<KClass<*>>.withMostSuperclasses() = maxBy {it.allSuperclasses.size}

fun commonParents(vararg classes: KClass<*>) = commonParents(classes.toList())

fun commonParents(classes: Iterable<KClass<*>>) =
        classes.distinct().let {
            when {
                it.size == 1 -> listOf(it[0])
                else -> it.fold(null as (Set<KClass<*>>?)) { set, clazz ->
                    set?.intersect(clazz.allSuperclasses + clazz) ?: (clazz.allSuperclasses + clazz).toSet()
                }!!.let {
                    it - it.flatMap { it.superclasses }
                }.toList()
            }
        }

fun <T> merge(dataFrames: List<TypedDataFrame<*>>) = dataFrames
        .fold(emptyList<String>()) { acc, df -> acc + (df.columnNames() - acc) } // collect column names preserving order
        .map { name ->
            val list = mutableListOf<Any?>()
            var nullable = false
            val classes = mutableSetOf<KClass<*>>()

            dataFrames.forEach {
                val column = it.tryGetColumn(name)
                if (column != null) {
                    nullable = nullable || column.nullable
                    classes.add(column.valueClass)
                    list.addAll(column.values)
                } else {
                    if (it.nrow > 0) nullable = true
                    for (row in (0 until it.nrow)) {
                        list.add(null)
                    }
                }
            }
            val newClass = commonParents(classes).firstOrNull() ?: Any::class

            TypedDataCol(list, nullable, name, newClass)
        }.let { dataFrameOf(it).typed<T>() }

operator fun <T> TypedDataFrame<T>.plus(other: TypedDataFrame<T>) = merge<T>(listOf(this, other))

fun TypedDataFrame<*>.union(vararg other: TypedDataFrame<*>) = merge<Unit>(listOf(this) + other.toList())

// Column operations

fun <T : Comparable<T>> TypedColData<T?>.min() = values.asSequence().filterNotNull().min()
fun <T : Comparable<T>> TypedColData<T?>.max() = values.asSequence().filterNotNull().max()