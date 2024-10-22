package org.jetbrains.kotlinx.dataframe.impl.columns.tree

internal interface ReferenceData {
    val originalIndex: Int
    val wasRemoved: Boolean
}
