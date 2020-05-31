package krangl.typed

import java.lang.IllegalArgumentException

inline fun <reified T: Comparable<T>> List<T>.median(): Double {
    val sorted = sorted()
    val index = size/2
    return when(T::class){
        Double::class -> if (size % 2 == 0) (sorted[index-1] as Double + sorted[index] as Double) / 2.0 else sorted[index] as Double
        Int::class -> if (size % 2 == 0) (sorted[index-1] as Int + sorted[index] as Int) / 2.0 else (sorted[index] as Int).toDouble()
        Long::class -> if (size % 2 == 0) (sorted[index-1] as Long + sorted[index] as Long) / 2.0 else (sorted[index] as Long).toDouble()
        else -> throw IllegalArgumentException()
    }
}

inline fun <reified T:Number> List<T>.mean(): Double = when(T::class){
    Double::class -> (this as Iterable<Double>).sum() / size
    Int::class -> (this as Iterable<Int>).map { it.toDouble() }.sum() / size
    Long::class -> (this as Iterable<Long>).sum().toDouble() / size
    else -> throw IllegalArgumentException()
}