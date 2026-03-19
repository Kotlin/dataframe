package org.jetbrains.kotlinx.dataframe.examples.movies

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.explode
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.inplace
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.split
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.read
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
// private const val pathToCsv = "https://raw.githubusercontent.com/Kotlin/dataframe/master/examples/projects/movies/src/main/resources/movies.csv"

fun main() {
    // This example shows how to the use extension properties API to address columns in different operations
    // https://kotlin.github.io/dataframe/apilevels.html#extension-properties-api
    // check the README https://github.com/Kotlin/dataframe
    val step1 = DataFrame.read(pathToCsv)
        .convertTo<Movie>()
        .split { genres }.by("|").inplace()
        .split { title }.by {
            listOf(
                """\s*\(\d{4}\)\s*$""".toRegex().replace(title, ""),
                "\\d{4}".toRegex().findAll(title).lastOrNull()?.value,
            )
        }.into("title", "year")
        .convert { year }.with { it?.toIntOrNull() ?: -1 }
        .explode { genres }

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
