package org.jetbrains.kotlinx.dataframe.samples.api.multiple

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.excludeJoin
import org.jetbrains.kotlinx.dataframe.api.filterJoin
import org.jetbrains.kotlinx.dataframe.api.fullJoin
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class JoinSamples : DataFrameSampleHelper("join", "api") {

    @DataSchema
    interface DfAges {
        val age: Int
        val firstName: String
    }

    private val dfAges = dataFrameOf(
        "firstName" to listOf("Alice", "Bob", "Charlie"),
        "age" to listOf(14, 45, 20)
    ).cast<DfAges>()

    @DataSchema
    interface DfCities {
        val city: String
        val name: String
    }

    private val dfCities = dataFrameOf(
        "name" to listOf("Bob", "Alice", "Charlie"),
        "city" to listOf("London", "Dubai", "Moscow")
    ).cast<DfCities>()

    @DataSchema
    interface DfLeft {
        val age: kotlin.Int
        val city: kotlin.String
        val name: kotlin.String
    }

    private val dfLeft = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie", "Charlie"),
        "age" to listOf(15, 45, 20, 40),
        "city" to listOf("London", "Dubai", "Moscow", "Milan")
    ).cast<DfLeft>()

    @DataSchema
    interface DfRight {
        val city: kotlin.String?
        val isBusy: kotlin.Boolean
        val name: kotlin.String
    }

    private val dfRight = dataFrameOf(
        "name" to listOf("Charlie", "Bob", "Alice", "Charlie"),
        "isBusy" to listOf(true, false, true, true),
        "city" to listOf("Milan", "Tokyo", null, "Moscow")
    ).cast<DfRight>()

    @Test
    fun notebook_test_join_3() {
        // SampleStart
        dfAges
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_5() {
        // SampleStart
        dfCities
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_6() {
        // SampleStart
        dfAges.join(dfCities) { firstName match right.name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_8() {
        // SampleStart
        dfLeft
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_10() {
        // SampleStart
        dfRight
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_11() {
        // SampleStart
        dfLeft.join(dfRight) { name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_12() {
        // SampleStart
        dfLeft.join(dfRight)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_13() {
        // SampleStart
        dfLeft
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_14() {
        // SampleStart
        dfRight
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_15() {
        // SampleStart
        dfLeft.innerJoin(dfRight) { name and city }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_16() {
        // SampleStart
        dfLeft.filterJoin(dfRight) { name and city }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_17() {
        // SampleStart
        dfLeft.leftJoin(dfRight) { name and city }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_18() {
        // SampleStart
        dfLeft.rightJoin(dfRight) { name and city }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_19() {
        // SampleStart
        dfLeft.fullJoin(dfRight) { name and city }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_join_20() {
        // SampleStart
        dfLeft.excludeJoin(dfRight) { name and city }
            // SampleEnd
            .saveDfHtmlSample()
    }
}
