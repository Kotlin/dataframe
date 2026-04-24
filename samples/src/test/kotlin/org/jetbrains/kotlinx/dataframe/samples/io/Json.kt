@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.io.writeJson
import org.junit.Ignore
import org.junit.Test

class Json {

    @Ignore
    @Test
    fun readJson() {
        // SampleStart
        val df = DataFrame.readJson("example.json")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readJsonViaUrl() {
        // SampleStart
        val df = DataFrame.readJson("https://kotlin.github.io/dataframe/resources/example.json")
        // SampleEnd
    }

    @Ignore
    @Test
    fun writeJson() {
        val df = dataFrameOf("a" to columnOf(1))
        // SampleStart
        df.writeJson("example.json")
        // SampleEnd
    }
}
