package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame

// region DataFrame

/**
 * Returns a DataFrame containing the last [numRows] rows.
 *
 * Equivalent to [takeLast].
 *
 * @param numRows The number of rows to return from the end of the DataFrame. Defaults to 5.
 * @return A DataFrame containing the last [numRows] rows.
 */
public fun <T> DataFrame<T>.tail(numRows: Int = 5): DataFrame<T> = takeLast(numRows)

// endregion
