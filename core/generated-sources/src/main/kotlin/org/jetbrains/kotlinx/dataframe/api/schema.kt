package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.IntellijPluginApi
import org.jetbrains.kotlinx.dataframe.impl.api.compileTimeSchemaImpl
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

// region DataRow

public fun AnyRow.schema(): DataFrameSchema = owner.schema()

// endregion

// region DataFrame

@IntellijPluginApi
public fun AnyFrame.schema(): DataFrameSchema = extractSchema()

// endregion

// region GroupBy

public fun GroupBy<*, *>.schema(): DataFrameSchema = toDataFrame().schema()

// endregion

/**
 * [ordered] - if true, columns are ordered the same as in runtime schema for easier diff between the two.
 * if false, columns are ordered as they are represented in the compiler plugin
 */
public inline fun <reified T> DataFrame<T>.compileTimeSchema(ordered: Boolean = true): DataFrameSchema =
    compileTimeSchemaImpl(if (ordered) schema() else null, T::class)
