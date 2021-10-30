package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.impl.api.MergedAttributes
import org.jetbrains.kotlinx.dataframe.impl.api.SingleAttribute
import org.jetbrains.kotlinx.dataframe.impl.api.encode
import org.jetbrains.kotlinx.dataframe.impl.api.formatImpl
import org.jetbrains.kotlinx.dataframe.impl.api.linearGradient
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.HtmlData
import org.jetbrains.kotlinx.dataframe.io.toHTML

public data class RGBColor(val r: Short, val g: Short, val b: Short)

public interface CellAttributes {

    public fun attributes(): List<Pair<String, String>>
}

public infix fun CellAttributes?.and(other: CellAttributes?): CellAttributes? = when {
    other == null -> this
    this == null -> other
    else -> MergedAttributes(listOf(this, other))
}

public object FormattingDSL {

    public fun rgb(r: Short, g: Short, b: Short): RGBColor = RGBColor(r, g, b)

    public val black: RGBColor = rgb(0, 0, 0)
    public val white: RGBColor = rgb(255, 255, 255)
    public val green: RGBColor = rgb(0, 255, 0)
    public val red: RGBColor = rgb(255, 0, 0)
    public val blue: RGBColor = rgb(0, 0, 255)
    public val gray: RGBColor = rgb(128, 128, 128)
    public val darkGray: RGBColor = rgb(169, 169, 169)
    public val lightGray: RGBColor = rgb(211, 211, 211)

    public fun attr(name: String, value: String): CellAttributes = SingleAttribute(name, value)

    public fun background(color: RGBColor): CellAttributes = attr("background-color", color.encode())
    public fun background(r: Short, g: Short, b: Short): CellAttributes = background(RGBColor(r, g, b))

    public fun textColor(color: RGBColor): CellAttributes = attr("color", color.encode())
    public fun textColor(r: Short, g: Short, b: Short): CellAttributes = textColor(RGBColor(r, g, b))

    public val italic: CellAttributes = attr("font-style", "italic")

    public val bold: CellAttributes = attr("font-weight", "bold")

    public val underline: CellAttributes = attr("text-decoration", "underline")

    public fun linearBg(value: Number, from: Pair<Number, RGBColor>, to: Pair<Number, RGBColor>): CellAttributes = background(
        linear(value, from, to)
    )

    public fun linear(value: Number, from: Pair<Number, RGBColor>, to: Pair<Number, RGBColor>): RGBColor {
        val a = from.first.toDouble()
        val b = to.first.toDouble()
        if (a < b) return linearGradient(value.toDouble(), a, from.second, b, to.second)
        return linearGradient(value.toDouble(), b, to.second, a, from.second)
    }
}

public typealias RowColFormatter<T> = (DataRow<T>, AnyCol) -> CellAttributes?

public class FormattedFrame<T>(
    internal val df: DataFrame<T>,
    internal val formatter: RowColFormatter<T>? = null,
) {
    public fun toHTML(configuration: DisplayConfiguration): HtmlData = df.toHTML(getDisplayConfiguration(configuration))

    public fun getDisplayConfiguration(configuration: DisplayConfiguration): DisplayConfiguration {
        return configuration.copy(cellFormatter = formatter as RowColFormatter<*>?)
    }
}

public data class ColorClause<T, C>(
    val df: DataFrame<T>,
    val columns: ColumnsSelector<T, C>? = null,
    val oldFormatter: RowColFormatter<T>? = null,
    val filter: RowValueFilter<T, C> = { true },
)

public fun <T, C> FormattedFrame<T>.format(columns: ColumnsSelector<T, C>): ColorClause<T, C> = ColorClause(df, columns, formatter)
public fun <T, C> DataFrame<T>.format(columns: ColumnsSelector<T, C>): ColorClause<T, C> = ColorClause(this, columns)

public fun <T> FormattedFrame<T>.format(): ColorClause<T, Any?> = ColorClause(df, null, formatter)
public fun <T> DataFrame<T>.format(): ColorClause<T, Any?> = ColorClause(this)

public fun <T, C> ColorClause<T, C>.where(filter: RowValueFilter<T, C>): ColorClause<T, C> = copy(filter = filter)

public typealias CellFormatter<V> = FormattingDSL.(V) -> CellAttributes?

public fun <T, C : Number?> ColorClause<T, C>.linearBg(from: Pair<Number, RGBColor>, to: Pair<Number, RGBColor>): FormattedFrame<T> = with {
    if (it != null) {
        background(linear(it, from, to))
    } else null
}

public fun <T, C> ColorClause<T, C>.with(formatter: CellFormatter<C>): FormattedFrame<T> = formatImpl(formatter)
