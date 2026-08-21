package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test
import kotlin.reflect.typeOf

class AppendTests {

    @Test
    fun `append null to frame column replaces it with schema-aware empty dataframe`() {
        val frame = dataFrameOf("value")(1)
        val df = dataFrameOf(columnOf(frame) named "col")

        val result = df.append(null)

        result.nrow shouldBe 2
        result["col"].kind() shouldBe ColumnKind.Frame
        result["col"].type() shouldBe typeOf<DataFrame<*>>()
        result["col"].values() shouldBe listOf(frame, DataFrame.empty(frame.schema()))
    }
}
