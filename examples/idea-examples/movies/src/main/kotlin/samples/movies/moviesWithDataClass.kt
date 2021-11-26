package samples.movies

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.read

data class Movie(val movieId: String, val title: String, val genres: String)

private const val pathToCsv = "examples/idea-examples/movies/src/main/resources/ml-latest/movies.csv"

fun main() {
    val df1 = DataFrame
        .read(pathToCsv).convertTo<Movie>()
        .split(Movie::genres).by("|").inplace()
        .split(Movie::title).by {
            listOf(
                "\\s*\\(\\d{4}\\)\\s*$".toRegex().replace(it, ""),
                "\\d{4}".toRegex().findAll(it).lastOrNull()?.value?.toIntOrNull() ?: -1
            )
        }.into("title", "year")


    df1
        .explode("genres")
        .filter { "year"<Int>() >= 0 && it[Movie::genres] != "(no genres listed)" }
        .groupBy("year")
        .sortBy("year")
        .pivot("genres", inward = false)
        .aggregate {
            count() into "count"
            mean() into "mean"
        }.print(10)
}
