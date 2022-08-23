package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.take
import org.junit.Test

class Explode {
    @Test
    fun test1() {
        val ints by columnOf(1, 2, 3)
        val group by columnOf(ints)
        val df = dataFrameOf(group)

        val frameCol by columnOf(df, df.take(0))
        val names by columnOf("a", "b")
        val df1 = dataFrameOf(names, frameCol)

        println("Runtime before")
        df1.schema().print()
        println()
        df1.pluginSchema().print()

        val selector = df1.makeSelector { dfs { it.isList() || it.isFrameColumn() } }

        val runtime = df1.explode(selector = selector)

        println("Runtime")
        runtime.schema().print()
        runtime.print()

        val columns = selector.toColumnPath(df1)
        val compile = df1.pluginSchema().explodeImpl(dropEmpty = true, columns)

        println("Compile")
        compile.print()
    }
}
