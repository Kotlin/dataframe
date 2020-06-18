package krangl.typed

import krangl.DataFrame
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

typealias RowSelector<T, R> = TypedDataFrameRow<T>.(TypedDataFrameRow<T>) -> R

typealias RowFilter<T> = RowSelector<T, Boolean>

class TypedColumnsFromDataRowBuilder<T>(val dataFrame: TypedDataFrame<T>) {
    internal val columns = mutableListOf<DataCol>()

    fun add(column: DataCol) = columns.add(column)

    inline fun <reified R> add(name: String, noinline expression: RowSelector<T,R>) = add(dataFrame.new(name, expression))

    inline infix fun <reified R> String.to(noinline expression: RowSelector<T,R>) = add(this, expression)

    inline operator fun <reified R> String.invoke(noinline expression: RowSelector<T,R>) = add(this, expression)
}

// add Column

operator fun TypedDataFrame<*>.plus(col: DataCol) = dataFrameOf(columns + col)
operator fun TypedDataFrame<*>.plus(col: Iterable<DataCol>) = dataFrameOf(columns + col)

inline fun <reified R, T> TypedDataFrame<T>.add(name: String, noinline expression: RowSelector<T,R>) =
        (this + new(name, expression))

inline fun <reified R, T> GroupedDataFrame<T>.add(name: String, noinline expression: RowSelector<T,R>) =
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

inline fun <T, reified R> TypedDataFrame<T>.groupBy(name: String = "key", noinline expression: RowSelector<T,R?>) =
        add(name, expression).groupBy(name)

// size

val DataFrame.size: DataFrameSize get() = DataFrameSize(ncol, nrow)

// toList

inline fun <reified C> TypedDataFrame<*>.toList() = DataFrameToListTypedStub(this, C::class)

fun TypedDataFrame<*>.toList(className: String) = DataFrameToListNamedStub(this, className)

inline fun <T, reified R:Number> TypedDataFrameRow<T>.diff(selector: RowSelector<T, R>) = when(R::class){
    Double::class -> prev?.let { (selector(this) as Double) - (selector(it) as Double) }  ?: .0
    Int::class -> prev?.let { (selector(this) as Int) - (selector(it) as Int) }  ?: 0
    Long::class -> prev?.let { (selector(this) as Long) - (selector(it) as Long) }  ?: 0
    else -> throw NotImplementedError()
}

fun <T> TypedDataFrameRow<T>.movingAverage(k: Int, selector: RowSelector<T, Number>) :Double {
    var count = 0
    return backwardIterable().take(k).sumByDouble {
        count++
        selector(it).toDouble()
    } / count
}

// merge

fun <T> Iterable<TypedDataFrame<T>>.merge() = merge<T>(toList())

fun commonParent(vararg classes: KClass<*>) = commonParents(classes.toList()).withMostSuperclasses()

fun Iterable<KClass<*>>.withMostSuperclasses() = maxBy {it.allSuperclasses.size}

fun commonParents(vararg classes: KClass<*>) = commonParents(classes.toList())

fun commonParents(classes: Iterable<KClass<*>>) =
        classes.distinct().let {
            when {
                it.size == 1 -> listOf(it[0]) // if there is only one class - return it
                else -> it.fold(null as (Set<KClass<*>>?)) { set, clazz -> // collect a set of all common superclasses from original classes
                    val superclasses = clazz.allSuperclasses + clazz
                    set?.intersect(superclasses) ?: superclasses.toSet()
                }!!.let {
                    it - it.flatMap { it.superclasses } // leave only 'leaf' classes, that are not super to some other class in a set
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