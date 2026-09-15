package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.singleOrNull
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
    fun single() {
        // SampleStart
        val dylan = df
            .filter { age == 45 } // one row is left after filtering
            .single()
        // SampleEnd
        dylan.name.lastName shouldBe "Dylan"
    }

    @Test
    fun singleCondition_properties() {
        // SampleStart
        val dylan = df.single { age == 45 } // only Bob Dylan is 45
        // SampleEnd
        dylan.name.lastName shouldBe "Dylan"
    }

    @Test
    fun singleCondition_strings() {
        // SampleStart
        val dylan = df.single { "age"<Int>() == 45 } // only Bob Dylan is 45
        // SampleEnd
        dylan["age"] shouldBe 45
    }

    @Test
    fun singleOrNull() {
        // SampleStart
        val noOne = df
            .filter { age > 50 } // df is empty after filtering
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
