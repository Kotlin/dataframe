package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.diff
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.api.withValue
import org.junit.Test

class DataRowApi : TestBase() {

    @Test
    fun expressions() {
        // SampleStart
        // Row expression computes values for a new column
        df.add("fullName") { name.firstName + " " + name.lastName }

        // Row expression computes updated values
        df.update { weight }.at(1, 3, 4).with { prev()?.weight }

        // Row expression computes cell content for values of pivoted column
        df.pivot { city }.with { name.lastName.uppercase() }
        // SampleEnd
    }

    @Test
    fun conditions() {
        // SampleStart
        // Row condition is used to filter rows by index
        df.filter { index() % 5 == 0 }

        // Row condition is used to drop rows where `age` is the same as in previous row
        df.drop { diff { age } == 0 }

        // Row condition is used to filter rows for value update
        df.update { weight }.where { index() > 4 && city != "Paris" }.withValue(50)
        // SampleEnd
    }
}
