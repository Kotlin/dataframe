package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.junit.Test

class Recursively : TestBase() {
    private val recursivelyGoal = dfGroup.select { dfs { true } }.reorderColumnsBy { name }.alsoDebug() // correct
    private val recursivelyNoGroups = dfGroup.select { allDfs(false) }.reorderColumnsBy { name }.alsoDebug() // correct
    private val recursivelyString = dfGroup.select { dfsOf<String?>() }.reorderColumnsBy { name }.alsoDebug()

    @Test
    fun `recursively all`() {
//        df.select { all().recursively() }.alsoDebug() shouldBe recursivelyGoal
//        df.select { cols().recursively() }.alsoDebug() shouldBe recursivelyGoal
//
//        df.select { allRecursively() }.alsoDebug() shouldBe recursivelyGoal
//        df.select { all().allRecursively() }.alsoDebug() shouldBe recursivelyGoal
//        df.select { cols().allRecursively() }.alsoDebug() shouldBe recursivelyGoal

//        df.select { dfs { true } }.alsoDebug() // correct
//        df.select { all().dfs { true } }.alsoDebug() // incorrect (returns firstName, lastName)
//        df.select { cols().dfs { true } }.alsoDebug() // incorrect (returns firstName, lastName)
//
//        df.select { allDfs(includeGroups = true) }.alsoDebug() // correct
//        df.select { all().allDfs(includeGroups = true) }.alsoDebug() // incorrect (returns firstName, lastName)
//        df.select { cols().allDfs(includeGroups = true) }.alsoDebug() // incorrect (returns firstName, lastName)
    }

    @Test
    fun recursively() {
        dfGroup.select { recursively() }.reorderColumnsBy { name } shouldBe recursivelyGoal
        dfGroup.select { rec(includeGroups = false) }.reorderColumnsBy { name } shouldBe recursivelyNoGroups
    }

    @Test
    fun `all recursively`() {
        dfGroup.select { all().recursively() }.reorderColumnsBy { name } shouldBe recursivelyGoal
        dfGroup.select { all().rec(includeGroups = false) }.reorderColumnsBy { name } shouldBe recursivelyNoGroups
    }

    @Test
    fun `cols recursively`() {
        dfGroup.select { cols().recursively() }.reorderColumnsBy { name } shouldBe recursivelyGoal
        dfGroup.select { cols().rec(includeGroups = false) }.reorderColumnsBy { name } shouldBe recursivelyNoGroups
    }

    @Test
    fun `colsOf recursively`() {
        dfGroup.select { colsOf<String?>().recursively() }.reorderColumnsBy { name } shouldBe recursivelyString
        dfGroup.select { colsOf<String?>().rec(includeGroups = false) }.reorderColumnsBy { name } shouldBe recursivelyString
    }

    @Test
    fun `allRecursively`() {
        dfGroup.select { allRecursively() }.reorderColumnsBy { name } shouldBe recursivelyGoal
        dfGroup.select { allRec(includeGroups = false) }.reorderColumnsBy { name } shouldBe recursivelyNoGroups
    }

    @Test
    fun `all allRecursively`() {
        dfGroup.select { all().allRecursively() }.reorderColumnsBy { name } shouldBe recursivelyGoal
        dfGroup.select { all().allRec(includeGroups = false) }.reorderColumnsBy { name } shouldBe recursivelyNoGroups
    }

    @Test
    fun `cols allRecursively`() {
        dfGroup.select { cols().allRecursively() }.reorderColumnsBy { name } shouldBe recursivelyGoal
        dfGroup.select { cols().allRec(includeGroups = false) }.reorderColumnsBy { name } shouldBe recursivelyNoGroups
    }
}
