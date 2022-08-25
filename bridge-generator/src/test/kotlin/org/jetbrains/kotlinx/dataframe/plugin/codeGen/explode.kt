package org.jetbrains.kotlinx.dataframe.plugin.codeGen

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.plugin.model.Function
import org.jetbrains.kotlinx.dataframe.plugin.model.Parameter
import org.jetbrains.kotlinx.dataframe.plugin.model.Type
import org.junit.jupiter.api.Test

val explode = dataFrameOf(
    Function("DataFrame<T>", "explode", Type("DataFrame<T>", false), listOf(
        Parameter("dropEmpty", Type("Boolean", false), "true"),
        Parameter("selector", Type("ColumnsSelector<T, *>", false), "receiver.columns()"),
    ))
)

class Explode {
    @Test
    fun `explode API`() {
        explode.generateAll("explode_bridges.json")
    }
}
