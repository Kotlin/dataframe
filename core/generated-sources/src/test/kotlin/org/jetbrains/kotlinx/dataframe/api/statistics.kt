package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.columns.ResolvingValueColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.StatisticResult
import org.jetbrains.kotlinx.dataframe.impl.columns.ValueColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ValueColumnInternal
import org.jetbrains.kotlinx.dataframe.impl.columns.asValueColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.internalValueColumn
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class StatisticsTests {
    private val personsDf = dataFrameOf(
        "name",
        "age",
        "city",
        "weight",
        "height",
        "yearsToRetirement",
        "workExperienceYears",
        "dependentsCount",
        "annualIncome",
    )(
        "Alice", 15, "London", 99.5, "1.85", 50, 0.toShort(), 0.toByte(), 0L,
        "Bob", 20, "Paris", 140.0, "1.35", 45, 2.toShort(), 0.toByte(), 12000L,
        "Charlie", 100, "Dubai", 75.0, "1.95", 0, 70.toShort(), 0.toByte(), 0L,
        "Rose", 1, "Moscow", 45.33, "0.79", 64, 0.toShort(), 2.toByte(), 0L,
        "Dylan", 35, "London", 23.4, "1.83", 30, 15.toShort(), 1.toByte(), 90000L,
        "Eve", 40, "Paris", 56.72, "1.85", 25, 18.toShort(), 3.toByte(), 125000L,
        "Frank", 55, "Dubai", 78.9, "1.35", 10, 35.toShort(), 2.toByte(), 145000L,
        "Grace", 29, "Moscow", 67.8, "1.65", 36, 5.toShort(), 1.toByte(), 70000L,
        "Hank", 60, "Paris", 80.22, "1.75", 5, 40.toShort(), 4.toByte(), 200000L,
        "Isla", 22, "London", 75.1, "1.85", 43, 1.toShort(), 0.toByte(), 30000L,
    )

    @Test
    fun `sum on DataFrame`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.sum()
        res0.columnNames() shouldBe listOf(
            "age",
            "weight",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val sum01 = res0["age"] as Int
        sum01 shouldBe 377
        val sum02 = res0["weight"] as Double
        sum02 shouldBe 741.9699999999999
        val sum03 = res0["yearsToRetirement"] as Int
        sum03 shouldBe 308
        val sum04 = res0["workExperienceYears"] as Int
        sum04 shouldBe 186
        val sum05 = res0["dependentsCount"] as Int
        sum05 shouldBe 13.0
        val sum06 = res0["annualIncome"] as Long
        sum06 shouldBe 672000

        // scenario #1: particular column
        val res1 = personsDf.sumFor("age")
        res1.columnNames() shouldBe listOf("age")

        val sum11 = res1["age"] as Int
        sum11 shouldBe 377

        // scenario #1.1: particular column with converted type
        val res11 = personsDf.sumFor("dependentsCount")
        res11.columnNames() shouldBe listOf("dependentsCount")

        val sum111 = res11["dependentsCount"] as Int
        sum111 shouldBe 13

        // scenario #2: sum of values per columns separately
        val res3 = personsDf.sumFor("age", "weight", "workExperienceYears", "dependentsCount", "annualIncome")
        res3.columnNames() shouldBe listOf("age", "weight", "workExperienceYears", "dependentsCount", "annualIncome")

        val sum31 = res3["age"] as Int
        sum31 shouldBe 377
        val sum32 = res0["weight"] as Double
        sum32 shouldBe 741.9699999999999
        val sum33 = res0["workExperienceYears"] as Int
        sum33 shouldBe 186
        val sum34 = res0["dependentsCount"] as Int
        sum34 shouldBe 13.0
        val sum35 = res0["annualIncome"] as Long
        sum35 shouldBe 672000
    }

    @Test
    fun `sum on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").sum()
        res0.columnNames() shouldBe listOf(
            "city",
            "age",
            "weight",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

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
    fun `mean on DataFrame`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.mean()
        res0.columnNames() shouldBe listOf(
            "age",
            "weight",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val mean01 = res0["age"] as Double
        mean01 shouldBe 37.7
        val mean02 = res0["weight"] as Double
        mean02 shouldBe 74.19699999999999
        val mean03 = res0["yearsToRetirement"] as Double
        mean03 shouldBe 30.8
        val mean04 = res0["workExperienceYears"] as Double
        mean04 shouldBe 18.6
        val mean05 = res0["dependentsCount"] as Double
        mean05 shouldBe 1.30
        val mean06 = res0["annualIncome"] as Double
        mean06 shouldBe 67200.0

        // scenario #1: particular column
        val res1 = personsDf.meanFor("age")
        res1.columnNames() shouldBe listOf("age")

        val mean11 = res1["age"] as Double
        mean11 shouldBe 37.7

        // scenario #1.1: particular column with a converted type
        val res11 = personsDf.meanFor("dependentsCount")
        res11.columnNames() shouldBe listOf("dependentsCount")

        val mean111 = res11["dependentsCount"] as Double
        mean111 shouldBe 1.3

        // scenario #2: mean of values per columns separately
        val res3 = personsDf.meanFor("age", "weight", "workExperienceYears", "dependentsCount", "annualIncome")
        res3.columnNames() shouldBe listOf("age", "weight", "workExperienceYears", "dependentsCount", "annualIncome")

        val mean31 = res3["age"] as Double
        mean31 shouldBe 37.7
        val mean32 = res0["weight"] as Double
        mean32 shouldBe 74.19699999999999
        val mean33 = res0["workExperienceYears"] as Double
        mean33 shouldBe 18.6
        val mean34 = res0["dependentsCount"] as Double
        mean34 shouldBe 1.3
        val mean35 = res0["annualIncome"] as Double
        mean35 shouldBe 67200.0
    }

    @Test
    fun `mean on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").mean()
        res0.columnNames() shouldBe listOf(
            "city",
            "age",
            "weight",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

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

        // scenario #2.1: particular column with a new name-schema changes but via columnSelector
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

        // scenario #3.1: create a new column via expression with Double
        val res31 = personsDf.groupBy("city").meanOf(name = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val mean311 = res31["newAge"][0] as Double
        mean311 shouldBe 660.0
    }

    @Test
    fun `median on DataFrame`() {
        // scenario #0: all intraComparableColumns columns
        val res0 = personsDf.median()
        res0.columnNames() shouldBe listOf(
            "name",
            "age",
            "city",
            "weight",
            "height",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val median01 = res0["age"] as Double
        median01 shouldBe 32.0
        val median02 = res0["weight"] as Double
        median02 shouldBe 75.05
        val median03 = res0["yearsToRetirement"] as Double
        median03 shouldBe 33.0
        val median04 = res0["workExperienceYears"] as Double
        median04 shouldBe 10.0
        val median05 = res0["dependentsCount"] as Double
        median05 shouldBe 1.0
        val median06 = res0["annualIncome"] as Double
        median06 shouldBe 50000.0
        val median07 = res0["name"] as String
        median07 shouldBe "Eve"
        val median08 = res0["city"] as String
        median08 shouldBe "London"
        val median09 = res0["height"] as String
        median09 shouldBe "1.75"

        // scenario #1: particular column
        val res1 = personsDf.medianFor("age")
        res1.columnNames() shouldBe listOf("age")

        val median11 = res1["age"] as Double
        median11 shouldBe 32.0

        // scenario #1.1: particular column with a converted type
        val res11 = personsDf.medianFor("dependentsCount")
        res11.columnNames() shouldBe listOf("dependentsCount")

        val median111 = res11["dependentsCount"] as Double
        median111 shouldBe 1.0

        // scenario #2: median of values per columns separately
        val res3 = personsDf.medianFor("weight", "workExperienceYears", "dependentsCount", "annualIncome", "name")
        res3.columnNames() shouldBe listOf("weight", "workExperienceYears", "dependentsCount", "annualIncome", "name")

        val median31 = res3["weight"] as Double
        median31 shouldBe 75.05
        val median32 = res3["workExperienceYears"] as Double
        median32 shouldBe 10.0
        val median33 = res3["dependentsCount"] as Double
        median33 shouldBe 1.0
        val median34 = res3["annualIncome"] as Double
        median34 shouldBe 50000.0
        val median35 = res3["name"] as String
        median35 shouldBe "Eve"
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
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val median01 = res0["age"][0] as Double
        median01 shouldBe 22.0
        // val median02 = res0["weight"][0] as Double
        // median02 shouldBe 66.0

        // scenario #1: particular column
        val res1 = personsDf.groupBy("city").medianFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val median11 = res1["age"][0] as Double
        median11 shouldBe 22.0

        // scenario #1.1: particular column via median
        val res11 = personsDf.groupBy("city").median("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val median111 = res11["age"][0] as Double
        median111 shouldBe 22.0

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy("city").median("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")

        val median21 = res2["newAge"][0] as Double
        median21 shouldBe 22.0

        // scenario #2.1: particular column with new name - schema changes but via columnSelector
        val res21 = personsDf.groupBy("city").median(name = "newAge") { "age"<Int>() }
        res21.columnNames() shouldBe listOf("city", "newAge")

        val median211 = res21["newAge"][0] as Double
        median211 shouldBe 22.0

        // scenario #2.2: two columns with new name - schema changes but via columnSelector
        val res22 = personsDf.groupBy("city").median(name = "newAge") { "age"<Int>() and "yearsToRetirement"<Int>() }
        res22.columnNames() shouldBe listOf("city", "newAge")

        val median221 = res22["newAge"][0] as Double
        median221 shouldBe 32.5

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy("city").medianOf(name = "newAge") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "newAge")

        val median31 = res3["newAge"][0] as Double
        median31 shouldBe 220.0

        // scenario #3.1: create new column via expression with Double
        val res31 = personsDf.groupBy("city").medianOf(name = "newAge") { "weight"<Double>() * 10 }
        res31.columnNames() shouldBe listOf("city", "newAge")

        val median311 = res31["newAge"][0] as Double
        median311 shouldBe 751.0
    }

    @Test
    fun `std on DataFrame`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.std()
        res0.columnNames() shouldBe listOf(
            "age",
            "weight",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val std01 = res0["age"] as Double
        std01 shouldBe 28.26088777405582
        val std02 = res0["weight"] as Double
        std02 shouldBe 31.25191124822075
        val std03 = res0["yearsToRetirement"] as Double
        std03 shouldBe 20.89550722577038
        val std04 = res0["workExperienceYears"] as Double
        std04 shouldBe 23.200574705525437
        val std05 = res0["dependentsCount"] as Double
        std05 shouldBe 1.4181364924121764
        val std06 = res0["annualIncome"] as Double
        std06 shouldBe 71130.24048259018

        // scenario #1: particular column
        val res1 = personsDf.stdFor("age")
        res1.columnNames() shouldBe listOf("age")

        val std11 = res1["age"] as Double
        std11 shouldBe 28.26088777405582

        // scenario #1.1: particular column with a converted type
        val res11 = personsDf.stdFor("dependentsCount")
        res11.columnNames() shouldBe listOf("dependentsCount")

        val std111 = res11["dependentsCount"] as Double
        std111 shouldBe 1.4181364924121764

        // scenario #2: std of values per columns separately
        val res3 = personsDf.stdFor("age", "weight", "workExperienceYears", "dependentsCount", "annualIncome")
        res3.columnNames() shouldBe listOf("age", "weight", "workExperienceYears", "dependentsCount", "annualIncome")

        val std31 = res3["age"] as Double
        std31 shouldBe 28.26088777405582
        val std32 = res0["weight"] as Double
        std32 shouldBe 31.25191124822075
        val std33 = res0["workExperienceYears"] as Double
        std33 shouldBe 23.200574705525437
        val std34 = res0["dependentsCount"] as Double
        std34 shouldBe 1.4181364924121764
        val std35 = res0["annualIncome"] as Double
        std35 shouldBe 71130.24048259018
    }

    @Test
    fun `std on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").std()
        res0.columnNames() shouldBe listOf(
            "city",
            "age",
            "weight",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

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
    fun `min on DataFrame`() {
        // scenario #0: all intraComparableColumns columns
        val res0 = personsDf.min()
        res0.columnNames() shouldBe listOf(
            "name",
            "age",
            "city",
            "weight",
            "height",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val min01 = res0["age"] as Int
        min01 shouldBe 1
        val min02 = res0["weight"] as Double
        min02 shouldBe 23.4
        val min03 = res0["yearsToRetirement"] as Int
        min03 shouldBe 0
        val min04 = res0["workExperienceYears"] as Short
        min04 shouldBe 0
        val min05 = res0["dependentsCount"] as Byte
        min05 shouldBe 0
        val min06 = res0["annualIncome"] as Long
        min06 shouldBe 0L
        val min07 = res0["name"] as String
        min07 shouldBe "Alice"
        val min08 = res0["city"] as String
        min08 shouldBe "Dubai"
        val min09 = res0["height"] as String
        min09 shouldBe "0.79"

        // scenario #1: particular column
        val res1 = personsDf.minFor("age")
        res1.columnNames() shouldBe listOf("age")

        val min11 = res1["age"] as Int
        min11 shouldBe 1.0

        // scenario #1.1: particular column with a converted type
        val res11 = personsDf.minFor("dependentsCount")
        res11.columnNames() shouldBe listOf("dependentsCount")

        val min111 = res11["dependentsCount"] as Byte
        min111 shouldBe 0

        // scenario #2: min of values per columns separately
        val res3 = personsDf.minFor("weight", "workExperienceYears", "dependentsCount", "annualIncome", "name")
        res3.columnNames() shouldBe listOf("weight", "workExperienceYears", "dependentsCount", "annualIncome", "name")

        val min31 = res3["weight"] as Double
        min31 shouldBe 23.4
        val min32 = res3["workExperienceYears"] as Short
        min32 shouldBe 0
        val min33 = res3["dependentsCount"] as Byte
        min33 shouldBe 0
        val min34 = res3["annualIncome"] as Long
        min34 shouldBe 0L
        val min35 = res3["name"] as String
        min35 shouldBe "Alice"
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
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
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
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        ) // TODO: why is here weight presented? looks like inconsitency

        val min41 = res4["age"][0] as Int
        min41 shouldBe 15
        val min42 = res4["weight"][0] as Double
        min42 shouldBe 99.5

        // scenario #5: particular column via minBy and rowExpression
        val res5 = personsDf.groupBy("city").minBy { "age"<Int>() * 10 }.values()
        res4.columnNames() shouldBe listOf(
            "city",
            "name",
            "age",
            "weight",
            "height",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val min51 = res5["age"][0] as Int
        min51 shouldBe 15
    }

    @Test
    fun `max on DataFrame`() {
        // scenario #0: all intraComparableColumns columns
        val res0 = personsDf.max()
        res0.columnNames() shouldBe listOf(
            "name",
            "age",
            "city",
            "weight",
            "height",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val max01 = res0["age"] as Int
        max01 shouldBe 100
        val max02 = res0["weight"] as Double
        max02 shouldBe 140.0
        val max03 = res0["yearsToRetirement"] as Int
        max03 shouldBe 64
        val max04 = res0["workExperienceYears"] as Short
        max04 shouldBe 70
        val max05 = res0["dependentsCount"] as Byte
        max05 shouldBe 4
        val max06 = res0["annualIncome"] as Long
        max06 shouldBe 200000L
        val max07 = res0["name"] as String
        max07 shouldBe "Rose"
        val max08 = res0["city"] as String
        max08 shouldBe "Paris"
        val max09 = res0["height"] as String
        max09 shouldBe "1.95"

        // scenario #1: particular column
        val res1 = personsDf.maxFor("age")
        res1.columnNames() shouldBe listOf("age")

        val max11 = res1["age"] as Int
        max11 shouldBe 100

        // scenario #1.1: particular column with a converted type
        val res11 = personsDf.maxFor("dependentsCount")
        res11.columnNames() shouldBe listOf("dependentsCount")

        val max111 = res11["dependentsCount"] as Byte
        max111 shouldBe 4

        // scenario #2: max of values per columns separately
        val res3 = personsDf.maxFor("weight", "workExperienceYears", "dependentsCount", "annualIncome", "name")
        res3.columnNames() shouldBe listOf("weight", "workExperienceYears", "dependentsCount", "annualIncome", "name")

        val max31 = res3["weight"] as Double
        max31 shouldBe 140.0
        val max32 = res3["workExperienceYears"] as Short
        max32 shouldBe 70
        val max33 = res3["dependentsCount"] as Byte
        max33 shouldBe 4
        val max34 = res3["annualIncome"] as Long
        max34 shouldBe 200000L
        val max35 = res3["name"] as String
        max35 shouldBe "Rose"
    }

    @Test
    fun `max on GroupBy`() {
        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy("city").max()
        res0.columnNames() shouldBe listOf(
            "city",
            "name",
            "age",
            "weight",
            "height",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

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
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        ) // TODO: weight is here?

        val max41 = res4["age"][0] as Int
        max41 shouldBe 35
        val max42 = res4["weight"][0] as Double
        max42 shouldBe 23.4

        // scenario #5: particular column via maxBy and rowExpression
        val res5 = personsDf.groupBy("city").maxBy { "age"<Int>() * 10 }.values()
        res4.columnNames() shouldBe listOf(
            "city",
            "name",
            "age",
            "weight",
            "height",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val max51 = res5["age"][0] as Int
        max51 shouldBe 35
    }

    @Test
    fun `percentile on DataFrame`() {
        // scenario #0: all intraComparableColumns columns
        val percentile = 30.0
        val res0 = personsDf.percentile(percentile)
        res0.columnNames() shouldBe listOf(
            "name",
            "age",
            "city",
            "weight",
            "height",
            "yearsToRetirement",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
        )

        val percentile01 = res0["age"] as Double
        percentile01 shouldBe 20.866666666666667
        val percentile02 = res0["weight"] as Double
        percentile02 shouldBe 61.52133333333333
        val percentile03 = res0["yearsToRetirement"] as Double
        percentile03 shouldBe 16.500000000000004
        val percentile04 = res0["workExperienceYears"] as Double
        percentile04 shouldBe 1.4333333333333336
        val percentile05 = res0["dependentsCount"] as Double
        percentile05 shouldBe 0.0
        val percentile06 = res0["annualIncome"] as Double
        percentile06 shouldBe 5200.000000000003
        val percentile07 = res0["name"] as String
        percentile07 shouldBe "Charlie"
        val percentile08 = res0["city"] as String
        percentile08 shouldBe "London"
        val percentile09 = res0["height"] as String
        percentile09 shouldBe "1.35"

        // scenario #1: particular column
        val res1 = personsDf.percentileFor(percentile, "age")
        res1.columnNames() shouldBe listOf("age")

        val percentile11 = res1["age"] as Double
        percentile11 shouldBe 20.866666666666667

        // scenario #1.1: particular column with a converted type
        val res11 = personsDf.percentileFor(percentile, "dependentsCount")
        res11.columnNames() shouldBe listOf("dependentsCount")

        val percentile111 = res11["dependentsCount"] as Double
        percentile111 shouldBe 0.0

        // scenario #2: percentile of values per columns separately
        val res3 = personsDf.percentileFor(
            percentile,
            "weight",
            "workExperienceYears",
            "dependentsCount",
            "annualIncome",
            "name",
        )
        res3.columnNames() shouldBe listOf("weight", "workExperienceYears", "dependentsCount", "annualIncome", "name")

        val percentile31 = res3["weight"] as Double
        percentile31 shouldBe 61.52133333333333
        val percentile32 = res3["workExperienceYears"] as Double
        percentile32 shouldBe 1.4333333333333336
        val percentile33 = res3["dependentsCount"] as Double
        percentile33 shouldBe 0.0
        val percentile34 = res3["annualIncome"] as Double
        percentile34 shouldBe 5200.000000000003
        val percentile35 = res3["name"] as String
        percentile35 shouldBe "Charlie"
    }

    @Test
    fun `statistics cache for ValueColumn, stats functions read the cache`() {
        // test idea: put in the cache nonsense values. If stats functions return them values, no computation was done
        val valueColumn = columnOf(1, 4, 3, 8) as ValueColumnInternal
        // max
        valueColumn.putStatisticCache("max", mapOf("skipNaN" to false), StatisticResult(20))
        valueColumn.max(false) shouldBe 20
        // min
        valueColumn.putStatisticCache("min", mapOf("skipNaN" to false), StatisticResult(20))
        valueColumn.min(false) shouldBe 20
        // mean
        valueColumn.putStatisticCache("mean", mapOf("skipNaN" to false), StatisticResult(20))
        valueColumn.mean(false) shouldBe 20
        // sum
        valueColumn.putStatisticCache("sum", mapOf("skipNaN" to false), StatisticResult(0))
        valueColumn.sum() shouldBe 0
        // std
        valueColumn.putStatisticCache("std", mapOf("skipNaN" to false, "ddof" to 1), StatisticResult(100))
        valueColumn.std(false, 1) shouldBe 100
        // percentile
        valueColumn.putStatisticCache(
            "percentile", mapOf("skipNaN" to false, "percentile" to 30.0),
            StatisticResult(100.0),
        )
        valueColumn.percentile(30.0, false) shouldBe 100.0
        // median
        valueColumn.putStatisticCache("median", mapOf("skipNaN" to false), StatisticResult(20.0))
        valueColumn.median(false) shouldBe 20.0
    }

    @Test
    fun `statistics cache for ValueColumn, stats functions write to the cache`() {
        val valueColumn = columnOf(3, 1, 2) as ValueColumnInternal
        // max
        valueColumn.max(false) shouldBe 3
        valueColumn.getStatisticCacheOrNull("max", mapOf("skipNaN" to false))?.value shouldBe 3
        // min
        valueColumn.min(false) shouldBe 1
        valueColumn.getStatisticCacheOrNull("min", mapOf("skipNaN" to false))?.value shouldBe 1
        // mean
        valueColumn.mean(false) shouldBe 2
        valueColumn.getStatisticCacheOrNull("mean", mapOf("skipNaN" to false))?.value shouldBe 2
        // sum
        valueColumn.sum(false) shouldBe 6
        valueColumn.getStatisticCacheOrNull("sum", mapOf("skipNaN" to false))?.value shouldBe 6
        // std
        valueColumn.std(false, 1) shouldBe 1.0
        valueColumn.getStatisticCacheOrNull("std", mapOf("skipNaN" to false, "ddof" to 1))?.value shouldBe 1.0
        // percentile
        valueColumn.percentile(6.0, false) shouldBe 1.0
        valueColumn.getStatisticCacheOrNull(
            "percentile", mapOf("skipNaN" to false, "percentile" to 6.0),
        )?.value shouldBe 1.0
        // median
        valueColumn.median(false) shouldBe 2
        valueColumn.getStatisticCacheOrNull("median", mapOf("skipNaN" to false))?.value shouldBe 2
    }

    @Test
    fun `statistics cache for ValueColumn, cache is correctly exploited in a DataFrame context`() {
        // generic situation where statistic function is called one time for each row
        val filteredDf = personsDf.filter { it["age"] == personsDf["age"].cast<Int>().max() }
        filteredDf.rowsCount() shouldBe 1
        personsDf["age"].asValueColumn().internalValueColumn()
            .getStatisticCacheOrNull("max", mapOf("skipNaN" to false))?.value shouldBe 100
        // dataframe-wide statistic function
        personsDf.min { "age"<Int>() } shouldBe 1
        personsDf["age"].asValueColumn().internalValueColumn()
            .getStatisticCacheOrNull("min", mapOf("skipNaN" to false))?.value shouldBe 1
    }

    @Test
    fun `statistics cache for ValueColumn, preserve statistics cache when changing type or renaming`() {
        val valueColumn = columnOf(3, 1, 2)
        valueColumn.min(false) shouldBe 1
        // derived columns
        val renamedColumn = valueColumn.rename("newName").asValueColumn().internalValueColumn()
        val colWithDifferentType = ((valueColumn as ResolvingValueColumn).source as ValueColumnImpl)
            .changeType(typeOf<Double>())
        // tests
        valueColumn.asValueColumn().internalValueColumn()
            .getStatisticCacheOrNull("min", mapOf("skipNaN" to false))?.value shouldBe 1
        renamedColumn.getStatisticCacheOrNull("min", mapOf("skipNaN" to false))?.value shouldBe 1
        colWithDifferentType.getStatisticCacheOrNull("min", mapOf("skipNaN" to false))?.value shouldBe 1
    }
}
