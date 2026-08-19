package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropNA
import org.jetbrains.kotlinx.dataframe.api.dropNaNs
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class DropNullsNansNa : DataFrameSampleHelper("drop", "api")  {
    val df = peopleDf

    @Test
    fun dropWhere_properties() {
        // SampleStart
        df.drop { weight == null || city == null }
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropWhere_strings() {
        // SampleStart
        df.drop { it["weight"] == null || it["city"] == null }
        // SampleEnd
    }

    @Test
    fun dropNulls() {
        // SampleStart
        df.dropNulls() // remove rows with null value in any column
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsWhereAllNull() {
        // SampleStart
        df.dropNulls(whereAllNull = true) // remove rows with null values in all columns
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsSelector() {
        // SampleStart
        df.dropNulls { city } // remove rows with null value in 'city' column
        // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun dropNullsSelectorSeveralCols() {
        // SampleStart
        df.dropNulls { city and weight } // remove rows with null value in 'city' OR 'weight' columns
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNullsWhereAllNullSelector() {
        // SampleStart
        df.dropNulls(whereAllNull = true) { city and weight } // remove rows with null value in 'city' AND 'weight' columns
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNaNs() {
        // SampleStart
        df.dropNaNs() // remove rows containing NaN in any column
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsWhereAllNaN() {
        // SampleStart
        df.dropNaNs(whereAllNaN = true) // remove rows with NaN in all columns
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsSelector() {
        // SampleStart
        df.dropNaNs { weight } // remove rows where 'weight' is NaN
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsSelectorSeveralCols() {
        // SampleStart
        df.dropNaNs { age and weight } // remove rows where either 'age' or 'weight' is NaN
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNaNsWhereAllNaNSelector() {
        // SampleStart
        df.dropNaNs(whereAllNaN = true) { age and weight } // remove rows where both 'age' and 'weight' are NaN
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNA() {
        // SampleStart
        df.dropNA() // remove rows containing null or NaN in any column
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNAWhereAllNA() {
        // SampleStart
        df.dropNA(whereAllNA = true) // remove rows with null or NaN in all columns
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNASelector() {
        // SampleStart
        df.dropNA { weight } // remove rows where 'weight' is null or NaN
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNASelectorSeveralCols() {
        // SampleStart
        df.dropNA { age and weight } // remove rows where either 'age' or 'weight' is null or NaN
        // SampleEnd
        .saveDfHtmlSample()
    }

    @Test
    fun dropNAWhereAllNASelector() {
        // SampleStart
        df.dropNA(whereAllNA = true) { age and weight } // remove rows where both 'age' and 'weight' are null or NaN
        // SampleEnd
        .saveDfHtmlSample()
    }
}
