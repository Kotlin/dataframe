package org.jetbrains.dataframe.io

import kotlinx.serialization.json.JsonObject
import org.jetbrains.dataframe.AnyCol
import org.jetbrains.dataframe.ColumnsSelector
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.DataRow
import org.jetbrains.dataframe.RowCellFilter
import org.jetbrains.dataframe.getColumnsWithPaths
import org.jetbrains.kotlinx.jupyter.api.DisplayResult
import org.jetbrains.kotlinx.jupyter.api.HTML

data class RGBColor(val r: Short, val g: Short, val b: Short)

private fun encRgb(r: Short, g: Short, b: Short): String = "#${encHex(r)}${encHex(g)}${encHex(b)}"

private fun encHex(v: Short): String = "${(v / 16).toString(16)}${(v % 16).toString(16)}"

internal fun RGBColor.encode() = encRgb(r, g, b)

private fun componentWise(color1: RGBColor, color2: RGBColor, f: (Short, Short) -> Short) = RGBColor(
    f(color1.r, color2.r),
    f(color1.g, color2.g),
    f(color1.b, color2.b)
)

interface CellAttributes {
    fun attributes(): String
}

object FormatReceiver {

    internal class SingleAttribute(val attribute: String) : CellAttributes {

        constructor(attributeName: String, attributeValue: String): this("${attributeName}:${attributeValue}")

        override fun attributes() = attribute
    }

    internal class MergedAttributes(private val attributes: List<CellAttributes>): CellAttributes {
        override fun attributes() = attributes.joinToString(";") { it.attributes() }
    }

    internal fun attribute(name: String, value: String): CellAttributes = SingleAttribute(name, value)

    fun background(color: RGBColor) = attribute("background-color", color.encode())
    fun background(r: Short, g: Short, b: Short) = background(RGBColor(r, g, b))

    fun textColor(color: RGBColor) = attribute("color", color.encode())
    fun textColor(r: Short, g: Short, b: Short) = textColor(RGBColor(r, g, b))

    fun rgb(r: Short, g: Short, b: Short) = RGBColor(r, g, b)

    val italic = attribute("font-style", "italic")

    val bold = attribute("font-weight", "bold")

    val white = rgb(0,0,0)
    val green = rgb(0,255,0)
    val red = rgb(255,0,0)
    val blue = rgb(0,0,255)

    val none: CellAttributes = SingleAttribute("")

    val underline = attribute("text-decoration", "underline")

    infix fun CellAttributes.and(other: CellAttributes): CellAttributes = MergedAttributes(listOf(this, other))

    fun linear(value: Number, from: Pair<Number, RGBColor>, to: Pair<Number, RGBColor>): RGBColor {
        val a = from.first.toDouble()
        val b = to.first.toDouble()
        if(a < b) return linearGradient(value.toDouble(), a, from.second, b, to.second)
        return linearGradient(value.toDouble(), b, to.second, a, from.second)
    }
}

internal fun linearGradient(x: Double, minValue: Double, minColor: RGBColor, maxValue: Double, maxColor: RGBColor): RGBColor {
    if(x < minValue) return minColor
    if(x > maxValue) return maxColor
    val t = (x - minValue) / (maxValue - minValue)
    return componentWise(minColor, maxColor) { cmin, cmax ->
        (cmin + t * (cmax - cmin)).toInt().toShort()
    }
}

typealias RowColFormatter<T> = (DataRow<T>, AnyCol) -> CellAttributes?

class FrameRender<T>(
    private val df: DataFrame<T>,
    private val formatter: RowColFormatter<T>? = null,
): DisplayResult {

    override fun toJson(additionalMetadata: JsonObject) = HTML(df.toHTML(formatter = formatter)).toJson()
}

data class ColorClause<T, C>(
    val df: DataFrame<T>,
    val selector: ColumnsSelector<T, C>? = null,
    val filter: RowCellFilter<T, C> = { true },
)
fun <T, C> DataFrame<T>.format(selector: ColumnsSelector<T, C>) = ColorClause(this, selector)

fun <T> DataFrame<T>.format() = ColorClause<T, Any?>(this)

fun <T, C> ColorClause<T, C>.where(filter: RowCellFilter<T, C>) = copy(filter = filter)

typealias CellFormatter<V> = FormatReceiver.(V) -> CellAttributes?

fun <T, C: Number?> ColorClause<T, C>.linearBg(from: Pair<Number, RGBColor>, to: Pair<Number, RGBColor>) = with {
    if (it != null)
        background(linear(it, from, to))
    else null
}

fun <T, C> ColorClause<T, C>.with(formatter: CellFormatter<C>): FrameRender<T> {
    val columns = if(selector != null) df.getColumnsWithPaths(selector).mapNotNull { if(it.depth == 0) it.name else null }.toSet() else null
    return FrameRender(df) { row, col ->
        if (columns == null || columns.contains(col.name())) {
            val value = row[col] as C
            if (filter == null || filter(row, value))
                formatter(FormatReceiver, value)
            else null
        } else null
    }
}
