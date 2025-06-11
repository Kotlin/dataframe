@file:Suppress("ktlint:standard:function-signature")

package org.jetbrains.kotlinx.dataframe.examples.spark

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.examples.spark.convertToDataFrame
import org.jetbrains.kotlinx.dataframe.examples.spark.convertToDataFrameByInference
import org.jetbrains.kotlinx.dataframe.examples.spark.convertToSpark
import org.jetbrains.kotlinx.spark.api.col
import org.jetbrains.kotlinx.spark.api.gt

/**
 * Since we don't know the schema at compile time this time, we need to do
 * some schema mapping in between Spark and DataFrame.
 *
 * We will use spark/compatibilityLayer.kt to do this.
 * Take a look at that file for the implementation details!
 *
 * NOTE: You will likely need to run this function with Java 8 or 11 for it to work correctly.
 * Use the `runSparkUntypedDataset` Gradle task to do so.
 */
fun main() {
    val spark = SparkSession.builder()
        .master(SparkConf().get("spark.master", "local[*]"))
        .appName("Kotlin Spark Sample")
        .getOrCreate()
    val sc = JavaSparkContext(spark.sparkContext())

    // Creating a Spark Dataframe (untyped Dataset). Usually, this is loaded from some server or database.
    val rawDataset: Dataset<Row> = spark.createDataset(
        listOf(
            Person(Name("Alice", "Cooper"), 15, "London", 54, true),
            Person(Name("Bob", "Dylan"), 45, "Dubai", 87, true),
            Person(Name("Charlie", "Daniels"), 20, "Moscow", null, false),
            Person(Name("Charlie", "Chaplin"), 40, "Milan", null, true),
            Person(Name("Bob", "Marley"), 30, "Tokyo", 68, true),
            Person(Name("Alice", "Wolf"), 20, null, 55, false),
            Person(Name("Charlie", "Byrd"), 30, "Moscow", 90, true),
        ),
        Person.encoder,
    ).toDF()

    // we can perform large operations in Spark.
    // DataFrames are in-memory structures, so this is a good place to limit the number of rows if you don't have the RAM ;)
    val dataset = rawDataset.filter(col("age") gt 17)

    // Using inference
    val df1 = dataset.convertToDataFrameByInference()
    df1.schema().print()
    df1.print(columnTypes = true, borders = true)

    // Using full schema mapping
    val df2 = dataset.convertToDataFrame()
    df2.schema().print()
    df2.print(columnTypes = true, borders = true)

    // now we can use DataFrame-specific functions
    val ageStats = df1
        .groupBy("city").aggregate {
            mean("age") into "meanAge"
            std("age") into "stdAge"
            min("age") into "minAge"
            max("age") into "maxAge"
        }

    ageStats.print(columnTypes = true, borders = true)

    // and when we want to convert a DataFrame back to Spark, we will use the `convertToSpark()` extension function
    // This performs the necessary schema mapping under the hood.
    val sparkDataset = df2.convertToSpark(spark, sc)
    sparkDataset.printSchema()
    sparkDataset.show()

    spark.stop()
}
