package org.jetbrains.dataframe.person

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.index
import org.jetbrains.dataframe.io.FormatReceiver.green
import org.jetbrains.dataframe.io.FormatReceiver.red
import org.jetbrains.dataframe.io.format
import org.jetbrains.dataframe.io.linearBg
import org.jetbrains.dataframe.io.with
import org.jetbrains.dataframe.io.toHTML
import org.jetbrains.dataframe.io.where
import org.jetbrains.kotlinx.jupyter.api.toJson
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

    @Test
    fun `conditional formatting`(){
        typed.format { intCols().notNullable() }.where { index % 2 == 0 }.with {
            if(it > 10) background(white) and bold and italic
            else textColor(linear(it, 30.5 to red, 50 to green)) and underline
        }

        typed.format { age }.linearBg(20 to green, 80 to red).toJson()
    }
}