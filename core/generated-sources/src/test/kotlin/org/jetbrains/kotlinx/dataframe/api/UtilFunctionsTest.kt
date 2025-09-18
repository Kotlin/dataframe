package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.size
import org.junit.Test
import kotlin.collections.map
import kotlin.random.Random

@Suppress("UNCHECKED_CAST")
class UtilFunctionsTest : TestBase() {

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
        ageCol.between(20, 40, includeBoundaries = false).toList() shouldBe
            listOf(false, false, false, false, true, false, true)
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

    @Test
    fun `DataFrame chunked`() {
        val groups = df.chunked(3)
        groups.size shouldBe 3
        groups.name() shouldBe "groups"
        groups[0].rowsCount() shouldBe 3
        groups[1].rowsCount() shouldBe 3
        groups[2].rowsCount() shouldBe 1
    }

    @Test
    fun `DataColumn chunked`() {
        val ageCol = df["age"] as DataColumn<Int>
        val chunked = ageCol.chunked(4)
        chunked.size shouldBe 2
        chunked.name() shouldBe "age"
        // Check chunk contents
        chunked[0] shouldBe listOf(15, 45, 20, 40)
        chunked[1] shouldBe listOf(30, 20, 30)
    }

    @Test
    fun `DataFrame shuffle`() {
        val rnd = Random(123)
        val shuffledDf = df.shuffle(rnd)
        // Compute expected order via indices.shuffled with same seed
        val ages = (df["age"] as DataColumn<Int>).toList()
        val expectedAges = ages.indices.shuffled(Random(123)).map { ages[it] }
        shuffledDf.rows().map { it["age"] as Int } shouldBe expectedAges
    }

    @Test
    fun `DataColumn shuffle`() {
        val rnd = Random(123)
        val ageCol = df["age"] as DataColumn<Int>
        val shuffled = ageCol.shuffle(rnd)
        val values = ageCol.toList()
        val expected = values.indices.shuffled(Random(123)).map { values[it] }
        shuffled.toList() shouldBe expected
    }
}
