package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters

/**
 * Name conflicts are resolved by numerical suffix
 */
public sealed interface MarkerNameProvider {
    public companion object {
        /**
         * [MarkerNameProvider] that generates a name from the column name.
         * It uses the last component of the [ColumnPath], converts it to camel case and capitalizes it.
         * This provider is useful for generating descriptive names for nested markers that reflect the structure they represent.
         */
        public val fromColumnName: MarkerNameProvider = GeneratedName { columnPath ->
            columnPath.last().toCamelCaseByDelimiters().replaceFirstChar { it.uppercase() }
        }
    }

    public data object PredefinedName : MarkerNameProvider

    public fun interface GeneratedName :
        MarkerNameProvider,
        (ColumnPath) -> String
}
