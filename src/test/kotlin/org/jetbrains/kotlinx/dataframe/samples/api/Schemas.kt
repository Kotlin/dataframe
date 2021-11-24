package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.jupyter.useSchema
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import org.junit.Test

class Schemas {

    @DataSchema
    interface Person {
        val name: String
        val age: Int
    }

    fun DataFrame<Person>.splitName() = split { name }.by(",").into("firstName", "lastName")
    fun DataFrame<Person>.adults() = filter { age > 18 }

    @Test
    fun createDf() {
        // SampleStart
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", null
        )
        // SampleEnd
    }

    @Test
    fun extendedDf() {
        // SampleStart
        val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", 15, 60.0,
            "Marley, Bob", 20, 73.5
        )
        // SampleEnd
        df.print()
    }

    @Test
    fun splitName() {
        val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", 15, 60.0,
            "Marley, Bob", 20, 73.5
        ).cast<Person>()
        // SampleStart
        df.splitName()
        // SampleEnd
    }

    @Test
    fun adults() {
        val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", 15, 60.0,
            "Marley, Bob", 20, 73.5
        ).cast<Person>()
        // SampleStart
        df.adults()
        // SampleEnd
    }

    fun DataFrame<Person>.countAdults() = count { it[Person::age] > 18 }

    // @JupyterLibrary
    internal class Integration : JupyterIntegration() {

        override fun Builder.onLoaded() {
            onLoaded {
                useSchema<Person>()
            }
        }
    }
}
