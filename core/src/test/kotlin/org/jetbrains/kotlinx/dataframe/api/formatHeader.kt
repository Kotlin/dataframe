package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.blue
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class FormatHeaderTests : TestBase() {

    @Test
    fun `formatHeader on single column adds inline style to header`() {
        val formatted = df.formatHeader { age }.with { attr("border", "3px solid green") }
        val html = formatted.toHtml().toString()

        // header style is rendered inline inside <span ... style="...">
        // count exact style occurrences to avoid interference with CSS
        val occurrences = html.split("border:3px solid green").size - 1
        occurrences shouldBe 1
    }

    @Test
    fun `formatHeader by names overload`() {
        val formatted = df.formatHeader("age").with { attr("text-align", "center") }
        val html = formatted.toHtml().toString()
        val occurrences = html.split("text-align:center").size - 1
        occurrences shouldBe 1
    }

    @Test
    fun `header style inherited from group to children`() {
        // Apply style to the group header only
        val formatted = df.formatHeader { name }.with { attr("border", "1px solid red") }
        val html = formatted.toHtml().toString()

        // We expect the style on the group header itself and each direct child header
        // In the default TestBase dataset, name group has two children
        val occurrences = html.split("border:1px solid red").size - 1
        occurrences shouldBe 3
    }

    @Test
    fun `child header overrides parent group header style`() {
        val formatted = df
            .formatHeader { name }.with { attr("border", "1px solid red") }
            .formatHeader { name.firstName }.with { attr("border", "2px dashed green") }
        val html = formatted.toHtml().toString()

        // Parent style applies to group and lastName, but firstName gets its own style in addition to or replacing
        // We check for both occurrences
        val parentOcc = html.split("border:1px solid red").size - 1
        val childOcc = html.split("border:2px dashed green").size - 1

        parentOcc shouldBe 2 // group + lastName
        childOcc shouldBe 1 // firstName only
    }

    @Test
    fun `format and formatHeader can be chained and both persist`() {
        val formatted = df
            .format { age }.with { background(blue) }
            .formatHeader { age }.with { attr("border", "3px solid green") }

        val html = formatted.toHtml().toString()

        // body cell style
        (html.split("background-color:#0000ff").size - 1) shouldBe 7
        // header style
        (html.split("border:3px solid green").size - 1) shouldBe 1

        formatted::class.simpleName shouldNotBe null
    }
}
