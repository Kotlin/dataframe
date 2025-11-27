package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

/**
 * Tests the behavior of the `first` (`firstCol`) and `firstOrNull` functions, including:
 *
 * - **ColumnsSelectionDsl**: selecting the first column or first column matching a condition, with invocations
 * on illegal types, and in case when no column matches the condition.
 *
 * - **DataColumn**: getting the first value or the first value matching a predicate,
 * verifying behavior on empty columns, columns with `null` values, and columns without values matching the predicate.
 *
 * - **DataFrame**: getting the first row or first matching row,
 * verifying behavior on empty DataFrames, DataFrames with `null` values, and
 * DataFrames without rows matching the predicate.
 *
 * - **GroupBy**: reducing each group to its first row or the first row matching a predicate,
 * with handling groups that contain no matching rows.
 *
 * - **Pivot**: reducing each group in the pivot to its first row or the first row matching a predicate,
 * with handling groups that contain no matching rows.
 *
 * - **PivotGroupBy**: reducing each combined [pivot] + [groupBy] group to its first row
 * or the first row matching a predicate, with handling [pivot] + [groupBy] combinations that contain no matching rows.
 */
class FirstTests : ColumnsSelectionDslTests() {

    // region ColumnsSelectionDsl

    @Test
    fun `ColumnsSelectionDsl first`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".firstCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { columnGroup(Person::age).firstCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.firstCol() }
        }
        shouldThrow<NoSuchElementException> {
            df.select { first { false } }
        }

        listOf(
            df.select { name },
            df.select { first() },
            df.select { all().first() },
            df.select { first { it.name().startsWith("n") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.colsOf<String>().first { col -> col.any { it == "Alice" } } },
            df.select { name.firstCol { col -> col.any { it == "Alice" } } },
            df.select { "name".firstCol { col -> col.any { it == "Alice" } } },
            df.select { Person::name.firstCol { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.firstCol { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").firstCol() },
            df.select { pathOf("name").firstCol { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().firstCol { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }

    // endregion

    // region DataColumn

    @Test
    fun `first on DataColumn`() {
        df.name.lastName.first() shouldBe "Cooper"
        df.age.first { it in 18..<40 } shouldBe 20
        shouldThrow<IndexOutOfBoundsException> {
            df.drop(df.nrow).isHappy.first()
        }
        shouldThrow<NoSuchElementException> {
            df.age.first { it > 50 }
        }
    }

    @Test
    fun `firstOrNull on DataColumn`() {
        df.name.lastName.firstOrNull() shouldBe "Cooper"
        df.drop(2).weight.firstOrNull() shouldBe null
        df.drop(df.nrow).age.firstOrNull() shouldBe null
        df.age.firstOrNull { it in 21..30 } shouldBe 30
        df.age.firstOrNull { it > 50 } shouldBe null
    }

    // endregion

    // region DataFrame

    @Test
    fun `first on DataFrame`() {
        df.first().name.lastName shouldBe "Cooper"
        df.first { !isHappy }.name.lastName shouldBe "Daniels"
        shouldThrow<NoSuchElementException> {
            df.drop(df.nrow).first()
        }
        shouldThrow<NoSuchElementException> {
            df.first { age > 50 }
        }
        shouldThrow<NoSuchElementException> {
            df.drop(df.nrow).first { isHappy }
        }
    }

    @Test
    fun `firstOrNull on DataFrame`() {
        df.firstOrNull()?.name?.lastName shouldBe "Cooper"
        df.drop(df.nrow).firstOrNull() shouldBe null
        df.firstOrNull { !isHappy }?.name?.lastName shouldBe "Daniels"
        df.firstOrNull { age > 50 } shouldBe null
        df.drop(df.nrow).firstOrNull { isHappy } shouldBe null
    }

    // endregion

    // region GroupBy

    // not tested on an empty dataframe because of #1531
    @Test
    fun `first on GroupBy`() {
        val grouped = df.groupBy { isHappy }
        val reducedGrouped = grouped.first()
        val firstHappy = reducedGrouped.values()[0]
        val firstUnhappy = reducedGrouped.values()[1]
        firstHappy shouldBe dataFrameOf(
            "isHappy" to columnOf(true),
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        firstUnhappy shouldBe dataFrameOf(
            "isHappy" to columnOf(false),
            "name" to columnOf(
                "firstName" to columnOf("Charlie"),
                "lastName" to columnOf("Daniels"),
            ),
            "age" to columnOf(20),
            "city" to columnOf("Moscow"),
            "weight" to columnOf(null),
        )[0]
    }

    @Test
    fun `first on GroupBy with predicate`() {
        val grouped = df.groupBy { isHappy }
        val reducedGrouped = grouped.first { it["age"] as Int > 17 && it["city"] != "Moscow" }
        val firstHappy = reducedGrouped.values()[0]
        val firstUnhappy = reducedGrouped.values()[1]
        firstHappy shouldBe dataFrameOf(
            "isHappy" to columnOf(true),
            "name" to columnOf(
                "firstName" to columnOf("Bob"),
                "lastName" to columnOf("Dylan"),
            ),
            "age" to columnOf(45),
            "city" to columnOf("Dubai"),
            "weight" to columnOf(87),
        )[0]
        firstUnhappy shouldBe dataFrameOf(
            "isHappy" to columnOf(false),
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Wolf"),
            ),
            "age" to columnOf(20),
            "city" to columnOf(null),
            "weight" to columnOf(55),
        )[0]
    }

    @Test
    fun `first on GroupBy with predicate without match`() {
        val grouped = df.groupBy { isHappy }
        val reducedGrouped = grouped.first { it["city"] == "London" }
        val firstHappy = reducedGrouped.values()[0]
        val firstUnhappy = reducedGrouped.values()[1]
        firstHappy shouldBe dataFrameOf(
            "isHappy" to columnOf(true),
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        firstUnhappy shouldBe dataFrameOf(
            "isHappy" to columnOf(false),
            "name" to columnOf(
                "firstName" to columnOf(null),
                "lastName" to columnOf(null),
            ),
            "age" to columnOf(null),
            "city" to columnOf(null),
            "weight" to columnOf(null),
        )[0]
    }

    // endregion

    // region Pivot

    @Test
    fun `first on Pivot`() {
        val pivot = df.pivot { isHappy }
        val reducedPivot = pivot.first()
        val firstHappy = reducedPivot.values()[0]
        val firstUnhappy = reducedPivot.values()[1]
        firstHappy shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        firstUnhappy shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Charlie"),
                "lastName" to columnOf("Daniels"),
            ),
            "age" to columnOf(20),
            "city" to columnOf("Moscow"),
            "weight" to columnOf(null),
        )[0]
    }

    @Test
    fun `first on Pivot with predicate`() {
        val pivot = df.pivot { isHappy }
        val reducedPivotAdults = pivot.first { age > 17 }
        val firstHappyAdult = reducedPivotAdults.values()[0]
        val firstUnhappyAdult = reducedPivotAdults.values()[1]
        firstHappyAdult shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Bob"),
                "lastName" to columnOf("Dylan"),
            ),
            "age" to columnOf(45),
            "city" to columnOf("Dubai"),
            "weight" to columnOf(87),
        )[0]
        firstUnhappyAdult shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Charlie"),
                "lastName" to columnOf("Daniels"),
            ),
            "age" to columnOf(20),
            "city" to columnOf("Moscow"),
            "weight" to columnOf(null),
        )[0]
    }

    @Test
    fun `first on Pivot with predicate without match`() {
        val pivot = df.pivot { isHappy }
        val reducedPivot = pivot.first { it["city"] == "London" }
        val firstHappy = reducedPivot.values()[0]
        val firstUnhappy = reducedPivot.values()[1]
        firstHappy shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        firstUnhappy shouldBe dataFrameOf(
            "name" to columnOf(null),
            "age" to columnOf(null),
            "city" to columnOf(null),
            "weight" to columnOf(null),
        )[0]
    }

    // endregion

    // region PivotGroupBy

    @Test
    fun `first on PivotGroupBy`() {
        val students = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Alice", "Alice", "Bob", "Bob", "Bob", "Bob"),
            "age" to columnOf(15, 15, 20, 20, 15, 15, 20, 20),
            "group" to columnOf(1, 2, 1, 2, 1, 2, 1, 2),
        )
        val studentsPivotGrouped = students.pivot("age").groupBy("name")
        val studentsPivotGroupedReduced = studentsPivotGrouped.first().values()
        val expectedDf = dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf(
                "15" to columnOf(1, 1),
                "20" to columnOf(1, 1),
            ),
        )
        studentsPivotGroupedReduced shouldBe expectedDf
    }

    @Test
    fun `first on PivotGroupBy with predicate`() {
        val students = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Alice", "Alice", "Bob", "Bob", "Bob", "Bob"),
            "age" to columnOf(15, 15, 20, 20, 15, 15, 20, 20),
            "group" to columnOf(1, 2, 1, 2, 1, 2, 1, 2),
        )
        val studentsPivotGrouped = students.pivot("age").groupBy("name")
        val studentsPivotGroupedReduced = studentsPivotGrouped.first { it["group"] == 2 }.values()
        val expected = dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf(
                "15" to columnOf(2, 2),
                "20" to columnOf(2, 2),
            ),
        )
        studentsPivotGroupedReduced shouldBe expected
    }

    @Test
    fun `first on PivotGroupBy with predicate without match`() {
        val students = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Alice", "Alice", "Bob", "Bob", "Bob", "Bob"),
            "age" to columnOf(15, 15, 20, 20, 15, 15, 20, 20),
            "group" to columnOf(1, 2, 1, 2, 1, 2, 1, 2),
        )
        val studentsPivotGrouped = students.pivot("age").groupBy("name")
        val studentsPivotGroupedReduced = studentsPivotGrouped.first { it["group"] == 3 }.values()
        val expected = dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf(
                "15" to columnOf(null, null),
                "20" to columnOf(null, null),
            ),
        )
        studentsPivotGroupedReduced shouldBe expected
    }

    // endregion
}
