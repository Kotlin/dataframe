package org.jetbrains.kotlinx.dataframe.examples.movies

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import java.io.File

/**
 * | movieId                          | title                     | genres                           |
 * |----------------------------------|---------------------------|----------------------------------|
 * | 9b30aff7943f44579e92c261f3adc193 | Women in Black (1997)     | Fantasy\|Suspenseful\|Comedy     |
 * | 2a1ba1fc5caf492a80188e032995843e | Bumblebee Movie (2007)    | Comedy\|Jazz\|Family\|Animation  |
 */
@DataSchema
interface Movie {
    val movieId: String
    val title: String
    val genres: String
}

private val pathToCsv = File(object {}.javaClass.classLoader.getResource("movies.csv")!!.toURI()).absolutePath
// Uncomment this line if you want to copy-paste and run the code in your project without downloading the file
// private const val pathToCsv = "https://raw.githubusercontent.com/Kotlin/dataframe/master/examples/idea-examples/movies/src/main/resources/movies.csv"

fun main() {
    // This example shows how to the use extension properties API to address columns in different operations
    // https://kotlin.github.io/dataframe/apilevels.html

    // Add the Gradle plugin and run `assemble`
    // check the README https://github.com/Kotlin/dataframe?tab=readme-ov-file#setup
    val step1 = DataFrame.read(pathToCsv)
        .convertTo<Movie>()
        .split { genres }.by("|").inplace()
        // TODO replace with `requireColumn {}` #1715 or replace+AddDsl #1749
        .split { title }.by {
            listOf<Any>(
                """\s*\(\d{4}\)\s*$""".toRegex().replace(title, ""),
                "\\d{4}".toRegex().findAll(title).lastOrNull()?.value?.toIntOrNull() ?: -1,
            )
        }.into("title", "year")
        .explode { genres }
        .cast<Step1>(verify = true)

    step1.print(borders = true)

    /**
     * Data is parsed and prepared for aggregation
     *
     * | movieId                          | title              | year | genres       |
     * |----------------------------------|--------------------|------|--------------|
     * | 9b30aff7943f44579e92c261f3adc193 | Women in Black     | 1997 | Fantasy      |
     * | 9b30aff7943f44579e92c261f3adc193 | Women in Black     | 1997 | Suspenseful  |
     * | 9b30aff7943f44579e92c261f3adc193 | Women in Black     | 1997 | Comedy       |
     * | 2a1ba1fc5caf492a80188e032995843e | Bumblebee Movie    | 2007 | Comedy       |
     * | 2a1ba1fc5caf492a80188e032995843e | Bumblebee Movie    | 2007 | Jazz         |
     * | 2a1ba1fc5caf492a80188e032995843e | Bumblebee Movie    | 2007 | Family       |
     * | 2a1ba1fc5caf492a80188e032995843e | Bumblebee Movie    | 2007 | Animation    |
     */
    val step2 = step1
        .filter { year >= 0 && genres != "(no genres listed)" }
        .groupBy { year }
        .sortBy { year }
        .pivot(inward = false) { genres }
        .aggregate {
            count() into "count"
            mean() into "mean"
        }

    step2.print(10)
//    Discover the final reshaped data in an interactive HTML table
//    step2.toStandaloneHTML().openInBrowser()
}

@DataSchema
internal data class Step1(
    val movieId: String,
    val title: String,
    val year: Int,
    val genres: String,
)
