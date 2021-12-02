package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dropNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.toListOf
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.io.read
import org.junit.Ignore
import org.junit.Test

@Ignore
class ApiLevels {

    @Test
    fun strings() {
        // SampleStart
        DataFrame.read("titanic.csv")
            .add("lastName") { "name"<String>().split(",").last() }
            .dropNulls("age")
            .filter { "survived"<Boolean>() && "home"<String>().endsWith("NY") && "age"<Int>() in 10..20 }
        // SampleEnd
    }

    @Test
    fun accessors1() {
        // SampleStart
        val survived by column<Boolean>() // accessor for Boolean column with name 'survived'
        val home by column<String>()
        val age by column<Int?>()
        val name by column<String>()
        val lastName by column<String>()
        // SampleEnd
    }

    @Test
    fun accessors2() {
        val survived by column<Boolean>()
        val home by column<String>()
        val age by column<Int?>()
        val name by column<String>()
        val lastName by column<String>()
        // SampleStart

        DataFrame.read("titanic.csv")
            .add(lastName) { name().split(",").last() }
            .dropNulls { age }
            .filter { survived() && home().endsWith("NY") && age()!! in 10..20 }
        // SampleEnd
    }

    @Test
    fun accessors3() {
        // SampleStart
        val survived by column<Boolean>()
        val home by column<String>()
        val age by column<Int?>()
        val name by column<String>()
        val lastName by column<String>()

        DataFrame.read("titanic.csv")
            .add(lastName) { name().split(",").last() }
            .dropNulls { age }
            .filter { survived() && home().endsWith("NY") && age()!! in 10..20 }
        // SampleEnd
    }

    @Test
    fun kproperties1() {
        // SampleStart
        data class Passenger(val survived: Boolean, val home: String, val age: Int, val lastName: String)

        val passengers = DataFrame.read("titanic.csv")
            .add(Passenger::lastName) { "name"<String>().split(",").last() }
            .dropNulls(Passenger::age)
            .filter { it[Passenger::survived] && it[Passenger::home].endsWith("NY") && it[Passenger::age] in 10..20 }
            .toListOf<Passenger>()
        // SampleEnd
    }

    @Test
    fun kproperties2() {
        // SampleStart
        data class Passenger(
            @ColumnName("survived") val isAlive: Boolean,
            @ColumnName("home") val city: String,
            val name: String
        )

        val passengers = DataFrame.read("titanic.csv")
            .filter { it.get(Passenger::city).endsWith("NY") }
            .toListOf<Passenger>()
        // SampleEnd
    }

    @DataSchema
    interface TitanicPassenger {
        val survived: Boolean
        val home: String
        val age: Int
        val name: String
    }

    @Test
    fun extensionProperties2() {
        val df = DataFrame.read("titanic.csv").cast<TitanicPassenger>()
        // SampleStart
        df.add("lastName") { name.split(",").last() }
            .dropNulls { age }
            .filter { survived && home.endsWith("NY") && age in 10..20 }
        // SampleEnd
    }

    @Test
    fun extensionProperties1() {
        // SampleStart
        val df = DataFrame.read("titanic.csv")
        // SampleEnd
    }
}
