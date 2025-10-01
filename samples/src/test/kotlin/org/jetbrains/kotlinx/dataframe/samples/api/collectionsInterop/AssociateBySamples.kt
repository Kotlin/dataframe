package org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop

import org.jetbrains.kotlinx.dataframe.api.associateBy
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class AssociateBySamples : DataFrameSampleHelper("associateBy", "api/collectionsInterop") {

    private val df = peopleDf

    @Test
    fun notebook_test_associateBy_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_associateBy_2() {
        // SampleStart
        df.associateBy { "${name.firstName} ${name.lastName}" }
        // SampleEnd
    }
}
