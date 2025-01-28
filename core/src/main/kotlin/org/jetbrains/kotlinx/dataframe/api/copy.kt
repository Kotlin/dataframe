package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.CandidateForRemoval
// region DataFrame

@CandidateForRemoval
public fun <T> DataFrame<T>.copy(): DataFrame<T> = columns().toDataFrame().cast()

// endregion
