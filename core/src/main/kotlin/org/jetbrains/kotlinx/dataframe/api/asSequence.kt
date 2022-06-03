package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow

// region DataColumn

public fun <T> DataColumn<T>.asSequence(): Sequence<T> = asIterable().asSequence()

// endregion

// region DataFrame

public fun <T> DataFrame<T>.asSequence(): Sequence<DataRow<T>> = rows().asSequence()

// endregion
