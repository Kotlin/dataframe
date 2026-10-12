package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import java.math.BigDecimal

@DataSchema
interface Order {
    val item: String
    val amount: BigDecimal
}

/**
 * Orders with a `java.math.BigDecimal` `amount` column.
 * Used in the big number examples of the statistics topics.
 */
val ordersDf: DataFrame<Order> = dataFrameOf(
    "item" to listOf("Laptop", "Phone", "Tablet", "Monitor"),
    "amount" to listOf(
        BigDecimal("1200.50"),
        BigDecimal("899.99"),
        BigDecimal("450.25"),
        BigDecimal("310.00"),
    ),
).cast()
