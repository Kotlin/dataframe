package org.jetbrains.kotlinx.dataframe.columns

public enum class CellKind {
    DataFrameConvertable {
        override fun toString(): String = "DataFrameConvertable"
    },
}
