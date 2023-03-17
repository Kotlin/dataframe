package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.matches(): DataFrame<T> = matches(yes = true, no = false)
public fun <T, R> PivotGroupBy<T>.matches(yes: R, no: R): DataFrame<T> = aggregate { yes default no }

// endregion
