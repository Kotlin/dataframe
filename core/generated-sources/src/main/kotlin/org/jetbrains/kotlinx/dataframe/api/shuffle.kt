package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.indices
import kotlin.random.Random

// region DataColumn

public fun <T> DataColumn<T>.shuffle(random: Random): DataColumn<T> = get(indices.shuffled(random))

public fun <T> DataColumn<T>.shuffle(): DataColumn<T> = get(indices.shuffled())

// endregion

// region DataFrame

public fun <T> DataFrame<T>.shuffle(random: Random): DataFrame<T> = getRows(indices.shuffled(random))

public fun <T> DataFrame<T>.shuffle(): DataFrame<T> = getRows(indices.shuffled())

// endregion
