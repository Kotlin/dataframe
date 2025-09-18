package org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop

import org.jetbrains.kotlinx.dataframe.api.associate
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class AssociateSamples : DataFrameSampleHelper("associate", "api/collectionsInterop") {

    private val df = peopleDf

    @Test
    fun notebook_test_associate_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_associate_2() {
        // SampleStart
        df.associate { "${name.firstName} ${name.lastName}" to age }
        // SampleEnd
    }
}
