package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class StatisticsTests {
    private val personsDf = dataFrameOf("name", "age", "city", "weight", "height", "yearsToRetirement")(
        "Alice", 15, "London", 99.5, "1.85", 50,
        "Bob", 20, "Paris", 140.0, "1.35", 45,
        "Charlie", 100, "Dubai", 75.0, "1.95", 0,
        "Rose", 1, "Moscow", 45.33, "0.79", 64,
        "Dylan", 35, "London", 23.4, "1.83", 30,
        "Eve", 40, "Paris", 56.72, "1.85", 25,
        "Frank", 55, "Dubai", 78.9, "1.35", 10,
        "Grace", 29, "Moscow", 67.8, "1.65", 36,
        "Hank", 60, "Paris", 80.22, "1.75", 5,
        "Isla", 22, "London", 75.1, "1.85", 43,
    )

    @Test
    fun `sum on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").sum()
        res0.columnNames() shouldBe listOf("city", "age", "weight", "yearsToRetirement")

        val sum01 = res0["age"][0] as Int
        sum01 shouldBe 72
        val sum02 = res0["weight"][0] as Double
        sum02 shouldBe 198.0

        // scenario #1: particular column
        val res1 = personsDf.groupBy("city").sumFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val sum11 = res1["age"][0] as Int
        sum11 shouldBe 72

        // scenario #1.1: particular column via sum
        val res11 = personsDf.groupBy("city").sum("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val sum111 = res11["age"][0] as Int
        sum111 shouldBe 72

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy("city").sum("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")

        val sum21 = res2["newAge"][0] as Int
        sum21 shouldBe 72

        // scenario #2.1: particular column with new name - schema changes but via columnSelector
        val res21 = personsDf.groupBy("city").sum(name = "newAge") { "age"<Int>() }
        res21.columnNames() shouldBe listOf("city", "newAge")

        val sum211 = res21["newAge"][0] as Int
        sum211 shouldBe 72

        // scenario #2.2: two columns with new name - schema changes but via columnSelector
        val res22 = personsDf.groupBy("city").sum(name = "newAge") { "age"<Int>() and "yearsToRetirement"<Int>() }
        res22.columnNames() shouldBe listOf("city", "newAge")

        val sum221 = res22["newAge"][0] as Int
        sum221 shouldBe 195

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy("city").sumOf(resultName = "newAge") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "newAge")

        val sum31 = res3["newAge"][0] as Int
        sum31 shouldBe 720

        // scenario #3.1: create new column via expression with Double type
        val res31 = personsDf.groupBy("city").sumOf(resultName = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val sum311 = res31["newAge"][0] as Double
        sum311 shouldBe 1980.0
    }

    @Test
    fun `mean on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").mean()
        res0.columnNames() shouldBe listOf("city", "age", "weight", "yearsToRetirement")

        val mean01 = res0["age"][0] as Double
        mean01 shouldBe 24.0
        val mean02 = res0["weight"][0] as Double
        mean02 shouldBe 66.0

        // scenario #1: particular column
        val res1 = personsDf.groupBy("city").meanFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val mean11 = res1["age"][0] as Double
        mean11 shouldBe 24.0

        // scenario #1.1: particular column via mean
        val res11 = personsDf.groupBy("city").mean("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val mean111 = res11["age"][0] as Double
        mean111 shouldBe 24.0

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy("city").mean("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")

        val mean21 = res2["newAge"][0] as Double
        mean21 shouldBe 24.0

        // scenario #2.1: particular column with new name - schema changes but via columnSelector
        val res21 = personsDf.groupBy("city").mean(name = "newAge") { "age"<Int>() }
        res21.columnNames() shouldBe listOf("city", "newAge")

        val mean211 = res21["newAge"][0] as Double
        mean211 shouldBe 24.0

        // scenario #2.2: two columns with new name - schema changes but via columnSelector
        val res22 = personsDf.groupBy("city").mean(name = "newAge") { "age"<Int>() and "yearsToRetirement"<Int>() }
        res22.columnNames() shouldBe listOf("city", "newAge")

        val mean221 = res22["newAge"][0] as Double
        mean221 shouldBe 32.5

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy("city").meanOf(name = "newAge") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "newAge")

        val mean31 = res3["newAge"][0] as Double
        mean31 shouldBe 240

        // scenario #3.1: create new column via expression with Double
        val res31 = personsDf.groupBy("city").meanOf(name = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val mean311 = res31["newAge"][0] as Double
        mean311 shouldBe 660.0
    }

    @Test
    fun `median on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").median()
        res0.columnNames() shouldBe listOf(
            "city",
            "name",
            "age",
            "weight",
            "height",
            "yearsToRetirement",
        )

        val median01 = res0["age"][0] as Int
        median01 shouldBe 22
        // val median02 = res0["weight"][0] as Double
        // median02 shouldBe 66.0

        // scenario #1: particular column
        val res1 = personsDf.groupBy("city").medianFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val median11 = res1["age"][0] as Int
        median11 shouldBe 22

        // scenario #1.1: particular column via median
        val res11 = personsDf.groupBy("city").median("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val median111 = res11["age"][0] as Int
        median111 shouldBe 22

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy("city").median("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")

        val median21 = res2["newAge"][0] as Int
        median21 shouldBe 22

        // scenario #2.1: particular column with new name - schema changes but via columnSelector
        val res21 = personsDf.groupBy("city").median(name = "newAge") { "age"<Int>() }
        res21.columnNames() shouldBe listOf("city", "newAge")

        val median211 = res21["newAge"][0] as Int
        median211 shouldBe 22

        // scenario #2.2: two columns with new name - schema changes but via columnSelector
        val res22 = personsDf.groupBy("city").median(name = "newAge") { "age"<Int>() and "yearsToRetirement"<Int>() }
        res22.columnNames() shouldBe listOf("city", "newAge")

        val median221 = res22["newAge"][0] as Int
        median221 shouldBe 32

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy("city").medianOf(name = "newAge") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "newAge")

        val median31 = res3["newAge"][0] as Int
        median31 shouldBe 220

        // scenario #3.1: create new column via expression with Double
        val res31 = personsDf.groupBy("city").medianOf(name = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val median311 = res31["newAge"][0] as Double
        median311 shouldBe 751.0
    }

    @Test
    fun `std on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").std()
        res0.columnNames() shouldBe listOf("city", "age", "weight", "yearsToRetirement")

        val std01 = res0["age"][0] as Double
        std01 shouldBe 10.14889156509222
        val std02 = res0["weight"][0] as Double
        std02 shouldBe 38.85756039691633

        // scenario #1: particular column
        val res1 = personsDf.groupBy("city").stdFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val std11 = res1["age"][0] as Double
        std11 shouldBe 10.14889156509222

        // scenario #1.1: particular column via std
        val res11 = personsDf.groupBy("city").std("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val std111 = res11["age"][0] as Double
        std111 shouldBe 10.14889156509222

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy("city").std("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")

        val std21 = res2["newAge"][0] as Double
        std21 shouldBe 10.14889156509222

        // scenario #2.1: particular column with new name - schema changes but via columnSelector
        val res21 = personsDf.groupBy("city").std(name = "newAge") { "age"<Int>() }
        res21.columnNames() shouldBe listOf("city", "newAge")

        val std211 = res21["newAge"][0] as Double
        std211 shouldBe 10.14889156509222

        // scenario #2.2: two columns with new name - schema changes but via columnSelector
        val res22 = personsDf.groupBy("city").std(name = "newAge") { "age"<Int>() and "yearsToRetirement"<Int>() }
        res22.columnNames() shouldBe listOf("city", "newAge")

        val std221 = res22["newAge"][0] as Double
        std221 shouldBe 13.003845585056753

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy("city").stdOf(name = "newAge") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "newAge")

        val std31 = res3["newAge"][0] as Double
        std31 shouldBe 101.4889156509222

        // scenario #3.1: create new column via expression with Double
        val res31 = personsDf.groupBy("city").stdOf(name = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val std311 = res31["newAge"][0] as Double
        std311 shouldBe 388.57560396916324
    }

    @Test
    fun `min on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").min()
        res0.columnNames() shouldBe listOf(
            "city",
            "name",
            "age",
            "weight",
            "height",
            "yearsToRetirement",
        )

        val min01 = res0["age"][0] as Int
        min01 shouldBe 15
        // val min02 = res0["weight"][0] as Double
        // min02 shouldBe 38.85756039691633

        // scenario #1: particular column
        val res1 = personsDf.groupBy("city").minFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val min11 = res1["age"][0] as Int
        min11 shouldBe 15

        // scenario #1.1: particular column via min
        val res11 = personsDf.groupBy("city").min("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val min111 = res11["age"][0] as Int
        min111 shouldBe 15

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy("city").min("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")

        val min21 = res2["newAge"][0] as Int
        min21 shouldBe 15

        // scenario #2.1: particular column with new name - schema changes but via columnSelector
        val res21 = personsDf.groupBy("city").min(name = "newAge") { "age"<Int>() }
        res21.columnNames() shouldBe listOf("city", "newAge")

        val min211 = res21["newAge"][0] as Int
        min211 shouldBe 15

        // scenario #2.2: two columns with new name - schema changes but via columnSelector
        val res22 = personsDf.groupBy("city").min(name = "newAge") { "age"<Int>() and "yearsToRetirement"<Int>() }
        res22.columnNames() shouldBe listOf("city", "newAge")

        val min221 = res22["newAge"][0] as Int
        min221 shouldBe 15

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy("city").minOf(name = "newAge") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "newAge")

        val min31 = res3["newAge"][0] as Int
        min31 shouldBe 150

        // scenario #3.1: create new column via expression with Double
        val res31 = personsDf.groupBy("city").minOf(name = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val min311 = res31["newAge"][0] as Double
        min311 shouldBe 234.0

        // scenario #4: particular column via minBy
        val res4 = personsDf.groupBy("city").minBy("age").values()
        res4.columnNames() shouldBe listOf(
            "city",
            "name",
            "age",
            "weight",
            "height",
            "yearsToRetirement",
        ) // TODO: why is here weight presented? looks like inconsitency

        val min41 = res4["age"][0] as Int
        min41 shouldBe 15
        val min42 = res4["weight"][0] as Double
        min42 shouldBe 99.5

        // scenario #5: particular column via minBy and rowExpression
        val res5 = personsDf.groupBy("city").minBy { "age"<Int>() * 10 }.values()
        res4.columnNames() shouldBe listOf("city", "name", "age", "weight", "height", "yearsToRetirement")

        val min51 = res5["age"][0] as Int
        min51 shouldBe 15
    }

    @Test
    fun `max on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").max()
        res0.columnNames() shouldBe listOf("city", "name", "age", "weight", "height", "yearsToRetirement")

        val max01 = res0["age"][0] as Int
        max01 shouldBe 35
        // val max02 = res0["weight"][0] as Double
        // max02 shouldBe 140.0

        // scenario #1: particular column
        val res1 = personsDf.groupBy("city").maxFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val max11 = res1["age"][0] as Int
        max11 shouldBe 35

        // scenario #1.1: particular column via max
        val res11 = personsDf.groupBy("city").max("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val max111 = res11["age"][0] as Int
        max111 shouldBe 35

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy("city").max("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")

        val max21 = res2["newAge"][0] as Int
        max21 shouldBe 35

        // scenario #2.1: particular column with new name - schema changes but via columnSelector
        val res21 = personsDf.groupBy("city").max(name = "newAge") { "age"<Int>() }
        res21.columnNames() shouldBe listOf("city", "newAge")

        val max211 = res21["newAge"][0] as Int
        max211 shouldBe 35

        // scenario #2.2: two columns with new name - schema changes but via columnSelector
        val res22 = personsDf.groupBy("city").max(name = "newAge") { "age"<Int>() and "yearsToRetirement"<Int>() }
        res22.columnNames() shouldBe listOf("city", "newAge")

        val max221 = res22["newAge"][0] as Int
        max221 shouldBe 50

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy("city").maxOf(name = "newAge") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "newAge")

        val max31 = res3["newAge"][0] as Int
        max31 shouldBe 350

        // scenario #3.1: create new column via expression with Double
        val res31 = personsDf.groupBy("city").maxOf(name = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val max311 = res31["newAge"][0] as Double
        max311 shouldBe 995.0

        // scenario #4: particular column via maxBy
        val res4 = personsDf.groupBy("city").maxBy("age").values()
        res4.columnNames() shouldBe listOf(
            "city",
            "name",
            "age",
            "weight",
            "height",
            "yearsToRetirement",
        ) // TODO: weight is here?

        val max41 = res4["age"][0] as Int
        max41 shouldBe 35
        val max42 = res4["weight"][0] as Double
        max42 shouldBe 23.4

        // scenario #5: particular column via maxBy and rowExpression
        val res5 = personsDf.groupBy("city").maxBy { "age"<Int>() * 10 }.values()
        res4.columnNames() shouldBe listOf("city", "name", "age", "weight", "height", "yearsToRetirement")

        val max51 = res5["age"][0] as Int
        max51 shouldBe 35
    }
}
