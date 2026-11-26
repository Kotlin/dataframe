package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.with
import kotlin.test.Test

// Testing that even excluding dependencies required API still works without exceptions
class PluginApiUsages {
    @Test
    fun convertWith() {
        dataFrameOf("a" to listOf("123"))
            .convert { col("a") }
            .with { it.toString() }
    }

    @Test
    fun convertAsColumn() {
        dataFrameOf("a" to listOf("123"))
            .convert { col("a") }
            .asColumn { col -> col.map { it.toString() } }
    }
}
