package org.jetbrains.kotlinx.dataframe.testSets.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.diff
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.intoList
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.namedValues
import org.jetbrains.kotlinx.dataframe.api.namedValuesOf
import org.jetbrains.kotlinx.dataframe.api.next
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.api.relative
import org.jetbrains.kotlinx.dataframe.api.rowMean
import org.jetbrains.kotlinx.dataframe.api.rowStd
import org.jetbrains.kotlinx.dataframe.api.rowSum
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.api.transposeTo
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.value
import org.jetbrains.kotlinx.dataframe.api.valuesOf
import org.jetbrains.kotlinx.dataframe.api.with
import org.junit.Test
import kotlin.math.sqrt

class DataRowTests : BaseTest() {

    @Test
    fun prevNext() {
        typed.update { age }.with { prev()?.age }.age.drop(1) shouldBe typed.age.dropLast(1)
        typed.update { age }.with { next()?.age }.age.dropLast(1) shouldBe typed.age.drop(1)
    }

    @Test
    fun diff() {
        typed.update { age }.with { diff { age } }.age.drop(1).values() shouldBe typed.age.values()
            .zipWithNext { curr, next -> next - curr }
    }

    @Test
    fun mean() {
        typed.mapToColumn("mean") { rowMean() }.values() shouldBe typed.age.values()
            .zip(typed.weight.values()) { a, b -> if (b != null) (a + b) / 2.0 else a }
    }

    @Test
    fun std() {
        typed.mapToColumn("std") { rowStd(skipNA = true, ddof = 0) }.values() shouldBe typed.age.values()
            .zip(typed.weight.values()) { a, b ->
                if (b == null) .0
                else {
                    val mean = (a + b) / 2.0
                    sqrt(((a - mean) * (a - mean) + (b - mean) * (b - mean)) / 2)
                }
            }
    }

    @Test
    fun sum() {
        typed.convert { weight }.toDouble()
            .mapToColumn("sum") { rowSum() }.values() shouldBe typed.age.values().zip(typed.weight.values()) { a, b -> a + (b ?: 0).toDouble() }
    }

    @Test
    fun namedValuesOf() {
        typed.mapToColumn("vals") {
            namedValuesOf<Int>().map { it.value }
        }.values() shouldBe typed.merge { age and weight }.by { it.filterNotNull() }.intoList()
    }

    @Test
    fun valuesOf() {
        typed.mapToColumn("vals") {
            valuesOf<String>()
        }.values() shouldBe typed.merge { name and city }.by { it.filterNotNull() }.intoList()
    }

    @Test
    fun namedValuesFilter() {
        typed.mapToColumn("vals") {
            namedValues().firstOrNull { it.value == null }?.name
        } shouldBe typed.mapToColumn("vals") {
            val firstNullIndex = values().indexOfFirst { it == null }
            if (firstNullIndex == -1) null else columnNames()[firstNullIndex]
        }
    }

    @Test
    fun transposeTo() {
        val df = dataFrameOf("a", "b")(1, 2).first().transposeTo<Int>()
        df.name.toList() shouldBe listOf("a", "b")
        df.value.toList() shouldBe listOf(1, 2)
    }

    @Test
    fun relativeTest() {
        typed[1].relative(0..0) shouldBe typed[1..1]
        typed[1].relative(-2..2) shouldBe typed[0..3]
        typed[1].relative(listOf(2, -1, -3, 0)) shouldBe typed[3, 0, 1]
    }
}
