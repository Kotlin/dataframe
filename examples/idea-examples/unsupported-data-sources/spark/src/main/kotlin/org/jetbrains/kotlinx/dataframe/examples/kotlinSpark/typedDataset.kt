@file:Suppress("ktlint:standard:function-signature")

package org.jetbrains.kotlinx.dataframe.examples.kotlinSpark

import org.apache.spark.sql.Dataset
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.spark.api.withSpark

/**
 * With the Kotlin Spark API, normal Kotlin data classes are supported,
 * meaning we can reuse the same class for Spark and DataFrame!
 *
 * Also, since we use an actual class to define the schema, we need no type conversion!
 *
 * See [Person] and [Name] for an example.
 *
 * NOTE: You will likely need to run this function with Java 8 or 11 for it to work correctly.
 * Use the `runKotlinSparkTypedDataset` Gradle task to do so.
 */
fun main() = withSpark {
    // Creating a Spark Dataset. Usually, this is loaded from some server or database.
    val rawDataset: Dataset<Person> = listOf(
        Person(Name("Alice", "Cooper"), 15, "London", 54, true),
        Person(Name("Bob", "Dylan"), 45, "Dubai", 87, true),
        Person(Name("Charlie", "Daniels"), 20, "Moscow", null, false),
        Person(Name("Charlie", "Chaplin"), 40, "Milan", null, true),
        Person(Name("Bob", "Marley"), 30, "Tokyo", 68, true),
        Person(Name("Alice", "Wolf"), 20, null, 55, false),
        Person(Name("Charlie", "Byrd"), 30, "Moscow", 90, true),
    ).toDS()

    // we can perform large operations in Spark.
    // DataFrames are in-memory structures, so this is a good place to limit the number of rows if you don't have the RAM ;)
    val dataset = rawDataset.filter { it.age > 17 }

    // and convert it to DataFrame via a typed List
    val dataframe = dataset.collectAsList().toDataFrame()
    dataframe.schema().print()
    dataframe.print(columnTypes = true, borders = true)

    // now we can use DataFrame-specific functions
    val ageStats = dataframe
        .groupBy { city }.aggregate {
            mean { age } into "meanAge"
            std { age } into "stdAge"
            min { age } into "minAge"
            max { age } into "maxAge"
        }

    ageStats.print(columnTypes = true, borders = true)

    // and when we want to convert a DataFrame back to Spark, we can do the same trick via a typed List
    val sparkDatasetAgain = dataframe.toList().toDS()
    sparkDatasetAgain.printSchema()
    sparkDatasetAgain.show()
}

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
