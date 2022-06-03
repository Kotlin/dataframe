package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

// region DataRow

public fun AnyRow.schema(): DataFrameSchema = owner.schema()

// endregion

// region DataFrame

public fun AnyFrame.schema(): DataFrameSchema = extractSchema()

// endregion

// region GroupBy

public fun GroupBy<*, *>.schema(): DataFrameSchema = toDataFrame().schema()

// endregion
