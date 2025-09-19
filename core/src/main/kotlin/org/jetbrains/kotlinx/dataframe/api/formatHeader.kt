package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.getColumnPaths
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import kotlin.reflect.KProperty
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API

/**
 * Formats the headers (column names) of the selected columns similarly to how [format] formats cell values.
 *
 * This does not immediately produce a [FormattedFrame]; instead it returns a [HeaderFormatClause] which must be
 * finalized using [HeaderFormatClause.with].
 *
 * Header formatting is additive and supports nested column groups: styles specified for a parent [ColumnGroup]
 * are inherited by its child columns unless overridden for the child.
 *
 * Examples:
 * ```kt
 * // center a single column header
 * df.formatHeader { age }.with { attr("text-align", "center") }
 *
 * // style a whole group header and override one child
 * df.formatHeader { name }.with { bold }
 *   .formatHeader { name.firstName }.with { textColor(green) }
 *   .toStandaloneHtml()
 * ```
 */
public typealias HeaderColFormatter<C> = FormattingDsl.(col: ColumnWithPath<C>) -> CellAttributes?

/**
 * Intermediate clause for header formatting, analogous to [FormatClause] but without rows.
 *
 * Use [with] to specify how to format the selected column headers, producing a [FormattedFrame].
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

// region DataFrame.formatHeader

/**
 * Selects [columns] whose headers should be formatted; finalize with [HeaderFormatClause.with].
 */
public fun <T, C> DataFrame<T>.formatHeader(columns: ColumnsSelector<T, C>): HeaderFormatClause<T, C> =
    HeaderFormatClause(this, columns)

/** Selects columns by [columns] names for header formatting. */
public fun <T> DataFrame<T>.formatHeader(vararg columns: String): HeaderFormatClause<T, Any?> =
    formatHeader { columns.toColumnSet() }

/** Selects all columns for header formatting. */
public fun <T> DataFrame<T>.formatHeader(): HeaderFormatClause<T, Any?> = HeaderFormatClause(this)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.formatHeader(vararg columns: ColumnReference<C>): HeaderFormatClause<T, C> =
    formatHeader { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.formatHeader(vararg columns: KProperty<C>): HeaderFormatClause<T, C> =
    formatHeader { columns.toColumnSet() }

// endregion

// region FormattedFrame.formatHeader

public fun <T, C> FormattedFrame<T>.formatHeader(columns: ColumnsSelector<T, C>): HeaderFormatClause<T, C> =
    HeaderFormatClause(df = df, columns = columns, oldHeaderFormatter = headerFormatter as HeaderColFormatter<C>?, oldCellFormatter = formatter)

public fun <T> FormattedFrame<T>.formatHeader(vararg columns: String): HeaderFormatClause<T, Any?> =
    formatHeader { columns.toColumnSet() }

public fun <T> FormattedFrame<T>.formatHeader(): HeaderFormatClause<T, Any?> =
    HeaderFormatClause(df = df, oldHeaderFormatter = headerFormatter as HeaderColFormatter<Any?>?, oldCellFormatter = formatter)

// endregion

// region terminal operations

@Suppress("UNCHECKED_CAST")
public fun <T, C> HeaderFormatClause<T, C>.with(formatter: HeaderColFormatter<C>): FormattedFrame<T> {
    val paths = df.getColumnPaths(UnresolvedColumnsPolicy.Skip, columns).toSet()
    val oldHeader = oldHeaderFormatter
    val composedHeader: HeaderColFormatter<Any?> = { col ->
        val parentCols = col.path.indices
            .map { i -> col.path.take(i + 1) }
            .dropLast(0) // include self and parents handled below
        // Merge attributes from parents that are selected
        val parentAttributes = parentCols
            .dropLast(1)
            .map { path -> ColumnWithPath(df[path], path) }
            .map { parentCol -> if (parentCol.path in paths) (oldHeader?.invoke(FormattingDsl, parentCol as ColumnWithPath<C>)) else null }
            .reduceOrNull(CellAttributes?::and)
        val selfAttr = if (col.path in paths) {
            val oldAttr = oldHeader?.invoke(FormattingDsl, col as ColumnWithPath<C>)
            oldAttr and formatter(FormattingDsl, col as ColumnWithPath<C>)
        } else {
            oldHeader?.invoke(FormattingDsl, col as ColumnWithPath<C>)
        }
        parentAttributes and selfAttr
    }
    @Suppress("UNCHECKED_CAST")
    return FormattedFrame(df, oldCellFormatter, composedHeader)
}

// endregion
