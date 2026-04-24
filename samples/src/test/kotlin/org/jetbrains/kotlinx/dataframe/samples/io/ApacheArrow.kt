@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.junit.Ignore
import org.junit.Test

class ApacheArrow {

    @Ignore
    @Test
    fun readArrowFeather() {
        // SampleStart
        val df = DataFrame.readArrowFeather("example.feather")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readArrowFeatherViaUrl() {
        // SampleStart
        val df = DataFrame.readArrowFeather("https://kotlin.github.io/dataframe/resources/example.feather")
        // SampleEnd
    }
}
