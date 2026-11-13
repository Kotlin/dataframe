package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine

// region Pivot

public fun <T> Pivot<T>.frames(): DataRow<*> = aggregate { this }

// endregion

// region PivotGroupBy

@Refine
@Interpretable("PivotGroupByFrames")
public fun <T> PivotGroupBy<T>.frames(): DataFrame<*> = aggregate { this }

// endregion
