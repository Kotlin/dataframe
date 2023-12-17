package org.jetbrains.kotlinx.dataframe.testSets.person2

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.byDesc
import org.jetbrains.kotlinx.dataframe.api.reorder
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.with
import org.junit.Test

class ReplaceTests : Base() {

    @Test
    fun `reorder columns in group`() {
        val reordered = df.replace { name }.with {
            it.asColumnGroup().select { lastName and firstName }.asColumnGroup(it.name())
        }
        reordered shouldBe df.reorder { name.firstName and name.lastName }.byDesc { it.name() }
    }
}
