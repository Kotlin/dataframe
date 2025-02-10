package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.annotations.HasSchema

@HasSchema(schemaArg = 0)
public abstract class AggregateGroupedDsl<out T> : AggregateDsl<T>()
