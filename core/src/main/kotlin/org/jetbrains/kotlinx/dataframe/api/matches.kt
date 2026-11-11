package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine

// region PivotGroupBy

@Refine
@Interpretable("PivotGroupByMatches0")
public fun <T> PivotGroupBy<T>.matches(): DataFrame<*> = matches(yes = true, no = false)

@Refine
@Interpretable("PivotGroupByMatches1")
public fun <T, R> PivotGroupBy<T>.matches(yes: R, no: R): DataFrame<*> = aggregate { yes default no }

// endregion
