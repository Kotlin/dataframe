package org.jetbrains.kotlinx.dataframe.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.sort
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sortDesc
import org.junit.Test

class DataColumnTests : BaseTest() {

    @Test
    fun `sort column`() {
        typed.age.sort() shouldBe typed.sortBy { age }.age
        typed.age.sortDesc() shouldBe typed.sortByDesc { age }.age
    }
}
