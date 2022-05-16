package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.junit.Test
import kotlin.reflect.typeOf

class ConvertToTests {

    @Test
    fun `convert frame column with empty frames`() {
        val groups by columnOf(dataFrameOf("a")("1"), DataFrame.empty())
        val df = dataFrameOf(groups)

        @DataSchema
        data class GroupSchema(val a: Int)

        @DataSchema
        data class DataFrameSchema(val groups: DataFrame<GroupSchema>)

        val converted = df.convertTo<DataFrameSchema>()

        converted[groups].forEach {
            it["a"].type() shouldBe typeOf<Int>()
        }
    }
}
