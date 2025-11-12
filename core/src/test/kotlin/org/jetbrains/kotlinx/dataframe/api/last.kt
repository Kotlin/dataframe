package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

/**
 * This class tests the behavior of the `last` (`lastCol`) and `lastOrNull` functions, including:
 * - **ColumnsSelectionDsl**: selecting the last column or last column matching a condition, with invocations
 * on illegal types, and in case when no column matches the condition.
 * - **DataColumn**: getting the last value or the last value matching a predicate,
 * verifying behavior on empty columns, columns with `null` values, and columns without values matching the predicate.
 * - **DataFrame**: getting the last row or last matching row,
 * verifying behavior on empty DataFrames, DataFrames with `null` values, and
 * DataFrames without rows matching the predicate.
 * - **GroupBy**: reducing each group to its last row or the last row matching a predicate,
 * with handling groups that contain no matching rows.
 * - **Pivot**: reducing each group in the pivot to its last row or the last row matching a predicate,
 * with handling groups that contain no matching rows.
 * - **PivotGroupBy**: reducing each combined [pivot] + [groupBy] group to its last row
 * or the last row matching a predicate, with handling [pivot] + [groupBy] combinations that contain no matching rows.
 */
class LastTests : ColumnsSelectionDslTests() {

    // region ColumnsSelectionDsl

    @Test
    fun `ColumnsSelectionDsl last`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".lastCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { columnGroup(Person::age).lastCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.lastCol() }
        }
        shouldThrow<NoSuchElementException> {
            df.select { last { false } }
        }

        listOf(
            df.select { isHappy },
            df.select { last() },
            df.select { all().last() },
            df.select { last { it.name().startsWith("is") } },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.colsOf<String>().last { col -> col.any { it == "Alice" } } },
            df.select { name.lastCol { col -> col.any { it == "Alice" } } },
            df.select { "name".lastCol { col -> col.any { it == "Alice" } } },
            df.select { Person::name.lastCol { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.lastCol { col -> col.any { it == "Alice" } } },
            df.remove { name.lastName }.select { pathOf("name").lastCol() },
            df.select { pathOf("name").lastCol { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().lastCol { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }

    // endregion

    // region DataColumn

    @Test
    fun `last on DataColumn`() {
        df.name.lastName.last() shouldBe "Byrd"
        df.age.last { it > 30 } shouldBe 40
        shouldThrow<IndexOutOfBoundsException> {
            df.drop(df.nrow).isHappy.last()
        }
    }

    @Test
    fun `lastOrNull on DataColumn`() {
        df.name.lastName.lastOrNull() shouldBe "Byrd"
        df.take(4).weight.lastOrNull() shouldBe null
        df.drop(df.nrow).age.lastOrNull() shouldBe null
        df.age.lastOrNull { it > 30 } shouldBe 40
        df.age.lastOrNull { it > 50 } shouldBe null
    }

    // endregion

    // region DataFrame

    @Test
    fun `last on DataFrame`() {
        df.last().name.lastName shouldBe "Byrd"
        df.last { !isHappy }.name.lastName shouldBe "Wolf"
        shouldThrow<NoSuchElementException> {
            df.drop(df.nrow).last()
        }
        shouldThrow<NoSuchElementException> {
            df.last { age > 50 }
        }
        shouldThrow<NoSuchElementException> {
            df.drop(df.nrow).last { isHappy }
        }
    }

    @Test
    fun `lastOrNull on DataFrame`() {
        df.lastOrNull()?.name?.lastName shouldBe "Byrd"
        df.take(6).lastOrNull()?.city shouldBe null
        df.drop(df.nrow).lastOrNull() shouldBe null
        df.lastOrNull { !isHappy }?.name?.lastName shouldBe "Wolf"
        df.lastOrNull { age > 50 } shouldBe null
        df.drop(df.nrow).lastOrNull { isHappy } shouldBe null
    }

    // endregion

    // region GroupBy

    @Test
    fun `last on GroupBy`() {
        val grouped = df.groupBy { isHappy }
        val reducedGrouped = grouped.last()
        val lastHappy = reducedGrouped.values()[0]
        val lastUnhappy = reducedGrouped.values()[1]
        lastHappy shouldBe dataFrameOf(
            "isHappy" to columnOf(true),
            "name" to columnOf(
                "firstName" to columnOf("Charlie"),
                "lastName" to columnOf("Byrd"),
            ),
            "age" to columnOf(30),
            "city" to columnOf("Moscow"),
            "weight" to columnOf(90),
        )[0]
        lastUnhappy shouldBe dataFrameOf(
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
    fun `last on GroupBy with predicate`() {
        val grouped = df.groupBy { isHappy }
        val reducedGrouped = grouped.last { "age"<Int>() < 21 && it["city"] != "Moscow" }
        val lastHappy = reducedGrouped.values()[0]
        val lastUnhappy = reducedGrouped.values()[1]
        lastHappy shouldBe dataFrameOf(
            "isHappy" to columnOf(true),
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        lastUnhappy shouldBe dataFrameOf(
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
    fun `last on GroupBy with predicate without match`() {
        val grouped = df.groupBy { isHappy }
        val reducedGrouped = grouped.last { it["city"] == "London" }
        val lastHappy = reducedGrouped.values()[0]
        val lastUnhappy = reducedGrouped.values()[1]
        lastHappy shouldBe dataFrameOf(
            "isHappy" to columnOf(true),
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        lastUnhappy shouldBe dataFrameOf(
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
    fun `last on Pivot`() {
        val pivot = df.pivot { isHappy }
        val reducedPivot = pivot.last()
        val lastHappy = reducedPivot.values()[0]
        val lastUnhappy = reducedPivot.values()[1]
        lastHappy shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Charlie"),
                "lastName" to columnOf("Byrd"),
            ),
            "age" to columnOf(30),
            "city" to columnOf("Moscow"),
            "weight" to columnOf(90),
        )[0]
        lastUnhappy shouldBe dataFrameOf(
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
    fun `last on Pivot with predicate`() {
        val pivot = df.pivot { isHappy }
        val reducedPivot = pivot.last { "age"<Int>() < 21 && it["city"] != "Moscow" }
        val lastHappy = reducedPivot.values()[0]
        val lastUnhappy = reducedPivot.values()[1]
        lastHappy shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        lastUnhappy shouldBe dataFrameOf(
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
    fun `last on Pivot with predicate without match`() {
        val pivot = df.pivot { isHappy }
        val reducedPivot = pivot.last { it["city"] == "London" }
        val lastHappy = reducedPivot.values()[0]
        val lastUnhappy = reducedPivot.values()[1]
        lastHappy shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(15),
            "city" to columnOf("London"),
            "weight" to columnOf(54),
        )[0]
        lastUnhappy shouldBe dataFrameOf(
            "name" to columnOf(null),
            "age" to columnOf(null),
            "city" to columnOf(null),
            "weight" to columnOf(null),
        )[0]
    }

    // endregion

    // region PivotGroupBy

    @Test
    fun `last on PivotGroupBy`() {
        val students = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Alice", "Alice", "Bob", "Bob", "Bob", "Bob"),
            "age" to columnOf(15, 15, 20, 20, 15, 15, 20, 20),
            "group" to columnOf(1, 2, 1, 2, 1, 2, 1, 2),
        )
        val studentsPivotGrouped = students.pivot("age").groupBy("name")
        val studentsPivotGroupedReduced = studentsPivotGrouped.last().values()
        val expectedDf = dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf(
                "15" to columnOf(2, 2),
                "20" to columnOf(2, 2),
            ),
        )
        studentsPivotGroupedReduced shouldBe expectedDf
    }

    @Test
    fun `last on PivotGroupBy with predicate`() {
        val students = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Alice", "Alice", "Bob", "Bob", "Bob", "Bob"),
            "age" to columnOf(15, 15, 20, 20, 15, 15, 20, 20),
            "group" to columnOf(1, 2, 1, 2, 1, 2, 1, 2),
        )
        val studentsPivotGrouped = students.pivot("age").groupBy("name")
        val studentsPivotGroupedReduced = studentsPivotGrouped.last { it["group"] == 1 }.values()
        val expected = dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf(
                "15" to columnOf(1, 1),
                "20" to columnOf(1, 1),
            ),
        )
        studentsPivotGroupedReduced shouldBe expected
    }

    @Test
    fun `last on PivotGroupBy with predicate without match`() {
        val students = dataFrameOf(
            "name" to columnOf("Alice", "Alice", "Alice", "Alice", "Bob", "Bob", "Bob", "Bob"),
            "age" to columnOf(15, 15, 20, 20, 15, 15, 20, 20),
            "group" to columnOf(1, 2, 1, 2, 1, 2, 1, 2),
        )
        val studentsPivotGrouped = students.pivot("age").groupBy("name")
        val studentsPivotGroupedReduced = studentsPivotGrouped.last { it["group"] == 3 }.values()
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
