package org.jetbrains.kotlinx.dataframe.testSets.person

import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.junit.Ignore
import org.junit.Test
import java.awt.Desktop
import java.io.File

class HtmlRenderingTests : BaseTest() {

    fun AnyFrame.browse() {
        val file = File("temp.html") // File.createTempFile("df_rendering", ".html")
        file.writeText(toStandaloneHtml().toString())
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
        val html = df.toStandaloneHtml().toString()
        html shouldContain "href"
        html.findNthSubstring(address, 2) shouldNotBe -1
    }
}

internal fun String.findNthSubstring(s: String, n: Int, start: Int = 0): Int {
    if (n < 1 || start == -1) return -1

    var i = start

    for (k in 1..n) {
        i = indexOf(s, i)
        if (i == -1) return -1
        i += s.length
    }

    return i - s.length
}
