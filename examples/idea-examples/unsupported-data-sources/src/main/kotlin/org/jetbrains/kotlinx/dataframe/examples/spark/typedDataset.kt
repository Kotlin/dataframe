@file:Suppress("ktlint:standard:function-signature")

package org.jetbrains.kotlinx.dataframe.examples.spark

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Encoder
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.SparkSession
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
import java.io.Serializable

/**
 * For Spark, Kotlin data classes are supported if we:
 * - Add [@JvmOverloads][JvmOverloads] to the constructor
 * - Make all parameter arguments mutable and with defaults
 * - Make them [Serializable]
 *
 * But by adding [@DataSchema][DataSchema] we can reuse the same class for Spark and DataFrame!
 *
 * See [Person] and [Name] for an example.
 *
 * Also, since we use an actual class to define the schema, we need no type conversion!
 *
 * NOTE: You will likely need to run this function with Java 8 or 11 for it to work correctly.
 * Use the `runSparkTypedDataset` Gradle task to do so.
 */
fun main() {
    val spark = SparkSession.builder()
        .master(SparkConf().get("spark.master", "local[*]"))
        .appName("Kotlin Spark Sample")
        .getOrCreate()
    val sc = JavaSparkContext(spark.sparkContext())

    // Creating a Spark Dataset. Usually, this is loaded from some server or database.
    val rawDataset: Dataset<Person> = spark.createDataset(
        listOf(
            Person(Name("Alice", "Cooper"), 15, "London", 54, true),
            Person(Name("Bob", "Dylan"), 45, "Dubai", 87, true),
            Person(Name("Charlie", "Daniels"), 20, "Moscow", null, false),
            Person(Name("Charlie", "Chaplin"), 40, "Milan", null, true),
            Person(Name("Bob", "Marley"), 30, "Tokyo", 68, true),
            Person(Name("Alice", "Wolf"), 20, null, 55, false),
            Person(Name("Charlie", "Byrd"), 30, "Moscow", 90, true),
        ),
        beanEncoderOf(),
    )

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
    val sparkDatasetAgain = spark.createDataset(dataframe.toList(), beanEncoderOf())
    sparkDatasetAgain.printSchema()
    sparkDatasetAgain.show()

    spark.stop()
}

/** Creates a [bean encoder][Encoders.bean] for the given [T] instance. */
inline fun <reified T : Serializable> beanEncoderOf(): Encoder<T> = Encoders.bean(T::class.java)

@DataSchema
data class Name
    @JvmOverloads
    constructor(var firstName: String = "", var lastName: String = "") : Serializable

@DataSchema
data class Person
    @JvmOverloads
    constructor(
        var name: Name = Name(),
        var age: Int = -1,
        var city: String? = null,
        var weight: Int? = null,
        var isHappy: Boolean = false,
    ) : Serializable
