package org.jetbrains.kotlinx.dataframe.testSets.person2

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.by
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.merge
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rename
import org.junit.Test

class MergeTests: Base() {

    @Test
    fun `merge inplace`() {
        val merged = df.merge { name.firstName and city }.by { it[0] + " from " + it[1] }.into("name")

        merged shouldBe df.merge { name.firstName and city }.by { it[0] + " from " + it[1] }.into("name2")
            .remove { name }.rename("name2" to "name")
    }
}
