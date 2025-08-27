package org.jetbrains.kotlinx.dataframe.samples.api.utils

import org.jetbrains.kotlinx.kandy.letsplot.*
import org.jetbrains.kotlinx.kandy.letsplot.export.*
import org.jetbrains.kotlinx.kandy.letsplot.feature.*
import org.jetbrains.kotlinx.kandy.letsplot.layers.*
import org.jetbrains.kotlinx.kandy.letsplot.multiplot.*
import org.jetbrains.kotlinx.kandy.letsplot.multiplot.facet.*
import org.jetbrains.kotlinx.kandy.letsplot.translator.*
import org.jetbrains.kotlinx.kandy.letsplot.scales.*
import org.jetbrains.kotlinx.kandy.letsplot.scales.guide.*
import org.jetbrains.kotlinx.kandy.letsplot.style.*
import org.jetbrains.kotlinx.kandy.letsplot.tooltips.*
import org.jetbrains.kotlinx.kandy.letsplot.settings.*
import org.jetbrains.kotlinx.kandy.letsplot.settings.font.*
import org.jetbrains.kotlinx.kandy.letsplot.samples.SampleHelper
import org.jetbrains.kotlinx.kandy.dsl.*
import org.jetbrains.kotlinx.kandy.util.color.*
import org.jetbrains.kotlinx.kandy.util.context.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.columns.*
import org.jetbrains.kotlinx.dataframe.dataTypes.*
import org.junit.Test

class Notebook_test_anyTests : SampleHelper("any") {

    @DataSchema
    interface SimplePerson {
        val name: String
        val age: Int
    }

    private val df = dataFrameOf(
        "name" to listOf("Alice", "Bob"),
        "age" to listOf(15, 20)
    ).cast<SimplePerson>()

    @Test
    fun notebook_test_any_3() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_any_4() {
        // SampleStart
        df.any { age > 21 }
        // SampleEnd
    }

    @Test
    fun notebook_test_any_5() {
        // SampleStart
        df.any { age == 15 && name == "Alice" }
        // SampleEnd
    }

    @Test
    fun notebook_test_any_6() {
        // SampleStart
        df.name.toDataFrame()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_any_7() {
        // SampleStart
        df.name.any { it.first().isUpperCase() }
        // SampleEnd
    }
}
