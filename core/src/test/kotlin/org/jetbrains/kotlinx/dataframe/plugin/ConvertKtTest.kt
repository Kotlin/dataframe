package org.jetbrains.kotlinx.dataframe.plugin

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlinx.dataframe.annotations.ConvertApproximation
import org.jetbrains.kotlinx.dataframe.annotations.TypeApproximationImpl
import org.junit.Test

class ConvertKtTest {
    @Test
    fun test() {
        val convert = ConvertApproximation(
            PluginDataFrameSchema(
                listOf(
                    SimpleCol("city", TypeApproximationImpl("kotlin.String", true)),
                    SimpleColumnGroup(
                        "person",
                        listOf(
                            SimpleCol("age", TypeApproximationImpl("kotlin.Number", true)),
                            SimpleCol("name", TypeApproximationImpl("kotlin.String", true)),
                            SimpleCol("weight", TypeApproximationImpl("kotlin.Int", true))
                        )
                    )
                )
            ),
            columns = listOf(listOf("person", "age"))
        )
        val type = TypeApproximationImpl("kotlin.Number", false)
        convertImpl(convert, type).columns()[1].shouldBeInstanceOf<SimpleColumnGroup>().columns()[0].type shouldBe TypeApproximationImpl("kotlin.Number", false)
    }
}
