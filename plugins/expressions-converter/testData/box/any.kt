package org.jetbrains.kotlinx.dataframe.explainer

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*

object PluginCallback {
    fun doAction(any: Any) {}
}

fun box(): String {
    val a: GroupBy<*, *> = dataFrameOf("a")(1).groupBy("a")
    a.also {
        PluginCallback.doAction(it)
    }
    return "OK"
}
