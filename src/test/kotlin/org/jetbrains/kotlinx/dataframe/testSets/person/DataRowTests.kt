package org.jetbrains.kotlinx.dataframe.testSets.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.diff
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.dropLast
import org.jetbrains.kotlinx.dataframe.api.intoList
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.namedValues
import org.jetbrains.kotlinx.dataframe.api.namedValuesOf
import org.jetbrains.kotlinx.dataframe.api.next
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.api.rowMean
import org.jetbrains.kotlinx.dataframe.api.rowStd
import org.jetbrains.kotlinx.dataframe.api.rowSum
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.api.valuesOf
import org.jetbrains.kotlinx.dataframe.api.with
import org.junit.Test

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
        typed.map("mean") { rowMean() }.values() shouldBe typed.age.values()
            .zip(typed.weight.values()) { a, b -> if (b != null) (a + b) / 2.0 else a }
    }

    @Test
    fun std() {
        typed.map("std") { rowStd() }.values() shouldBe typed.age.values()
            .zip(typed.weight.values()) { a, b ->
                if (b == null) .0
                else {
                    val mean = (a + b) / 2.0
                    Math.sqrt((a - mean) * (a - mean) + (b - mean) * (b - mean))
                }
            }
    }

    @Test
    fun sum() {
        typed.convert { weight }.toDouble()
            .map("sum") { rowSum() }.values() shouldBe typed.age.values().zip(typed.weight.values()) { a, b -> a + (b ?: 0).toDouble() }
    }

    @Test
    fun namedValuesOf() {
        typed.map("vals") {
            namedValuesOf<Int>().map { it.value }
        }.values() shouldBe typed.merge { age and weight }.by { it.filterNotNull() }.intoList()
    }

    @Test
    fun valuesOf() {
        typed.map("vals") {
            valuesOf<String>()
        }.values() shouldBe typed.merge { name and city }.by { it.filterNotNull() }.intoList()
    }

    @Test
    fun namedValuesFilter() {
        typed.map("vals") {
            namedValues().firstOrNull { it.value == null }?.name
        } shouldBe typed.map("vals") {
            val firstNullIndex = values().indexOfFirst { it == null }
            if (firstNullIndex == -1) null else columnNames()[firstNullIndex]
        }
    }
}
