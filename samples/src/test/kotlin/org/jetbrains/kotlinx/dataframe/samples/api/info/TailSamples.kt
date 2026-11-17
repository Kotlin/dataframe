package org.jetbrains.kotlinx.dataframe.samples.api.info

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.tail
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test
import kotlin.reflect.typeOf

class TailSamples : DataFrameSampleHelper("tail", "api") {

    private val df = peopleDf

    @Test
    fun notebook_test_tail_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_tail_2() {
        // SampleStart
        df.tail()
            // SampleEnd
            .also {
                it.name.lastName.toList() shouldBe listOf("Wolf", "Byrd", "Smith", "Brown", "Johnson")
                // one of the last five cities is `null`, so the page says `city` stays `String?` here
                it.city.type() shouldBe typeOf<String?>()
            }
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_tail_3() {
        // SampleStart
        df.tail(2)
            // SampleEnd
            .also {
                it.name.lastName.toList() shouldBe listOf("Brown", "Johnson")
                // no `null` among the last two cities, so the page says `city` is `String` here
                it.city.type() shouldBe typeOf<String>()
            }
            .saveDfHtmlSample()
    }
}
