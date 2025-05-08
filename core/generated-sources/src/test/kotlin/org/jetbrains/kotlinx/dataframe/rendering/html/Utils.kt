package org.jetbrains.kotlinx.dataframe.rendering.html

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml

fun AnyFrame.browse() {
    toStandaloneHtml().openInBrowser()
}
