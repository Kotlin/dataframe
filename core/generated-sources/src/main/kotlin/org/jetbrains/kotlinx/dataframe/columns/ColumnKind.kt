package org.jetbrains.kotlinx.dataframe.columns

public enum class ColumnKind {
    Value {
        override fun toString(): String = "ValueColumn"
    },
    Group {
        override fun toString(): String = "ColumnGroup"
    },
    Frame {
        override fun toString(): String = "FrameColumn"
    }
}
