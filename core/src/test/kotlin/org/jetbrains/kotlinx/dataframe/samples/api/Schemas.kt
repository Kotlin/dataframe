@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.inward
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.explainer.TransformDataFrameExpressions
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class Schemas {

    @DataSchema
    interface Person {
        val name: String
        val age: Int
    }

    fun DataFrame<Person>.splitName() = split { name }.by(",").inward("firstName", "lastName")

    fun DataFrame<Person>.adults() = filter { age > 18 }

    @Test
    @TransformDataFrameExpressions
    fun createDfNullable() {
        // SampleStart
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", null,
        )
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun createDf() {
        // SampleStart
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
        )
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun extendedDf() {
        // SampleStart
        val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", 15, 60.0,
            "Marley, Bob", 20, 73.5,
        )
        // SampleEnd
        df.print()
    }

    @Test
    @TransformDataFrameExpressions
    fun splitNameWorks() {
        val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", 15, 60.0,
            "Marley, Bob", 20, 73.5,
        ).cast<Person>()
        // SampleStart
        df.splitName()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun adultsWorks() {
        val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", 15, 60.0,
            "Marley, Bob", 20, 73.5,
        ).cast<Person>()
        // SampleStart
        df.adults()
        // SampleEnd
    }

    fun DataFrame<Person>.countAdults() = count { it[Person::age] > 18 }

    @Test
    @TransformDataFrameExpressions
    fun convertTo() {
        // SampleStart
        @DataSchema
        data class Name(val firstName: String, val lastName: String)

        @DataSchema
        data class Person(val name: Name, val age: Int?)

        val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", "15", 60.0,
            "Marley, Bob", "20", 73.5,
        ).split { "name"<String>() }.inward("firstName", "lastName")

        val persons = df.cast<Person>().toList()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun useProperties() {
        // SampleStart
        val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
        ).cast<Person>()
        // age only available after executing `build` or `kspKotlin`!
        val teens = df.filter { age in 10..19 }
        teens.print()
        // SampleEnd
    }

    @Test
    @TransformDataFrameExpressions
    fun useInferredSchema() {
        // SampleStart
        // Repository.readCsv() has argument 'path' with default value https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv
        val df = Repository.readCsv()
        // Use generated properties to access data in rows
        df.maxBy { stargazersCount }.print()
        // Or to access columns in dataframe.
        print(df.fullName.count { it.contains("kotlin") })
        // SampleEnd
    }
}
