package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.formatHeaderImpl

// region docs

/**
 * A lambda used to format a column header (its displayed name) when rendering a dataframe to HTML.
 *
 * The lambda runs in the context of [FormattingDsl] and receives the [ColumnWithPath] of the header to format.
 * Return a [CellAttributes] (or `null`) describing the CSS you want to apply to the header cell.
 *
 * Examples:
 * - Center the header: `attr("text-align", "center")`
 * - Make it bold: `bold`
 * - Set a custom color: `textColor(rgb(10, 10, 10))`
 */
public typealias HeaderColFormatter<C> = FormattingDsl.(col: ColumnWithPath<C>) -> CellAttributes?

/**
 * An intermediate class used in the header-format operation [formatHeader].
 *
 * This class itself does nothing—it represents a selection of columns whose headers will be formatted.
 * Finalize this step by calling [with] to produce a new [FormattedFrame].
 *
 * Header formatting is additive and supports nested column groups: styles specified for a parent group
 * are inherited by its child columns unless overridden for the child.
 */
public class HeaderFormatClause<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C> = { all().cast() },
    internal val oldHeaderFormatter: HeaderColFormatter<C>? = null,
    internal val oldCellFormatter: RowColFormatter<T, *>? = null,
) {
    override fun toString(): String =
        "HeaderFormatClause(df=$df, columns=$columns, oldHeaderFormatter=$oldHeaderFormatter, oldCellFormatter=$oldCellFormatter)"
}

// endregion

// region DataFrame.formatHeader

/**
 * **Experimental API. It may be changed in the future.**
 *
 * Selects [columns] whose headers should be formatted. If unspecified, all columns will be formatted.
 *
 * This does not immediately produce a [FormattedFrame]; instead it returns a [HeaderFormatClause]
 * which must be finalized using [HeaderFormatClause.with].
 */
public fun <T, C> DataFrame<T>.formatHeader(columns: ColumnsSelector<T, C>): HeaderFormatClause<T, C> =
    HeaderFormatClause(this, columns)

/**
 * **Experimental API. It may be changed in the future.**
 *
 * Selects [columns] whose headers should be formatted.
 *
 * This does not immediately produce a [FormattedFrame]; instead it returns a [HeaderFormatClause]
 * which must be finalized using [HeaderFormatClause.with].
 */
public fun <T> DataFrame<T>.formatHeader(vararg columns: String): HeaderFormatClause<T, Any?> =
    formatHeader { columns.toColumnSet() }

/**
 * **Experimental API. It may be changed in the future.**
 *
 * Selects all columns for header formatting.
 *
 * This does not immediately produce a [FormattedFrame]; instead it returns a [HeaderFormatClause]
 * which must be finalized using [HeaderFormatClause.with].
 */
public fun <T> DataFrame<T>.formatHeader(): HeaderFormatClause<T, Any?> = HeaderFormatClause(this)

// endregion

// region FormattedFrame.formatHeader

/**
 * **Experimental API. It may be changed in the future.**
 *
 * Selects [columns] whose headers should be formatted.
 *
 * This does not immediately produce a [FormattedFrame]; instead it returns a [HeaderFormatClause]
 * which must be finalized using [HeaderFormatClause.with].
 */
public fun <T, C> FormattedFrame<T>.formatHeader(columns: ColumnsSelector<T, C>): HeaderFormatClause<T, C> =
    HeaderFormatClause(
        df = df,
        columns = columns,
        oldHeaderFormatter = headerFormatter as HeaderColFormatter<C>?,
        oldCellFormatter = formatter,
    )

/**
 * **Experimental API. It may be changed in the future.**
 *
 * Selects [columns] whose headers should be formatted.
 *
 * This does not immediately produce a [FormattedFrame]; instead it returns a [HeaderFormatClause]
 * which must be finalized using [HeaderFormatClause.with].
 */
public fun <T> FormattedFrame<T>.formatHeader(vararg columns: String): HeaderFormatClause<T, Any?> =
    formatHeader { columns.toColumnSet() }

/**
 * **Experimental API. It may be changed in the future.**
 *
 * Selects all columns for header formatting.
 *
 * This does not immediately produce a [FormattedFrame]; instead it returns a [HeaderFormatClause]
 * which must be finalized using [HeaderFormatClause.with].
 */
public fun <T> FormattedFrame<T>.formatHeader(): HeaderFormatClause<T, Any?> =
    HeaderFormatClause(
        df = df,
        oldHeaderFormatter = headerFormatter,
        oldCellFormatter = formatter,
    )

// endregion

// region terminal operations

/**
 * **Experimental API. It may be changed in the future.**
 *
 * Creates a new [FormattedFrame] that uses the specified [HeaderColFormatter] to format the selected headers.
 *
 * Header formatting is additive: attributes from already-applied header formatters are combined with the newly
 *
 *  returned attributes using [CellAttributes.and]. If a parent column group is selected, its attributes are
 * applied to its children unless explicitly overridden.
 */
@Suppress("UNCHECKED_CAST")
public fun <T, C> HeaderFormatClause<T, C>.with(formatter: HeaderColFormatter<C>): FormattedFrame<T> =
    formatHeaderImpl(formatter)

// endregion
