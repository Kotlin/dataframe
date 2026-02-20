package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters

public fun interface MarkerNameProvider : (ColumnPath) -> String {
    public companion object {
        public val fromColumnName: MarkerNameProvider = { columnPath ->
            columnPath.last().toCamelCaseByDelimiters().replaceFirstChar { it.uppercase() }
        }
    }
}
