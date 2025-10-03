package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl
import org.jetbrains.kotlinx.dataframe.api.HeaderColFormatter
import org.jetbrains.kotlinx.dataframe.api.HeaderFormatClause
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.impl.getColumnPaths

internal fun <T, C> HeaderFormatClause<T, C>.formatHeaderImpl(formatter: HeaderColFormatter<C>): FormattedFrame<T> {
    val selectedPaths = df.getColumnPaths(UnresolvedColumnsPolicy.Skip, columns).toSet()
    val oldHeader = oldHeaderFormatter

    val composedHeader: HeaderColFormatter<Any?> = { col ->
        val typedCol = col as ColumnWithPath<C>
        val existingAttr = oldHeader?.invoke(FormattingDsl, typedCol)
        val newAttr = if (col.path in selectedPaths) formatter(FormattingDsl, typedCol) else null
        existingAttr and newAttr
    }

    return FormattedFrame(df, oldCellFormatter, composedHeader)
}
