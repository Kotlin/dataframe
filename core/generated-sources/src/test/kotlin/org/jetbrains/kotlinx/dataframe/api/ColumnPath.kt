package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columns.dropOverlappingStartOfChild
import org.jetbrains.kotlinx.dataframe.columns.dropStartWrt
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.junit.Test

class ColumnPathTests : TestBase() {
    @Test
    fun `should trim overlapping start of first list from the end of second list`() {
        val parent = pathOf("something", "name", "firstName")
        val child = pathOf("name", "firstName", "secondName")

        dropOverlappingStartOfChild(parent, child) shouldBe listOf("secondName")
        child.dropStartWrt(parent) shouldBe pathOf("secondName")
    }

    @Test
    fun `should return first list as is when there is no overlap`() {
        val parent = pathOf("city", "country")
        val child = pathOf("name", "firstName", "secondName")

        dropOverlappingStartOfChild(parent, child) shouldBe listOf("name", "firstName", "secondName")
        child.dropStartWrt(parent) shouldBe pathOf("name", "firstName", "secondName")
    }

    @Test
    fun `should return empty list when first list is completely overlapped`() {
        val parent = pathOf("city", "name", "firstName")
        val child = pathOf("name", "firstName")

        dropOverlappingStartOfChild(parent, child) shouldBe emptyList()
        child.dropStartWrt(parent) shouldBe pathOf()
    }

    @Test
    fun `if parent is empty`() {
        val parent = pathOf()
        val child = pathOf("name", "firstName")

        dropOverlappingStartOfChild(parent, child) shouldBe listOf("name", "firstName")
        child.dropStartWrt(parent) shouldBe pathOf("name", "firstName")
    }
}
