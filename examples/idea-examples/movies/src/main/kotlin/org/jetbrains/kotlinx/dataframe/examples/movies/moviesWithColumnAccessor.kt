package org.jetbrains.kotlinx.dataframe.examples.movies

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.io.read

private const val pathToCsv = "examples/idea-examples/movies/src/main/resources/movies.csv"

fun main() {
    val genres by column<String>()
    val title by column<String>()
    val year by column<Int>()

    DataFrame
        .read(pathToCsv)
        .split { genres }.by("|").inplace()
        .split { title }.by {
            listOf(
                """\s*\(\d{4}\)\s*$""".toRegex().replace(it, ""),
                "\\d{4}".toRegex().findAll(it).lastOrNull()?.value?.toIntOrNull() ?: -1
            )
        }.into(title, year)
        .explode { genres }
        .filter { year() >= 0 && genres() != "(no genres listed)" }
        .groupBy { year }
        .sortBy { year }
        .pivot(inward = false) { genres }
        .aggregate {
            count() into "count"
            mean() into "mean"
        }.print(10)
}
