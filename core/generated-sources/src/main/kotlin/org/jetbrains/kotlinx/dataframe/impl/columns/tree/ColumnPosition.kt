package org.jetbrains.kotlinx.dataframe.impl.columns.tree

import org.jetbrains.kotlinx.dataframe.AnyCol

internal data class ColumnPosition(
    override val originalIndex: Int,
    override var wasRemoved: Boolean,
    var column: AnyCol?
) : ReferenceData
