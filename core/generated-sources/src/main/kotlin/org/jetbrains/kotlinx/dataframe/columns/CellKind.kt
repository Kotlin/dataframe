package org.jetbrains.kotlinx.dataframe.columns

/**
 *	Represents special kinds of elements that can be found within a Column.
 *	This is similar to the [ColumnKind], but it applies to specific elements of the Column.
 *	Its main use is to provide metadata during serialization for visualization within the KTNB plugin.
 */
internal enum class CellKind {
    /**
     * Represents a cell kind within a Column that is specifically convertible to a DataFrame.
     */
    DataFrameConvertable {
        override fun toString(): String = "DataFrameConvertable"
    },
}
