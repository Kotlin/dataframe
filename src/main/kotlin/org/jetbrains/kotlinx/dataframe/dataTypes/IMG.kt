package org.jetbrains.kotlinx.dataframe.dataTypes

import java.net.URL

public data class IMG(
    val src: String,
    val width: Int? = null,
    val height: Int? = null,
    val maxWidth: Int? = null,
    val maxHeight: Int? = null
) {
    public constructor(src: URL, width: Int? = null, height: Int? = null, maxWidth: Int? = null, maxHeight: Int? = null) : this(src.toString(), width, height, maxWidth, maxHeight)

    override fun toString(): String {
        val style = StringBuilder()
        if (width != null) style.append("width:${width}px;")
        if (height != null) style.append("height:${height}px;")
        if (maxWidth != null) style.append("max-width:${maxWidth}px;")
        if (maxHeight != null) style.append("max-height:${maxHeight}px;")
        return """<img src="$src" style="$style"}/>"""
    }
}
