package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class FunctionsStdTest : TestBase() {

    @Test
    fun `DataColumn any`() {
        val ageCol = df["age"] as DataColumn<Int>
        ageCol.any { it > 40 } shouldBe true
        ageCol.any { it > 90 } shouldBe false
    }

    @Test
    fun `DataFrame any`() {
        df.any { "age"<Int>() > 40 && "isHappy"<Boolean>() } shouldBe true
        df.any { "city"<String?>() == "Berlin" } shouldBe false
    }

    @Test
    fun `DataColumn between`() {
        val ages = listOf(15, 45, 20, 40, 30, 20, 30)
        val ageCol = df["age"] as DataColumn<Int>
        ageCol.between(20, 40).toList() shouldBe listOf(false, false, true, true, true, true, true)
        ageCol.between(20, 40, includeBoundaries = false).toList() shouldBe listOf(false, false, false, false, true, false, true)
        ageCol.toList() shouldBe ages
    }

    @Test
    fun `DataFrame associateBy`() {
        val byFirstName = df.associateBy { "name"["firstName"]<String>() }
        val alice = byFirstName["Alice"]!!
        val aliceName = alice.getColumnGroup("name")
        aliceName["lastName"] shouldBe "Wolf"
        alice["age"] shouldBe 20

        val byCity = df.associateBy { "city"<String?>() }
        val moscow = byCity["Moscow"]!!
        moscow.getColumnGroup("name")["lastName"] shouldBe "Byrd"
    }

    @Test
    fun `DataFrame associate`() {
        val map = df.associate { "name"["lastName"]<String>() to "age"<Int>() }
        map.size shouldBe 7
        map["Marley"] shouldBe 30
        map["Cooper"] shouldBe 15
    }

    @Test
    fun `DataColumn asIterable`() {
        val ageCol = df["age"] as DataColumn<Int>
        ageCol.asIterable().toList() shouldBe listOf(15, 45, 20, 40, 30, 20, 30)
    }

    @Test
    fun `DataColumn asSequence`() {
        val ageCol = df["age"] as DataColumn<Int>
        ageCol.asSequence().take(2).toList() shouldBe listOf(15, 45)
    }

    @Test
    fun `DataFrame asSequence`() {
        val happyCount = df.asSequence().count { it["isHappy"] as Boolean }
        happyCount shouldBe 5
    }
}
