package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.MergedAttributes
import org.jetbrains.kotlinx.dataframe.impl.api.SingleAttribute
import org.jetbrains.kotlinx.dataframe.impl.api.encode
import org.jetbrains.kotlinx.dataframe.impl.api.formatImpl
import org.jetbrains.kotlinx.dataframe.impl.api.linearGradient
import org.jetbrains.kotlinx.dataframe.io.DataFrameHtmlData
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.io.toHtml
import org.jetbrains.kotlinx.dataframe.io.toStandaloneHtml
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Formats the specified [columns\] or cells within the [DataFrame] such that
 * they have specific attributes applied to them when rendering the dataframe to HTML.
 *
 * This function does not immediately produce a [FormattedFrame], but instead it selects the columns to be formatted
 * and returns a [FormatClause] which serves as an intermediate step.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][FormatSelectingColumns].
 *
 * The [FormatClause] allows to further narrow down the selection to individual cells
 * by selecting only certain rows, using [where][FormatClause.where],
 * and then finally specify how to format the cells using
 * [with][FormatClause.with] or [perRowCol][FormatClause.perRowCol].
 *
 * Check out the [Grammar].
 *
 * For more information: {@include [DocumentationUrls.Format]}
 */
internal interface FormatDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetFormatOperationArg]}
     */
    interface FormatSelectingColumns

    /**
     * ## Format Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * TODO
     */
    interface Grammar
}

/** {@set [SelectingColumns.OPERATION] [format][format]} */
@ExcludeFromSources
private interface SetFormatOperationArg

/**
 * @include [FormatDocs]
 * ### This Format Overload
 */
@ExcludeFromSources
private interface CommonFormatDocs

// region format

/**
 * @include [CommonFormatDocs]
 * @include [SelectingColumns.Dsl] {@include [SetFormatOperationArg]}
 * ### Examples:
 * TODO example
 *
 * @param [columns\] The [columns-selector][ColumnsSelector] used to select the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T, C> DataFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> = FormatClause(this, columns)

/**
 * @include [CommonFormatDocs]
 * @include [SelectingColumns.ColumnNames] {@include [SetFormatOperationArg]}
 * ### Examples:
 * TODO example
 *
 * @param [columns\] The names of the columns to be formatted.
 *   If unspecified, all columns will be formatted.
 */
public fun <T> DataFrame<T>.format(vararg columns: String): FormatClause<T, Any?> = format { columns.toColumnSet() }

/**
 * @include [CommonFormatDocs]
 *
 * This simply formats all columns. Optionally, you can specify which columns to format using a
 * [columns-selector][ColumnsSelector] or by [column names][String].
 *
 * ### Examples:
 * TODO example
 */
public fun <T> DataFrame<T>.format(): FormatClause<T, Any?> = FormatClause(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.format(vararg columns: ColumnReference<C>): FormatClause<T, C> =
    format { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.format(vararg columns: KProperty<C>): FormatClause<T, C> =
    format { columns.toColumnSet() }

// endregion

public fun <T, C> FormatClause<T, C>.perRowCol(formatter: RowColFormatter<T, C>): FormattedFrame<T> =
    formatImpl(formatter)

@Suppress("UNCHECKED_CAST")
public fun <T, C> FormatClause<T, C>.with(formatter: CellFormatter<C>): FormattedFrame<T> =
    formatImpl { row, col -> formatter(row[col.name] as C) }

public fun <T, C> FormatClause<T, C>.where(filter: RowValueFilter<T, C>): FormatClause<T, C> =
    FormatClause(filter = filter, df = df, columns = columns, oldFormatter = oldFormatter)

public fun <T> FormattedFrame<T>.format(): FormatClause<T, Any?> = FormatClause(df, null, formatter)

// endregion

public data class RGBColor(val r: Short, val g: Short, val b: Short)

public interface CellAttributes {

    public fun attributes(): List<Pair<String, String>>
}

public infix fun CellAttributes?.and(other: CellAttributes?): CellAttributes? =
    when {
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

    public fun linearBg(value: Number, from: Pair<Number, RGBColor>, to: Pair<Number, RGBColor>): CellAttributes =
        background(
            linear(value, from, to),
        )

    public fun linear(value: Number, from: Pair<Number, RGBColor>, to: Pair<Number, RGBColor>): RGBColor {
        val a = from.first.toDouble()
        val b = to.first.toDouble()
        if (a < b) return linearGradient(value.toDouble(), a, from.second, b, to.second)
        return linearGradient(value.toDouble(), b, to.second, a, from.second)
    }
}

public typealias RowColFormatter<T, C> = FormattingDSL.(DataRow<T>, DataColumn<C>) -> CellAttributes?

/**
 * A wrapper around a [DataFrame][df] with HTML formatting data.
 */
public class FormattedFrame<T>(internal val df: DataFrame<T>, internal val formatter: RowColFormatter<T, *>? = null) {
    /**
     * todo copy from toHtml
     * @return DataFrameHtmlData without additional definitions. Can be rendered in Jupyter kernel environments
     */
    public fun toHtml(configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT): DataFrameHtmlData =
        df.toHtml(getDisplayConfiguration(configuration))

    /**
     * todo copy from toStandaloneHtml
     * @return DataFrameHtmlData with table script and css definitions. Can be saved as an *.html file and displayed in the browser
     */
    public fun toStandaloneHtml(configuration: DisplayConfiguration = DisplayConfiguration.DEFAULT): DataFrameHtmlData =
        df.toStandaloneHtml(getDisplayConfiguration(configuration))

    public fun getDisplayConfiguration(configuration: DisplayConfiguration): DisplayConfiguration =
        configuration.copy(cellFormatter = formatter as RowColFormatter<*, *>?)
}

/**
 * An intermediate class used in the [format] operation.
 *
 * This class itself does nothing—it is just a transitional step before specifying
 * how to format the selected columns.
 * It must be followed by one of the positioning methods
 * to produce a new [FormattedFrame]; a [DataFrame] with HTML formatting data.
 *
 * Use the following function to filter the rows to format:
 * - [where][FormatClause.where] – filters the rows to format using a [RowValueFilter].
 *
 * Use the following functions to finalize the formatting:
 * - [with][FormatClause.with] – Specifies how to format the cells using a [CellFormatter].
 * - [perRowCol][FormatClause.perRowCol] – Specifies how to format each cell individually using a [RowColFormatter].
 *
 * See [Grammar][TODO] for more details.
 */
public class FormatClause<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>? = null,
    internal val oldFormatter: RowColFormatter<T, C>? = null,
    internal val filter: RowValueFilter<T, C> = { true },
) {
    override fun toString(): String =
        "FormatClause(df=$df, columns=$columns, oldFormatter=$oldFormatter, filter=$filter)"
}

public fun <T, C> FormattedFrame<T>.format(columns: ColumnsSelector<T, C>): FormatClause<T, C> =
    FormatClause(df, columns, formatter)

public typealias CellFormatter<V> = FormattingDSL.(V) -> CellAttributes?

public fun <T, C : Number?> FormatClause<T, C>.linearBg(
    from: Pair<Number, RGBColor>,
    to: Pair<Number, RGBColor>,
): FormattedFrame<T> =
    with {
        if (it != null) {
            background(linear(it, from, to))
        } else {
            null
        }
    }
