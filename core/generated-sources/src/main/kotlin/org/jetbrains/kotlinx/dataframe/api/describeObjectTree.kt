package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

public inline fun <reified T : Any> Iterable<T>.describeObjectTree(): DataFrame<*> {
    return describeObjectTree(T::class)
}

@PublishedApi
internal fun <T : Any> Iterable<T>.describeObjectTree(klass: KClass<T>): DataFrame<*> {
    return TODO()
}

public inline fun <reified T : Any> describeObjectTree(maxDepth: Int): DataFrame<Any?> {
    return describeObjectTree(T::class, maxDepth)
}

@PublishedApi
internal fun <T : Any> describeObjectTree(klass: KClass<T>, maxDepth: Int): DataFrame<Any?> {
    if (maxDepth <= 0) return emptyDataFrame()
    val props = try {
        klass.memberProperties
    } catch (exception: Error) {
        println(klass)
        emptyList()
    }
//    val properties = props.toColumn("properties")
    val df: DataFrame<Any?> = props.toDataFrame {
        "property" from { it }
        "package" from { klass.qualifiedName?.substringBeforeLast(".") }
        "name" from { it.name }
        "returnType" from { it.returnType }
        "exclude" from { klass.simpleName?.let { simpleName -> "exclude($simpleName::${it.name})" } }
    }

    val dataFrames = mutableListOf(df)
    props.mapNotNullTo(dataFrames) {
        val klass1 = it.returnType.classifier as? KClass<*>
        klass1?.let { describeObjectTree(klass1, maxDepth - 1) }
    }
    return dataFrames.concat()
}

public fun main() {
    println(describeObjectTree<DataFrame<*>>(2))
}
