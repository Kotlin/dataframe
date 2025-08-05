package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.api.CellAttributes
import org.jetbrains.kotlinx.dataframe.api.FormatClause
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.RowColFormatter
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
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

internal fun RgbColor.encode() = encRgb(r, g, b)

internal fun componentWise(color1: RgbColor, color2: RgbColor, f: (Short, Short) -> Short) =
    RgbColor(
        f(color1.r, color2.r),
        f(color1.g, color2.g),
        f(color1.b, color2.b),
    )

internal fun linearGradient(
    x: Double,
    minValue: Double,
    minColor: RgbColor,
    maxValue: Double,
    maxColor: RgbColor,
): RgbColor {
    if (x < minValue) return minColor
    if (x > maxValue) return maxColor
    val t = (x - minValue) / (maxValue - minValue)
    return componentWise(minColor, maxColor) { cMin, cMax ->
        (cMin + t * (cMax - cMin)).toInt().toShort()
    }
}

@Suppress("UNCHECKED_CAST")
internal inline fun <T, C> FormatClause<T, C>.formatImpl(
    crossinline formatter: RowColFormatter<T, C>,
): FormattedFrame<T> {
    val clause = this
    val columns =
        if (clause.columns != null) {
            clause.df.getColumnsWithPaths(clause.columns)
                .mapNotNull { if (it.depth == 0) it.name else null } // TODO Causes #1356
                .toSet()
        } else {
            null
        }
    return FormattedFrame(clause.df) { row, col ->
        val oldAttributes = clause.oldFormatter?.invoke(FormattingDsl, row, col.cast())
        if (columns == null || columns.contains(col.name())) {
            val value = row[col.name] as C
            if (clause.filter(row, value)) {
                oldAttributes and formatter(FormattingDsl, row.cast(), col.cast())
            } else {
                oldAttributes
            }
        } else {
            oldAttributes
        }
    }
}
