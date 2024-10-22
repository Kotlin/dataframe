package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow

// region Pivot

public fun <T> Pivot<T>.frames(): DataRow<T> = aggregate { this }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.frames(): DataFrame<T> = aggregate { this }

// endregion
