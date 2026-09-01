package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.dropNaNs
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class DropNullsNansNaSamples : DataFrameSampleHelper("drop", "api") {
    @DataSchema
    interface DropData {
        val name: String?
        val age: Double?
        val weight: Double?
    }

    private val df: DataFrame<DropData> =
        dataFrameOf(
            "name" to columnOf<String?>("Alice", "Bob", "Charlie", null, null),
            "age" to columnOf<Double?>(15.0, Double.NaN, null, null, Double.NaN),
            "weight" to columnOf<Double?>(54.0, 87.0, null, null, Double.NaN),
        ).cast()

    @Test
    fun dropDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropWhere_properties() {
        // SampleStart
        df.drop { weight == null || name == null }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropWhere_strings() {
        // SampleStart
        df.drop { it["weight"] == null || it["name"] == null }
        // SampleEnd
    }

    @Test
    fun dropDataColumnByPredicate() {
        // SampleStart
        df.weight.drop { it != null && it < 60 }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNulls() {
        // SampleStart
        // remove rows with null value in any column
        df.dropNulls()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsWhereAllNull() {
        // SampleStart
        // remove rows with null values in all columns
        df.dropNulls(whereAllNull = true)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsSelector() {
        // SampleStart
        // remove rows with null value in 'name' column
        df.dropNulls { name }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsSelectorSeveralCols() {
        // SampleStart
        // remove rows with null value in 'name' OR 'weight' columns
        df.dropNulls { name and weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsWhereAllNullSelector() {
        // SampleStart
        // remove rows with nulls in both 'name' and 'weight' columns
        df.dropNulls(whereAllNull = true) { name and weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsDataColumn() {
        // SampleStart
        df.weight.dropNulls()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNaNs() {
        // SampleStart
        // remove rows containing NaN in any column
        df.dropNaNs()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsWhereAllNaN() {
        // SampleStart
        // remove rows with NaN in all columns of type `Double?`
        df.dropNaNs(whereAllNaN = true) { colsOf<Double?>() }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsSelector() {
        // SampleStart
        // remove rows where 'weight' is NaN
        df.dropNaNs { weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsSelectorSeveralCols() {
        // SampleStart
        // remove rows where either 'age' or 'weight' is NaN
        df.dropNaNs { age and weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsWhereAllNaNSelector() {
        // SampleStart
        // remove rows where both 'age' and 'weight' are NaN
        df.dropNaNs(whereAllNaN = true) { age and weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsDataColumn() {
        // SampleStart
        df.weight.dropNaNs()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNA() {
        // SampleStart
        // remove rows containing null or NaN in any column
        df.dropNA()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNAWhereAllNA() {
        // SampleStart
        // remove rows with null or NaN in all columns
        df.dropNA(whereAllNA = true)
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNASelector() {
        // SampleStart
        // remove rows where 'weight' is null or NaN
        df.dropNA { weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNASelectorSeveralCols() {
        // SampleStart
        // remove rows where either 'age' or 'weight' is null or NaN
        df.dropNA { age and weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNAWhereAllNASelector() {
        // SampleStart
        // remove rows where both 'age' and 'weight' are null or NaN
        df.dropNA(whereAllNA = true) { age and weight }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNADataColumn() {
        // SampleStart
        df.weight.dropNA()
            // SampleEnd
            .saveDfHtmlSample()
    }
}
