package org.jetbrains.kotlinx.dataframe.util

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.CellAttributes
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.formatHeader
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.with

internal val baseColorSet = listOf(
    FormattingDsl.rgb(244, 67, 54), // red
    FormattingDsl.rgb(33, 150, 243), // blue
    FormattingDsl.rgb(76, 175, 80), // green
    FormattingDsl.rgb(255, 152, 0), // orange
    FormattingDsl.rgb(156, 39, 176), // purple
    FormattingDsl.rgb(0, 150, 136), // teal
    FormattingDsl.rgb(233, 30, 99), // pink/magenta
)

internal val FormattingDsl.monospace: CellAttributes
    get() = attr(
        "font-family",
        "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace",
    )

internal fun <T> DataFrame<T>.defaultHeaderFormatting(headers: ColumnsSelector<T, *>): FormattedFrame<T> {
    val columns = getColumnsWithPaths(headers)
    require(columns.size <= baseColorSet.size) {
        "Too many headers: ${columns.size}. Max supported is ${baseColorSet.size}."
    }

    val start = formatHeader().with { null }

    return columns.foldIndexed(start) { idx, acc, header ->
        acc.formatHeader { header }
            .with {
                textColor(baseColorSet[idx]) and monospace
            }
    }
}

@Suppress("INVISIBLE_REFERENCE")
internal fun <T> FormattedFrame<T>.defaultHeaderFormatting(headers: ColumnsSelector<T, *>): FormattedFrame<T> {
    val columns = df.getColumnsWithPaths(headers)
    require(columns.size <= baseColorSet.size) {
        "Too many headers: ${columns.size}. Max supported is ${baseColorSet.size}."
    }

    val start = formatHeader().with { null }

    return columns.foldIndexed(start) { idx, acc, header ->
        acc.formatHeader { header }
            .with {
                textColor(baseColorSet[idx]) and monospace
            }
    }
}
