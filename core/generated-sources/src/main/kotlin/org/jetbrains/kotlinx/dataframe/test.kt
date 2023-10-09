package org.jetbrains.kotlinx.dataframe

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import java.io.Serializable
import kotlin.reflect.typeOf

public fun main() {
    println(
        listOf(
            renderType(typeOf<Comparable<*>>()),
            renderType(typeOf<Comparable<Any>>()),
            renderType(typeOf<Comparable<Any?>>()),
        )
    )


//    @Language("json")
//    val json = """
//    [
//        { "a": 1 },
//        { "a": 2 },
//        { "a": "hoi" }
//    ]
//"""
//
//    val df = DataFrame.readJsonStr(json)
//
//    df.print(columnTypes = true)
}
