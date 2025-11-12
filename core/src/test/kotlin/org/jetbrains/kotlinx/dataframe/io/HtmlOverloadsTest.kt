package org.jetbrains.kotlinx.dataframe.io

import org.junit.Test
import org.junit.Assert.assertEquals
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

class HtmlOverloadsTest {

    @Test
    fun writeHtml_overloads_String_Path_File_produce_same_content() {
        val html = DataFrameHtmlData(style = "body{color:black;}", body = "<div>hello</div>", script = "console.log('x')")
        val expected = html.toString()

        val tmpDir: Path = createTempDirectory("html_overloads_")
        try {
            val pathOut = tmpDir.resolve("out_path.html")
            val fileOut = tmpDir.resolve("out_file.html").toFile()
            val strOut = tmpDir.resolve("out_str.html").toString()

            html.writeHtml(pathOut)
            html.writeHtml(fileOut)
            html.writeHtml(strOut)

            fun readText(p: Path): String = String(Files.readAllBytes(p), Charsets.UTF_8)
            assertEquals(expected, readText(pathOut))
            assertEquals(expected, readText(fileOut.toPath()))
            assertEquals(expected, readText(Paths.get(strOut)))
        } finally {
            Files.walk(tmpDir).sorted(Comparator.reverseOrder()).forEach { it.toFile().delete() }
        }
    }
}
