package org.jetbrains.kotlinx.dataframe.testSets.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.sort
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sortDesc
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.junit.Test

class DataColumnTests : BaseTest() {

    @Test
    fun `sort column`() {
        typed.age.sort() shouldBe typed.sortBy { age }.age
        typed.age.sortDesc() shouldBe typed.sortByDesc { age }.age
    }

    @Test
    fun `value counts`() {
        val languages by columnOf("Kotlin", "Kotlin", null, null, "C++")
        val languageCounts = languages.valueCounts()
        languageCounts[languages].values() shouldBe listOf("Kotlin", "C++")
        languageCounts.count shouldBe listOf(2, 1)
    }
}
