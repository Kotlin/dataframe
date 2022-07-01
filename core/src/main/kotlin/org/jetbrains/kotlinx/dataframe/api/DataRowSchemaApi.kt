package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame

public interface DataRowSchema

public inline fun <reified T : DataRowSchema> dataFrameOf(vararg rows: T): DataFrame<T> =
    rows.asIterable().toDataFrame()

public inline fun <reified T : DataRowSchema> DataFrame<T>.append(vararg rows: T): DataFrame<T> =
    concat(dataFrameOf(*rows))
