package org.jetbrains.kotlinx.dataframe.puzzles

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test
import java.text.DecimalFormatSymbols
import kotlin.reflect.typeOf

class BasicTests {

    private val animal by columnOf("cat", "cat", "snake", "dog", "dog", "cat", "snake", "cat", "dog", "dog")
    private val age by columnOf(2.5, 3.0, 0.5, Double.NaN, 5.0, 2.0, 4.5, Double.NaN, 7.0, 3.0)
    private val visits by columnOf(1, 3, 2, 3, 2, 3, 1, 1, 2, 1)
    private val priority by columnOf("yes", "yes", "no", "yes", "no", "no", "no", "yes", "no", "no")

    private val df = dataFrameOf(animal, age, visits, priority)

    @Test
    fun `return first 3 rows`() {
        val expected = dataFrameOf("animal", "age", "visits", "priority")(
            "cat", 2.5, 1, "yes",
            "cat", 3.0, 3, "yes",
            "snake", 0.5, 2, "no"
        )

        df[0 until 3] shouldBe expected
        df.head(3) shouldBe expected
        df.take(3) shouldBe expected
    }

    @Test
    fun `select animal and age columns from df`() {
        val expected = dataFrameOf(animal, age)

        df[animal, age] shouldBe expected
        df["animal", "age"] shouldBe expected
        df.select { animal and age } shouldBe expected
        df.select { "animal" and "age" } shouldBe expected
    }

    @Test
    fun `select rows (3, 4, 8) and columns (animal, age)`() {
        val expected = dataFrameOf("animal", "age")(
            "dog", Double.NaN,
            "dog", 5.0,
            "dog", 7.0
        )

        df[3, 4, 8][animal, age] shouldBe expected
        df[3, 4, 8]["animal", "age"] shouldBe expected
        df.select { animal and age }[3, 4, 8] shouldBe expected
        df.select { "animal" and "age" }[3, 4, 8] shouldBe expected
    }

    @Test
    fun `select only rows where number of visits is grater than 2`() {
        val expected = dataFrameOf("animal", "age", "visits", "priority")(
            "cat", 3.0, 3, "yes",
            "dog", Double.NaN, 3, "yes",
            "cat", 2.0, 3, "no"
        )

        df.filter { visits > 2 } shouldBe expected
        df.filter { "visits"<Int>() > 2 } shouldBe expected
    }

    @Test
    fun `select rows where age is missing`() {
        val expected = dataFrameOf("animal", "age", "visits", "priority")(
            "dog", Double.NaN, 3, "yes",
            "cat", Double.NaN, 1, "yes"
        )

        df.filter { age().isNaN() } shouldBe expected
        df.filter { "age"<Double>().isNaN() } shouldBe expected
    }

    @Test
    fun `select rows where animal is a cat and age is less than 3`() {
        val expected = dataFrameOf("animal", "age", "visits", "priority")(
            "cat", 2.5, 1, "yes",
            "cat", 2.0, 3, "no"
        )

        df.filter { animal() == "cat" && age() < 3 } shouldBe expected
        df.filter { "animal"<String>() == "cat" && "age"<Double>() < 3 } shouldBe expected
    }

    @Test
    fun `select rows where age is between 2 and 4 (inclusive)`() {
        val expected = dataFrameOf("animal", "age", "visits", "priority")(
            "cat", 2.5, 1, "yes",
            "cat", 3.0, 3, "yes",
            "cat", 2.0, 3, "no",
            "dog", 3.0, 1, "no"
        )

        df.filter { age() in 2.0..4.0 } shouldBe expected
        df.filter { "age"() in 2.0..4.0 } shouldBe expected
    }

    @Test
    fun `change age in row 5 to 1,5`() {
        val dfActualAcc0 = df.update { age }.at(5).with { 1.5 }
        val dfActualAcc1 = df.update { "age"<Double>() }.at(5).with { 1.5 }

        dfActualAcc0[5][age] shouldBe 1.5
        dfActualAcc1[5]["age"] shouldBe 1.5
    }

    @Test
    fun `calculate sum of all visits`() {
        df[visits].sum() shouldBe 19
        df.sum { visits } shouldBe 19

        df["visits"].cast<Int>().sum() shouldBe 19
        df.sum { "visits"<Int>() } shouldBe 19
        df.sum("visits") shouldBe 19
    }

    @Test
    fun `calculate mean age for each animal`() {
        val expected = dataFrameOf("animal", "age")(
            "cat", Double.NaN,
            "snake", 2.5,
            "dog", Double.NaN
        )

        df.groupBy { animal }.mean { age } shouldBe expected
        df.groupBy("animal").mean("age") shouldBe expected
    }

    @Test
    fun `append and drop new row`() {
        val modifiedDf = df.append("dog", 5.5, 2, "no")

        val d = DecimalFormatSymbols.getInstance().decimalSeparator
        modifiedDf[10].toString() shouldBe "{ animal:dog, age:5${d}500000, visits:2, priority:no }"

        modifiedDf.dropLast() shouldBe df
    }

    @Test
    fun `count number of each type of animal`() {
        val expected = dataFrameOf("animal", "count")(
            "cat", 4,
            "snake", 2,
            "dog", 4,
        )

        df.groupBy { animal }.count() shouldBe expected
        df.groupBy("animal").count() shouldBe expected
    }

    @Test
    fun `sort df first by the values in age in descending order, then by in visit in ascending order`() {
        val expected = dataFrameOf("age", "visits")(4.5, 1, 3.0, 1, 3.0, 3)

        val sortDfAcc = df.sortBy { age.desc() and visits }
        val sortDfStr = df.sortBy { "age".desc() and "visits" }

        sortDfAcc[age, visits][4..6] shouldBe expected
        sortDfStr["age", "visits"][4..6] shouldBe expected
    }

    @Test
    fun `replace priority column to boolean values`() {
        val convertedDfAcc = df.convert { priority }.with { it == "yes" }
        val convertedDfStr = df.convert { "priority"<String>() }.with { it == "yes" }

        convertedDfAcc[priority].type() shouldBe typeOf<Boolean>()
        convertedDfAcc["priority"].type() shouldBe typeOf<Boolean>()

        convertedDfStr[priority][5] shouldBe false
        convertedDfStr["priority"][5] shouldBe false
    }

    @Test
    fun `change dog to corgi`() {
        val updatedDfAcc = df.update { animal }.where { it == "dog" }.with { "corgi" }
        val updatedDfStr = df.update("animal").where { it == "dog" }.with { "corgi" }

        updatedDfAcc[animal][3] shouldBe "corgi"
        updatedDfAcc[animal][8] shouldBe "corgi"

        updatedDfStr["animal"][3] shouldBe "corgi"
        updatedDfStr["animal"][8] shouldBe "corgi"
    }

    @Test
    fun `find mean age for each animal type and number of visits`() {
        val expected = dataFrameOf("animal", "1", "3", "2")(
            "cat", 2.5, 2.5, null,
            "snake", 4.5, null, 0.5,
            "dog", 3.0, Double.NaN, 6.0
        )

        val actualDfAcc = df.pivot(inward = false) { visits }.groupBy { animal }.mean(skipNA = true) { age }
        val actualDfStr = df.pivot("visits", inward = false).groupBy("animal").mean("age", skipNA = true)

        actualDfAcc shouldBe expected
        actualDfStr shouldBe expected
    }
}
