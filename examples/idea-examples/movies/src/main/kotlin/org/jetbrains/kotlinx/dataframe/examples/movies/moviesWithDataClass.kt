package org.jetbrains.kotlinx.dataframe.examples.movies

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.read

private const val pathToCsv = "examples/idea-examples/movies/src/main/resources/movies.csv"

fun main() {

    data class Movie(val movieId: String, val title: String, val genres: String, val year: Int)

    DataFrame
        .read(pathToCsv)
        .split(Movie::genres).by("|").inplace()
        .split(Movie::title).by {
            listOf(
                "\\s*\\(\\d{4}\\)\\s*$".toRegex().replace(it, ""),
                "\\d{4}".toRegex().findAll(it).lastOrNull()?.value?.toIntOrNull() ?: -1
            )
        }.into(Movie::title, Movie::year)
        .explode(Movie::genres)
        .filter { it[Movie::year] >= 0 && it[Movie::genres] != "(no genres listed)" }
        .groupBy(Movie::year)
        .sortBy(Movie::year)
        .pivot(Movie::genres, inward = false)
        .aggregate {
            count() into "count"
            mean() into "mean"
        }.print(10)
}
