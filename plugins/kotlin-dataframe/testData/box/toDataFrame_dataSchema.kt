@file:Suppress("UnusedImport")

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

@DataSchema
data class Person(val firstName: String, val lastName: String, val age: Int, val city: String?)

@DataSchema
data class Group(val id: String, val participants: List<Person>)

fun box(): String {
    val df = listOf(
        Group("1", listOf(
            Person("Alice", "Cooper", 15, "London"),
            Person("Bob", "Dylan", 45, "Dubai")
        )),
        Group("2", listOf(
            Person("Charlie", "Daniels", 20, "Moscow"),
            Person("Charlie", "Chaplin", 40, "Milan"),
        )),
    ).toDataFrame(maxDepth = 0)

    val groups = df.add("groupSize") {
        // interestingly, this is still a DataFrame. Because @DataSchema Person
        participants.rowsCount()
    }
    return "OK"
}
