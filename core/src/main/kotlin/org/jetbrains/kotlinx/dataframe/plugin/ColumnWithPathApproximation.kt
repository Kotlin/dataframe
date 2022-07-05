package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath

/**
 * @see ColumnWithPath
 */
internal class ColumnWithPathApproximation(val path: ColumnPathApproximation, val column: SimpleCol)
