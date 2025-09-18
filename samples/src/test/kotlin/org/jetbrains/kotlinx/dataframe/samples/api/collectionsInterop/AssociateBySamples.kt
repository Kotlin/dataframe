package org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.associateBy
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class AssociateBySamples : DataFrameSampleHelper("associateBy", "api/collectionsInterop") {

    @DataSchema
    interface Name {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val name: DataRow<Name>
        val weight: Int?
        val isHappy: Boolean
    }

    private val df = dataFrameOf(
        "firstName" to listOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
        "lastName" to listOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
        "age" to listOf(15, 45, 20, 40, 30, 20, 30),
        "city" to listOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
        "weight" to listOf(54, 87, null, null, 68, 55, 90),
        "isHappy" to listOf(true, true, false, true, true, false, true),
    ).group { firstName and lastName }.into("name").cast<Person>()

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
