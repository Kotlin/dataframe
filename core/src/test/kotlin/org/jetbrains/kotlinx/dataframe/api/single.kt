package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

/**
 * Tests the behavior of the [single] ([SingleColumnsSelectionDsl.singleCol]) and [singleOrNull] functions,
 * including:
 *
 * - [ColumnsSelectionDsl]: selecting the only column or the only column matching a condition, with invocations
 * on illegal types, and in case when no column matches the condition.
 *
 * - [DataColumn]: getting the only value, including a single `null` value,
 * and verifying behavior on empty columns and on columns with more than one value.
 *
 * - [DataFrame]: getting the only [row][DataRow] or the only matching [row][DataRow],
 * verifying behavior on empty DataFrames, on DataFrames with more than one row,
 * and on DataFrames without rows matching the predicate or with several of them,
 * as well as the messages of the thrown exceptions.
 */
class SingleTests : ColumnsSelectionDslTests() {

    private val emptyDf = df.take(0)

    // region ColumnsSelectionDsl

    @Test
    fun `ColumnsSelectionDsl single`() {
        shouldThrow<IllegalArgumentException> {
            df.select { "age".singleCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { columnGroup(Person::age).singleCol() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { Person::age.singleCol() }
        }
        shouldThrow<NoSuchElementException> {
            df.select { all().filter { false }.single() }
        }
        shouldThrow<IllegalArgumentException> {
            df.select { single() }
        }

        val singleDf = df.select { take(1) }

        listOf(
            df.select { name },
            singleDf.select { name },
            singleDf.select { single() },
            singleDf.select { all().single() },
            df.select { all().filter { it.name().startsWith("n") }.single() },
        ).shouldAllBeEqual()

        listOf(
            df.select { name.firstName },
            df.select { name.colsOf<String>().filter { col -> col.any { it == "Alice" } }.single() },
            df.select { name.singleCol { col -> col.any { it == "Alice" } } },
            df.select { "name".singleCol { col -> col.any { it == "Alice" } } },
            df.select { Person::name.singleCol { col -> col.any { it == "Alice" } } },
            df.select { NonDataSchemaPerson::name.singleCol { col -> col.any { it == "Alice" } } },
            df.select { pathOf("name").singleCol { col -> col.any { it == "Alice" } } },
            df.select { it["name"].asColumnGroup().singleCol { col -> col.any { it == "Alice" } } },
        ).shouldAllBeEqual()
    }

    // endregion

    // region DataColumn

    @Test
    fun `single on DataColumn`() {
        // the receiver is built explicitly so that the DataColumn overload is the one being called
        val lastNames: DataColumn<String> = df.take(1).name.lastName
        val lastName: String = lastNames.single()
        lastName shouldBe "Cooper"
    }

    @Test
    fun `single on empty DataColumn throws`() {
        val empty: DataColumn<Int> = emptyDf.age
        shouldThrow<NoSuchElementException> {
            empty.single()
        }
    }

    @Test
    fun `single on DataColumn with more than one value throws`() {
        val ages: DataColumn<Int> = df.age
        shouldThrow<IllegalArgumentException> {
            ages.single()
        }
    }

    @Test
    fun `single on DataColumn with a single null value returns null`() {
        // `weight` is nullable, and the third row, Charlie Daniels, has no weight
        val weights: DataColumn<Int?> = df[2..2].weight
        val weight: Int? = weights.single()
        weight shouldBe null
    }

    // endregion

    // region DataFrame

    @Test
    fun `single on DataFrame`() {
        val row: DataRow<Person> = df.take(1).single()
        row.name.lastName shouldBe "Cooper"
    }

    @Test
    fun `single on empty DataFrame throws`() {
        shouldThrow<NoSuchElementException> {
            emptyDf.single()
        }.message shouldBe "DataFrame has no rows. Use `singleOrNull`."
    }

    @Test
    fun `single on DataFrame with more than one row throws`() {
        shouldThrow<IllegalArgumentException> {
            df.single()
        }.message shouldBe "DataFrame has more than one row."
    }

    @Test
    fun `singleOrNull on DataFrame`() {
        val row: DataRow<Person>? = df.take(1).singleOrNull()
        row?.name?.lastName shouldBe "Cooper"
    }

    @Test
    fun `singleOrNull on empty DataFrame returns null`() {
        emptyDf.singleOrNull() shouldBe null
    }

    @Test
    fun `singleOrNull on DataFrame with more than one row returns null`() {
        df.singleOrNull() shouldBe null
    }

    @Test
    fun `single on DataFrame with predicate`() {
        // only Bob Dylan is 45
        val row: DataRow<Person> = df.single { age == 45 }
        row.name.lastName shouldBe "Dylan"
    }

    @Test
    fun `single on DataFrame without a matching row throws`() {
        shouldThrow<NoSuchElementException> {
            df.single { age > 50 }
        }
    }

    @Test
    fun `single on DataFrame with more than one matching row throws`() {
        // both Charlie Daniels and Alice Wolf are 20
        shouldThrow<IllegalArgumentException> {
            df.single { age == 20 }
        }
    }

    @Test
    fun `singleOrNull on DataFrame with predicate`() {
        val row: DataRow<Person>? = df.singleOrNull { age == 45 }
        row?.name?.lastName shouldBe "Dylan"
    }

    @Test
    fun `singleOrNull on DataFrame without a matching row returns null`() {
        df.singleOrNull { age > 50 } shouldBe null
    }

    @Test
    fun `singleOrNull on DataFrame with more than one matching row returns null`() {
        df.singleOrNull { age == 20 } shouldBe null
    }

    @Test
    fun `single on empty DataFrame with predicate throws`() {
        shouldThrow<NoSuchElementException> {
            emptyDf.single { isHappy }
        }
    }

    @Test
    fun `singleOrNull on empty DataFrame with predicate returns null`() {
        emptyDf.singleOrNull { isHappy } shouldBe null
    }

    @Test
    fun `single on DataFrame with predicate on a nested column`() {
        // lastName lives inside the `name` column group
        val row: DataRow<Person> = df.single { name.lastName == "Dylan" }
        row.age shouldBe 45
    }

    @Test
    fun `single on DataFrame with predicate on a nullable column`() {
        // Charlie Chaplin is the only 40-year-old, and that row has no weight
        val row: DataRow<Person> = df.single { age == 40 && weight == null }
        row.name.lastName shouldBe "Chaplin"
    }

    @Test
    fun `single on DataFrame with more than one row matching on a nullable column throws`() {
        // the weights of both Charlie Daniels and Charlie Chaplin are unknown
        shouldThrow<IllegalArgumentException> {
            df.single { weight == null }
        }
    }

    @Test
    fun `single on DataFrame with predicate written with the String column access API`() {
        val row: DataRow<Person> = df.single { "age"<Int>() == 45 }
        row.name.lastName shouldBe "Dylan"
    }

    @Test
    fun `single on DataFrame with predicate that uses the row as it`() {
        val row: DataRow<Person> = df.single { it.age == 45 }
        row.name.lastName shouldBe "Dylan"
    }

    // endregion
}
