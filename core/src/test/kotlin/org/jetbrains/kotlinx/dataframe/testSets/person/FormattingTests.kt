package org.jetbrains.kotlinx.dataframe.testSets.person

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.gray
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.green
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.red
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.colsOf
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.linearBg
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.impl.api.encode
import org.jetbrains.kotlinx.dataframe.impl.api.linearGradient
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

class FormattingTests : BaseTest() {

    @Test
    fun `conditional formatting`() {
        val formattedFrame = typed.format { colsOf<Int>() }.with {
            if (it > 10) {
                background(white) and bold and italic
            } else {
                textColor(linear(it, 30.5 to red, 50 to green)) and underline
            }
        }

        val formatter = formattedFrame.formatter!!
        for (row in 0 until typed.nrow) {
            FormattingDsl.formatter(typed[row], typed.age.addPath())!!.attributes().size shouldBe
                if (typed[row].age > 10) 3 else 2
        }

        formattedFrame.toHtml(DisplayConfiguration.DEFAULT).toString() shouldContain "font-style:italic"
    }

    @Test
    fun `override format`() {
        val formatter = typed
            .format { age }.linearBg(20 to green, 80 to red)
            .format { age and weight }.where { index % 2 == 0 }.with { background(gray) }
            .formatter!!

        for (row in 0 until typed.nrow step 2) {
            FormattingDsl.formatter(typed[row], typed.age.addPath())!!.attributes() shouldBe
                listOf("background-color" to gray.encode())
        }

        for (row in 1 until typed.nrow step 2) {
            FormattingDsl.formatter(typed[row], typed.age.addPath())!!.attributes() shouldBe
                listOf("background-color" to linearGradient(typed[row].age.toDouble(), 20.0, green, 80.0, red).encode())
        }
    }
}
