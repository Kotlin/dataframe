@file:Suppress("UNUSED_VARIABLE", "unused", "UNCHECKED_CAST", "ktlint", "ClassName")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.generateDataClasses
import org.jetbrains.kotlinx.dataframe.api.generateInterfaces
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class Generate : DataFrameSampleHelper("generate_docs", "api") {

    @DataSchema
    data class Orders(
        val orderId: Int,
        val amount: Double,
    )

    private val ordersAlice = dataFrameOf(
        "orderId" to listOf(101, 102),
        "amount" to listOf(50.0, 75.5),
    ).cast<Orders>()

    private val ordersBob = dataFrameOf(
        "orderId" to listOf(103, 104, 105),
        "amount" to listOf(20.0, 30.0, 25.0),
    ).cast<Orders>()

    @DataSchema
    data class Customer(
        val user: String,
        val orders: List<Orders>,
    )

    private val df = dataFrameOf(
        "user" to listOf("Alice", "Bob"),
        "orders" to listOf(ordersAlice, ordersBob),
    ).cast<Customer>()

    @Test
    fun notebook_test_generate_docs_1() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_generate_docs_2() {
        // SampleStart
        df.generateInterfaces()
        // SampleEnd
    }

    @Test
    fun notebook_test_generate_docs_3() {
        // SampleStart
        df.filter { orders.all { orderId >= 102 } }
        // SampleEnd
        // .saveDfHtmlSample()
    }

    @Test
    fun notebook_test_generate_docs_4() {
        // SampleStart
        df.generateDataClasses("Customer")
        // SampleEnd
    }

    @Test
    fun notebook_test_generate_docs_5() {
        // SampleStart
        val customers: List<Customer> = df.cast<Customer>().toList()
        // SampleEnd
    }

    @Test
    fun notebook_test_generate_docs_6() {
        // SampleStart
        df.generateInterfaces(markerName = "Customer")
        // SampleEnd
    }

    @Test
    fun notebook_test_generate_docs_7() {
        // SampleStart
        df.cast<Customer>()
            .add("ordersTotal") { orders.sumOf { it.amount } }
            .filter { user.startsWith("A") }
            .rename { user }.into("customer")
        // SampleEnd
        //   .saveDfHtmlSample()
    }
}
