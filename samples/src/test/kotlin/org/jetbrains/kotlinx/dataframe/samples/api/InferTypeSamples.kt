package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.inferType
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

/** Samples for the `inferType` operation: shown on its documentation page and in its KDoc via `@sample`. */
@Suppress("ktlint:standard:argument-list-wrapping")
class InferTypeSamples : DataFrameSampleHelper("inferType", "api") {

    @DataSchema
    interface Measurement {
        val source: String
        val value: Any?
    }

    private val df: DataFrame<Measurement> = dataFrameOf(
        "source" to columnOf("sensor", "manual", "sensor"),
        "value" to DataColumn.createValueColumn("value", listOf(1, null, 3), typeOf<Any?>()),
    ).cast()

    @Test
    fun inferTypeInput() {
        // SampleStart
        val shownDf = dataFrameOf(
            "source" to columnOf("sensor", "manual", "sensor"),
            "value" to DataColumn.createValueColumn("value", listOf(1, null, 3), typeOf<Any?>()),
        )
        // SampleEnd
        shownDf shouldBe df
        shownDf.saveDfHtmlSample()
    }

    @Test
    fun inferTypeNarrowsColumnAfterFilter() {
        // SampleStart
        val items = columnOf<Comparable<*>>("Alice", 1, 3.5)
        val strings = items.filter { it is String }
        val inferred = strings.inferType()
        // SampleEnd
        "before: ${strings.type()}\nafter: ${inferred.type()}".saveTextSample()
        inferred.values().toList() shouldBe listOf("Alice")
    }

    @Test
    fun inferTypeDoesNotParseStrings() {
        // SampleStart
        val numbersAsText = DataColumn.createValueColumn("numbersAsText", listOf("1", "2", "3"), typeOf<Any?>())
        val inferred = numbersAsText.inferType()
        // SampleEnd
        "type: ${inferred.type()}".saveTextSample()
        inferred.type() shouldBe typeOf<String>()
        inferred.values().toList() shouldBe listOf("1", "2", "3")
        inferred.saveDfHtmlSample()
    }

    @Test
    fun inferTypeMixedValues() {
        // SampleStart
        val mixed = DataColumn.createValueColumn("mixed", listOf<Any?>(1, 2.0), typeOf<Any?>())
        val inferred = mixed.inferType()
        // SampleEnd
        "type: ${inferred.type()}".saveTextSample()
        inferred.type() shouldBe typeOf<Number>()
        // the values are not unified: Int stays Int, Double stays Double
        inferred.values().map { it?.javaClass?.name } shouldBe listOf("java.lang.Integer", "java.lang.Double")
    }

    @Test
    fun inferTypeAllColumns() {
        // SampleStart
        val inferred = df.inferType()
        // SampleEnd
        "value: ${inferred["value"].type()}".saveTextSample()
        inferred["value"].type() shouldBe typeOf<Int?>()
        inferred.saveDfHtmlSample()
    }

    @Test
    fun inferTypeSelectedColumns_properties() {
        // SampleStart
        val inferred = df.inferType { value }
        // SampleEnd
        "value: ${inferred["value"].type()}".saveTextSample()
        inferred["value"].type() shouldBe typeOf<Int?>()
        inferred["source"].type() shouldBe typeOf<String>()
        inferred.saveDfHtmlSample()
    }

    @Test
    fun inferTypeSelectedColumns_strings() {
        // SampleStart
        val inferred = df.inferType("value")
        // SampleEnd
        inferred["value"].type() shouldBe typeOf<Int?>()
        inferred["source"].type() shouldBe typeOf<String>()
    }

    @Test
    fun inferTypeSelectByType() {
        // SampleStart
        val refined = df.inferType("value")
        val selected = refined.select { colsOf<Int?>() }
        // SampleEnd
        "value: ${selected["value"].type()}".saveTextSample()
        selected.columnNames() shouldBe listOf("value")
        selected.saveDfHtmlSample()
    }
}
