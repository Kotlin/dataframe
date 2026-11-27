package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.addPrefix
import org.jetbrains.kotlinx.dataframe.api.addSuffix
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.rename
import org.junit.Test

class RenameAffixes {
    @Test
    fun renameAddPrefix() {
        val df = dataFrameOf("name", "age")("Alice", 20)
        // SampleStart
        val result = df.rename("name", "age").addPrefix("person_")
        // SampleEnd
        result.columnNames() shouldBe listOf("person_name", "person_age")
    }

    @Test
    fun renameAddSuffix() {
        val df = dataFrameOf("name", "age")("Alice", 20)
        // SampleStart
        val result = df.rename("name").addSuffix("_original")
        // SampleEnd
        result.columnNames() shouldBe listOf("name_original", "age")
    }
}
