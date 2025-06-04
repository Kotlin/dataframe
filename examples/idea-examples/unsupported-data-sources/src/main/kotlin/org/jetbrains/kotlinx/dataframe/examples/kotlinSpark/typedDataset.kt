package org.jetbrains.kotlinx.dataframe.examples.kotlinSpark

import org.apache.spark.sql.Dataset
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.spark.api.withSpark

@DataSchema
data class Name(val firstName: String, val lastName: String)

@DataSchema
data class Person(
    val name: Name,
    val age: Int,
    val city: String?,
    val weight: Int?,
    val isHappy: Boolean,
)

object TypedDataset {

    /**
     * With the Kotlin Spark API, norm;;al Kotlin data classes are supported,
     * meaning we can reuse the same class for Spark and DataFrame!
     *
     */
    @JvmStatic
    fun main(args: Array<String>): Unit =
        withSpark {
            // Spark Dataset
            val rawDataset: Dataset<Person> = listOf(
                Person(Name("Alice", "Cooper"), 15, "London", 54, true),
                Person(Name("Bob", "Dylan"), 45, "Dubai", 87, true),
                Person(Name("Charlie", "Daniels"), 20, "Moscow", null, false),
                Person(Name("Charlie", "Chaplin"), 40, "Milan", null, true),
                Person(Name("Bob", "Marley"), 30, "Tokyo", 68, true),
                Person(Name("Alice", "Wolf"), 20, null, 55, false),
                Person(Name("Charlie", "Byrd"), 30, "Moscow", 90, true),
            ).toDS()

            // we can perform large operations in Spark
            val dataset = rawDataset.filter { it.age > 17 }

            // and convert to DataFrame
            val dataframe = dataset.collectAsList().toDataFrame()

            dataframe.print(columnTypes = true, borders = true)
        }
}
