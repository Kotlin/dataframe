package org.jetbrains.kotlinx.dataframe.rendering.html

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHTML

fun AnyFrame.browse() {
    toStandaloneHTML().openInBrowser()
}
