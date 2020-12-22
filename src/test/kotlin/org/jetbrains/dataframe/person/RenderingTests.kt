package org.jetbrains.dataframe.person

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.toHTML
import org.junit.Test

class RenderingTests: BaseTest() {

    @Test
    fun `render to html`() {
        val src = df.toHTML()
        println(src)
    }

    @Test
    fun `render to string`(){
        val expected = """
            Data Frame [7 x 4]

            |name:String |age:Int |city:String? |weight:Int? |
            |------------|--------|-------------|------------|
            |Alice       |15      |London       |54          |
            |Bob         |45      |Dubai        |87          |
            |Mark        |20      |Moscow       |null        |
            |Mark        |40      |Milan        |null        |
            |Bob         |30      |Tokyo        |68          |
            |Alice       |20      |null         |55          |
            |Mark        |30      |Moscow       |90          |
        """.trimIndent()

        typed.toString().trim() shouldBe expected
    }
}