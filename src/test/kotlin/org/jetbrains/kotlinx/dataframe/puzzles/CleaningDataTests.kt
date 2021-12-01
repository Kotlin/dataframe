package org.jetbrains.kotlinx.dataframe.puzzles

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.junit.Test

class CleaningDataTests {

    private val fromTo = listOf(
        "LoNDon_paris",
        "MAdrid_miLAN",
        "londON_StockhOlm",
        "Budapest_PaRis",
        "Brussels_londOn"
    ).toColumn("From_To")
    private val flightNumber = listOf(10045.0, Double.NaN, 10065.0, Double.NaN, 10085.0).toColumn("FlightNumber")
    private val recentDelays =
        listOf(listOf(23, 47), listOf(), listOf(24, 43, 87), listOf(13), listOf(67, 32)).toColumn("RecentDelays")
    private val airline = listOf(
        "KLM(!)",
        "{Air France} (12)",
        "(British Airways. )",
        "12. Air France",
        "'Swiss Air'"
    ).toColumn("Airline")

    private var df = dataFrameOf(fromTo, flightNumber, recentDelays, airline)

    @Test
    fun `interpolate test`() {
        val expected = columnOf(10045, 10055, 10065, 10075, 10085).named("FlightNumber")

        df.update { flightNumber }.where { it.isNaN() }
            .with { prev()!![flightNumber] + (next()!![flightNumber] - prev()!![flightNumber]) / 2 }
            .convert { flightNumber }.toInt()[flightNumber] shouldBe expected

        df.update { "FlightNumber"<Double>() }.where { it.isNaN() }
            .with {
                prev()!![ { "FlightNumber"<Double>() }] + (next()!![ { "FlightNumber"<Double>() }] - prev()!![ { "FlightNumber"<Double>() }]) / 2
            }
            .convert { flightNumber }.toInt()["FlightNumber"] shouldBe expected
    }

    @Test
    fun `split From_To`() {
        val expected = dataFrameOf("From", "To")(
            "LoNDon", "paris",
            "MAdrid", "miLAN",
            "londON", "StockhOlm",
            "Budapest", "PaRis",
            "Brussels", "londOn"
        )

        df.split { fromTo }.by('_').into("From", "To")["From", "To"] shouldBe expected
        df.split { "From_To"<String>() }.by('_').into("From", "To")["From", "To"] shouldBe expected
    }

    @Test
    fun `uppercase for cities`() {
        val from by column<String>("From")
        val to by column<String>("To")

        val expected = dataFrameOf("From", "To")(
            "London", "Paris",
            "Madrid", "Milan",
            "London", "Stockholm",
            "Budapest", "Paris",
            "Brussels", "London"
        )

        df
            .split { fromTo }.by('_').into("From", "To")[from, to]
            .update { from and to }.with { it.lowercase().replaceFirstChar(Char::uppercase) } shouldBe expected

        df
            .split { "From_To"<String>() }.by('_').into("From", "To")["From", "To"]
            .update { "From"<String>() and "To"() }
            .with { it.lowercase().replaceFirstChar(Char::uppercase) } shouldBe expected
    }

    @Test
    fun `airline test`() {
        val expected = columnOf("KLM", "Air France", "British Airways", "Air France", "Swiss Air").named("Airline")

        df.update { airline }.with {
            "([a-zA-Z\\s]+)".toRegex().find(it)?.value?.trim() ?: ""
        }[airline] shouldBe expected

        df.update { "Airline"<String>() }.with {
            "([a-zA-Z\\s]+)".toRegex().find(it)?.value?.trim() ?: ""
        }["Airline"] shouldBe expected
    }

    @Test
    fun `split delays`() {
        val delay1 by column<Double>("delay_1")
        val delay2 by column<Double>("delay_2")
        val delay3 by column<Double>("delay_3")

        val expected = dataFrameOf("delay_1", "delay_2", "delay_3")(
            23.0, 47.0, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN,
            24.0, 43.0, 87.0,
            13.0, Double.NaN, Double.NaN,
            67.0, 32.0, Double.NaN
        )

        df
            .convert { recentDelays }.with { it.map { d -> d.toDouble() } }
            .split { recentDelays }.default(Double.NaN).into { "delay_$it" }[delay1, delay2, delay3] shouldBe expected

        df
            .convert { "RecentDelays"<List<Int>>() }.with { it.map { d -> d.toDouble() } }
            .split { "RecentDelays"<List<Double>>() }.default(Double.NaN)
            .into { "delay_$it" }[delay1, delay2, delay3] shouldBe expected
    }
}
