package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import java.io.File
import java.net.URI
import java.net.URL
import kotlin.io.path.Path
import kotlin.io.path.absolute

class Guess2 {

    @Test
    fun `read JSON reference`() {
        val expected = DataFrame.readJson("../data/participants.json")

        DataFrame.readReference("../data/participants.json") shouldBe expected
        DataFrame.readReference(Path("../data/participants.json")) shouldBe expected
        DataFrame.readReference(File("../data/participants.json")) shouldBe expected
        DataFrame.readReference(
            Path("../data/participants.json").absolute().normalize().toUri().toURL(),
        ) shouldBe expected

        val options = org.jetbrains.kotlinx.dataframe.io.Json.Options(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readReference("../data/participants.json", options) shouldBe expected
        DataFrame.readReference(Path("../data/participants.json"), options) shouldBe expected
        DataFrame.readReference(File("../data/participants.json"), options) shouldBe expected
        DataFrame.readReference(
            Path("../data/participants.json").absolute().normalize().toUri().toURL(),
            options,
        ) shouldBe expected
    }

    @Test
    fun `read JSON in memory`() {
        val expected = DataFrame.readJson("../data/participants.json")

        val file = File("../data/participants.json")

        DataFrame.readFromData(file.readText()) shouldBe expected
        DataFrame.readFromData(file.inputStream()) shouldBe expected
        DataFrame.readFromData(Json.decodeFromString<JsonElement>(file.readText())) shouldBe expected

        val options = org.jetbrains.kotlinx.dataframe.io.Json.Options(
            typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        )

        DataFrame.readFromData(file.readText(), options) shouldBe expected
        DataFrame.readFromData(file.inputStream(), options) shouldBe expected
        DataFrame.readFromData(Json.decodeFromString<JsonElement>(file.readText()), options) shouldBe expected
    }
}
