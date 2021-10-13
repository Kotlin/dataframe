package org.jetbrains.kotlinx.dataframe.person

import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.dataframe.group
import org.jetbrains.dataframe.into
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.io.html
import org.jetbrains.kotlinx.dataframe.io.toHTML
import org.jetbrains.kotlinx.jupyter.findNthSubstring
import org.junit.Ignore
import org.junit.Test
import java.awt.Desktop
import java.io.File

class HtmlRenderingTests : BaseTest() {

    fun AnyFrame.browse() {
        val file = File("temp.html") // File.createTempFile("df_rendering", ".html")
        file.writeText(toHTML(includeInit = true).toString())
        val uri = file.toURI()
        val desktop = Desktop.getDesktop()
        desktop.browse(uri)
    }

    @Ignore
    @Test
    fun test() {
        typed.group { name and age }.into("temp").browse()
    }

    @Test
    fun `render url`() {
        val address = "http://www.google.com"
        val df = dataFrameOf("url")(address).parse()
        val html = df.html()
        html shouldContain "href"
        html.findNthSubstring(address, 2) shouldNotBe -1
    }
}
