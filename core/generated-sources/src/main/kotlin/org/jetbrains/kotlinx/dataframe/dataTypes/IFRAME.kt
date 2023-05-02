package org.jetbrains.kotlinx.dataframe.dataTypes

import java.net.URL

public data class IFRAME(
    val src: String,
    val border: Boolean = false,
    val width: Int? = null,
    val height: Int? = null
) {
    public constructor(src: URL, border: Boolean = false, width: Int? = null, height: Int? = null) : this(src.toString(), border, width, height)

    override fun toString(): String {
        return """<iframe src="$src" frameborder=${if (border) 1 else 0 }${width?.let { " width=$it" } ?: ""}${height?.let { " height=$it" } ?: ""}/>"""
    }
}
