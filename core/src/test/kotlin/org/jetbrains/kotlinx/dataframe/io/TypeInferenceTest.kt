package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlin.reflect.typeOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test

class TypeInferenceTest {

    open class A

    private class B : A()

    @Test
    fun `private subtypes`() {
        val df = dataFrameOf("col")(B(), B())
        df["col"].type() shouldBe typeOf<A>()
    }
}
