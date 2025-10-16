package org.jetbrains.kotlinx.dataframe.examples.movies

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*


private const val pathToCsv = "examples/idea-examples/movies/src/main/resources/movies.csv"
// Uncomment this line if you want to copy-paste and run the code in your project without downloading the file
//private const val pathToCsv = "https://raw.githubusercontent.com/Kotlin/dataframe/master/examples/idea-examples/movies/src/main/resources/movies.csv"

fun main() {
    // This example shows how to the use extension properties API to address columns in different operations
    // https://kotlin.github.io/dataframe/apilevels.html

    // Add the Gradle plugin and run `assemble`
    // check the README https://github.com/Kotlin/dataframe?tab=readme-ov-file#setup
    val step1 = DataFrame
        .read(pathToCsv).convertTo<Movie>()
        .split("genres").by("|").inplace()
        .split { "title"<String>() }.by {
            listOf(
                """\s*\(\d{4}\)\s*$""".toRegex().replace(it, ""),
                "\\d{4}".toRegex().findAll(it).lastOrNull()?.value?.toIntOrNull() ?: -1
            )
        }.into("title", "year")
        .explode("genres")
    step1.print()

    /**
     * Data is parsed and prepared for aggregation
     *                             movieId                                    title year      genres
     *   0 9b30aff7943f44579e92c261f3adc193                           Women in Black 1997     Fantasy
     *   1 9b30aff7943f44579e92c261f3adc193                           Women in Black 1997 Suspenseful
     *   2 9b30aff7943f44579e92c261f3adc193                           Women in Black 1997      Comedy
     *   3 2a1ba1fc5caf492a80188e032995843e                          Bumblebee Movie 2007      Comedy
     *   4 2a1ba1fc5caf492a80188e032995843e                          Bumblebee Movie 2007        Jazz
     *   5 2a1ba1fc5caf492a80188e032995843e                          Bumblebee Movie 2007      Family
     *   6 2a1ba1fc5caf492a80188e032995843e                          Bumblebee Movie 2007   Animation
     */
    val step2 = step1
        .filter { "year"<Int>() >= 0 && "genres"<String>() != "(no genres listed)" }
        .groupBy("year")
        .sortBy("year")
        .pivot("genres", inward = false)
        .aggregate {
            count() into "count"
            mean() into "mean"
        }

    step2.print(10)
//    Discover the final reshaped data in an interactive HTML table
//    step2.toStandaloneHTML().openInBrowser()
}
