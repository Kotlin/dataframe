package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.util.PRINT

// region DataColumn

public fun <T> DataColumn<T>.print(): Unit = println(this)

// endregion

// region DataRow

public fun <T> DataRow<T>.print(): Unit = println(this)

// endregion

// region DataFrame

@Deprecated(message = PRINT, level = DeprecationLevel.HIDDEN)
public fun <T> DataFrame<T>.print(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = false,
    title: Boolean = false,
): Unit = print(rowsLimit, valueLimit, borders, alignLeft, columnTypes, title, true)

public fun <T> DataFrame<T>.print(
    rowsLimit: Int = 20,
    valueLimit: Int = 40,
    borders: Boolean = false,
    alignLeft: Boolean = false,
    columnTypes: Boolean = false,
    title: Boolean = false,
    rowIndex: Boolean = true,
): Unit = println(renderToString(rowsLimit, valueLimit, borders, alignLeft, columnTypes, title, rowIndex))

// endregion

// region GroupBy

public fun <T, G> GroupBy<T, G>.print(): Unit = println(this)

// endregion

// region DataFrameSchema

public fun DataFrameSchema.print(): Unit = println(this)

// endregion

public fun CodeString.print(): Unit = println(this)
