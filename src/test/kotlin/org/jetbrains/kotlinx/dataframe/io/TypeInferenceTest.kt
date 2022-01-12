package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.schema
import org.junit.Test
import kotlin.reflect.typeOf

class TypeInferenceTest {

    open class A

    private class B : A()

    @Test
    fun `private subtypes`() {
        val df = dataFrameOf("col")(B(), B())
        df["col"].type() shouldBe typeOf<A>()
    }

    @Test
    fun `json property named 'value'`() {
        val df = DataFrame.readJsonStr(
            """
                {
                  "key": "icon",
                  "value": "str"
                }
            """.trimIndent()
        )
        df.schema().columns["value"]?.type shouldBe typeOf<String>()
    }
}
