package org.jetbrains.kotlinx.dataframe.rendering.html

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.io.toHTML
import java.awt.Desktop
import java.io.File

fun AnyFrame.browse() {
    val file = File("temp.html") // File.createTempFile("df_rendering", ".html")
    file.writeText(toHTML(includeInit = true).toString())
    val uri = file.toURI()
    val desktop = Desktop.getDesktop()
    desktop.browse(uri)
}
