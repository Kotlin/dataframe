package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.singleOrNull
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class SingleSamples : DataFrameSampleHelper("single", "api") {
    val df = peopleDf

    @Test
    fun singleDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun single_properties() {
        // SampleStart
        df
            .filter { age == 45 } // one row is left after filtering
            .single()
            // SampleEnd
            .also { it.name.lastName shouldBe "Dylan" }
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun single_strings() {
        // SampleStart
        df
            .filter { "age"<Int>() == 45 } // one row is left after filtering
            .single()
            // SampleEnd
            .also { it["age"] shouldBe 45 }
    }

    @Test
    fun singleCondition_properties() {
        // SampleStart
        df.single { age == 45 } // only Bob Dylan is 45
            // SampleEnd
            .also { it.name.lastName shouldBe "Dylan" }
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun singleCondition_strings() {
        // SampleStart
        df.single { "age"<Int>() == 45 } // only Bob Dylan is 45
            // SampleEnd
            .also { it["age"] shouldBe 45 }
    }

    @Test
    fun singleOrNull_properties() {
        // SampleStart
        val noOne = df
            .filter { age > 50 } // df is empty after filtering
            .singleOrNull() // returns null
        // SampleEnd
        noOne shouldBe null
    }

    @Test
    fun singleOrNull_strings() {
        // SampleStart
        val noOne = df
            .filter { "age"<Int>() > 50 } // df is empty after filtering
            .singleOrNull() // returns null
        // SampleEnd
        noOne shouldBe null
    }

    @Test
    fun singleOrNullCondition_properties() {
        // SampleStart
        val noOne = df.singleOrNull { age == 30 } // returns null: three people are 30
        // SampleEnd
        noOne shouldBe null
    }

    @Test
    fun singleOrNullCondition_strings() {
        // SampleStart
        val noOne = df.singleOrNull { "age"<Int>() == 30 } // returns null: three people are 30
        // SampleEnd
        noOne shouldBe null
    }
}
