package org.jetbrains.dataframe.io

import kotlinx.serialization.json.JsonObject
import org.jetbrains.dataframe.AnyCol
import org.jetbrains.dataframe.AnyFrame
import org.jetbrains.dataframe.AnyRow
import org.jetbrains.dataframe.ColumnsSelector
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.RowFilter
import org.jetbrains.dataframe.filter
import org.jetbrains.dataframe.select
import org.jetbrains.kotlinx.jupyter.api.DisplayResult
import org.jetbrains.kotlinx.jupyter.api.HTML

data class RGBColor(
    val r: Short,
    val g: Short,
    val b: Short,
)
private fun encRgb(r: Short, g: Short, b: Short): String =
    "#${encHex(r)}${encHex(g)}${encHex(b)}"
private fun encHex(v: Short): String =
    "${(v / 16).toString(16)}${(v % 16).toString(16)}"
fun RGBColor.encode() = encRgb(r, g, b)
private fun componentWise(color1: RGBColor, color2: RGBColor, f: (Short, Short) -> Short) = RGBColor(
    f(color1.r, color2.r),
    f(color1.g, color2.g),
    f(color1.b, color2.b)
)

data class CellCustomizationData(
    val textColor: RGBColor? = null,
    val bgColor: RGBColor? = null,
) {
    fun toAttributesString(): String {
        return listOfNotNull(
            textColor?.let { "color" to it.encode() },
            bgColor?.let { "background-color" to it.encode() }
        ).joinToString(";") { "${it.first}:${it.second}" }
    }
}

enum class CellCustomizationKind(val picker: (RGBColor) -> CellCustomizationData) {
    TEXT_COLOR ({ CellCustomizationData(textColor = it)}),
    BG_COLOR({ CellCustomizationData(bgColor = it)});
}

fun CellCustomizationData?.merge(other: CellCustomizationData?): CellCustomizationData? {
    if (this == null) return other
    if (other == null) return this
    return CellCustomizationData(
        other.textColor ?: textColor,
        other.bgColor ?: bgColor
    )
}

typealias CellCustomization = (AnyRow, AnyCol) -> CellCustomizationData?

class LinearGradientCustomizer(
    private val minValue: Double,
    private val maxValue: Double,
    private val minColor: RGBColor,
    private val maxColor: RGBColor,
    private val picker: (RGBColor) -> CellCustomizationData
): (Number) -> CellCustomizationData? {
    override fun invoke(value: Number): CellCustomizationData {
        val x = value.toDouble()
        require(x in minValue..maxValue) { "Rendered value $x is not within its bounds [$minValue, $maxValue]" }
        val t = (x - minValue) / (maxValue - minValue)
        val color = componentWise(minColor, maxColor) { cmin, cmax ->
            (cmin + t * (cmax - cmin)).toInt().toShort()
        }
        return picker(color)
    }
}

class FrameRender(
    private val frame: AnyFrame,
    private val customization: CellCustomization? = null,
): DisplayResult {
    constructor(render: FrameRender, customization: CellCustomization? = null): this(render.frame, { row, col ->
        val oldC = render.customization
        val newC = customization
        val data1 = oldC?.invoke(row, col)
        val data2 = newC?.invoke(row, col)
        data1.merge(data2)
    })

    override fun toJson(additionalMetadata: JsonObject) = HTML(frame.toHTML(cellCustomization = customization)).toJson()
}

fun AnyFrame.customizeView(customization: CellCustomization) = FrameRender(this, customization)
inline fun <reified V> AnyFrame.customizeViewByValue(crossinline customization: (V) -> CellCustomizationData?) = FrameRender(this).customizeViewByValue(customization)

fun FrameRender.customizeView(customization: CellCustomization) = FrameRender(this, customization)
inline fun <reified V> FrameRender.customizeViewByValue(crossinline customization: (V) -> CellCustomizationData?) = FrameRender(this) { row, col ->
    val value = row[col]
    if (value is V) customization(value)
    else null
}


data class ColorClause<T, C>(
    val kind: CellCustomizationKind,
    val df: DataFrame<T>,
    val selector: ColumnsSelector<T, C>? = null,
    val filter: RowFilter<T> = { true },
) {
    val filteredDf: DataFrame<T>
        get() = (selector?.let { df.select(it) } ?: df).filter(filter)
}
fun <T, C> DataFrame<T>.bgcolor(selector: ColumnsSelector<T, C>) = ColorClause(CellCustomizationKind.BG_COLOR, this, selector)
fun <T, C> DataFrame<T>.textcolor(selector: ColumnsSelector<T, C>) = ColorClause(CellCustomizationKind.TEXT_COLOR, this, selector)

fun <T> DataFrame<T>.bgcolor() = ColorClause<T, Any?>(CellCustomizationKind.BG_COLOR, this)
fun <T> DataFrame<T>.textcolor() = ColorClause<T, Any?>(CellCustomizationKind.TEXT_COLOR, this)

fun <T, C> ColorClause<T, C>.where(filter: RowFilter<T>) = copy(filter = filter)

fun <T, C> ColorClause<T, C>.linear(min: Pair<Double, RGBColor>, max: Pair<Double, RGBColor>): FrameRender {
    return filteredDf.customizeViewByValue(
        LinearGradientCustomizer(min.first, max.first, min.second, max.second) {
            kind.picker(it)
        }
    )
}
inline fun <T, C, reified V> ColorClause<T, C>.into(crossinline colorer: (V) -> RGBColor) = filteredDf.customizeViewByValue { value: V ->
    kind.picker(colorer(value))
}
