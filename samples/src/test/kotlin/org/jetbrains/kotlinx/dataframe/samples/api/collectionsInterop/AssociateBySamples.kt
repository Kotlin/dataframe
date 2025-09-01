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

    private val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
        "Alice",
        "Cooper",
        15,
        "London",
        54,
        true,
        "Bob",
        "Dylan",
        45,
        "Dubai",
        87,
        true,
        "Charlie",
        "Daniels",
        20,
        "Moscow",
        null,
        false,
        "Charlie",
        "Chaplin",
        40,
        "Milan",
        null,
        true,
        "Bob",
        "Marley",
        30,
        "Tokyo",
        68,
        true,
        "Alice",
        "Wolf",
        20,
        null,
        55,
        false,
        "Charlie",
        "Byrd",
        30,
        "Moscow",
        90,
        true,
    ).group("firstName", "lastName").into("name").cast<Person>()

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
