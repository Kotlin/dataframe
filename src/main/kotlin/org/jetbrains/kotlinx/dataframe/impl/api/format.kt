package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.api.CellAttributes
import org.jetbrains.kotlinx.dataframe.api.CellFormatter
import org.jetbrains.kotlinx.dataframe.api.FormatClause
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.FormattingDSL
import org.jetbrains.kotlinx.dataframe.api.RGBColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.depth

internal class SingleAttribute(val key: String, val value: String) : CellAttributes {
    override fun attributes() = listOf(key to value)
}

internal class MergedAttributes(private val attributes: List<CellAttributes>) : CellAttributes {
    override fun attributes() = attributes.flatMap { it.attributes() }.toMap().toList()
}

internal fun encRgb(r: Short, g: Short, b: Short): String = "#${encHex(r)}${encHex(g)}${encHex(b)}"

internal fun encHex(v: Short): String = "${(v / 16).toString(16)}${(v % 16).toString(16)}"

internal fun RGBColor.encode() = encRgb(r, g, b)

internal fun componentWise(color1: RGBColor, color2: RGBColor, f: (Short, Short) -> Short) = RGBColor(
    f(color1.r, color2.r),
    f(color1.g, color2.g),
    f(color1.b, color2.b)
)

internal fun linearGradient(
    x: Double,
    minValue: Double,
    minColor: RGBColor,
    maxValue: Double,
    maxColor: RGBColor
): RGBColor {
    if (x < minValue) return minColor
    if (x > maxValue) return maxColor
    val t = (x - minValue) / (maxValue - minValue)
    return componentWise(minColor, maxColor) { cmin, cmax ->
        (cmin + t * (cmax - cmin)).toInt().toShort()
    }
}

internal fun <T, C> FormatClause<T, C>.formatImpl(formatter: CellFormatter<C>): FormattedFrame<T> {
    val columns =
        if (columns != null) df.getColumnsWithPaths(columns).mapNotNull { if (it.depth == 0) it.name else null }
            .toSet() else null
    return FormattedFrame(df) { row, col ->
        val oldAttributes = oldFormatter?.invoke(row, col)
        if (columns == null || columns.contains(col.name())) {
            val value = row[col] as C
            if (filter == null || filter(row, value)) {
                oldAttributes and formatter(FormattingDSL, value)
            } else oldAttributes
        } else oldAttributes
    }
}
