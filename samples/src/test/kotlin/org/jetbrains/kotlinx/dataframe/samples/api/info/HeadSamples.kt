package org.jetbrains.kotlinx.dataframe.samples.api.info

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.head
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test
import kotlin.reflect.typeOf

class HeadSamples : DataFrameSampleHelper("head", "api") {

    private val df = peopleDf

    @Test
    fun headInput() {
        // SampleStart
        df
            // SampleEnd
            .also { it.city.type() shouldBe typeOf<String?>() }
            .saveDfHtmlSample()
    }

    @Test
    fun headDefault() {
        // SampleStart
        df.head()
            // SampleEnd
            .also {
                it.name.lastName.toList() shouldBe listOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley")
                // no `null` among the first five cities, so the page says `city` is `String` here
                it.city.type() shouldBe typeOf<String>()
            }
            .saveDfHtmlSample()
    }

    @Test
    fun headNumRows() {
        // SampleStart
        df.head(3)
            // SampleEnd
            .also { it.name.lastName.toList() shouldBe listOf("Cooper", "Dylan", "Daniels") }
            .saveDfHtmlSample()
    }
}
