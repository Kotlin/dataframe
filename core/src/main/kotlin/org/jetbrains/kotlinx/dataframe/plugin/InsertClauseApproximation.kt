package org.jetbrains.kotlinx.dataframe.plugin

import org.jetbrains.kotlinx.dataframe.api.InsertClause

/**
 * @see InsertClause
*/
internal class InsertClauseApproximation(val df: PluginDataFrameSchema, val column: SimpleCol)
