package org.jetbrains.kotlinx.dataframe.dataTypes

import java.net.URL

public data class IMG(val src: String, val width: Int? = null, val height: Int? = null) {
    public constructor(src: URL, width: Int? = null, height: Int? = null) : this(src.toString(), width, height)

    override fun toString(): String {
        return """<img src="$src"${width?.let { " width=$it" } ?: ""}${height?.let { " height=$it" } ?: ""}/>"""
    }
}
