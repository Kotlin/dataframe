@file:Suppress("UNUSED_VARIABLE", "unused", "UNCHECKED_CAST", "ktlint", "ClassName")

package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.generateCode
import org.jetbrains.kotlinx.dataframe.api.generateDataClasses
import org.jetbrains.kotlinx.dataframe.api.generateInterfaces
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.sumOf
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class Generate : DataFrameSampleHelper("generate_docs", "api") {
    val ordersAlice = dataFrameOf(
        "orderId" to listOf(101, 102),
        "amount" to listOf(50.0, 75.5),
    )

    val ordersBob = dataFrameOf(
        "orderId" to listOf(103, 104, 105),
        "amount" to listOf(20.0, 30.0, 25.0),
    )

    val df = dataFrameOf(
        "user" to listOf("Alice", "Bob"),
        "orders" to listOf(ordersAlice, ordersBob),
    )

    @DataSchema(isOpen = false)
    interface _DataFrameType11 {
        val amount: kotlin.Double
        val orderId: kotlin.Int
    }

    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<_DataFrameType11>.amount: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Double>
        @JvmName(
            "_DataFrameType11_amount",
        )
        get() = this["amount"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Double>
    val org.jetbrains.kotlinx.dataframe.DataRow<_DataFrameType11>.amount: kotlin.Double
        @JvmName("_DataFrameType11_amount")
        get() = this["amount"] as kotlin.Double
    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<_DataFrameType11>.orderId: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int>
        @JvmName(
            "_DataFrameType11_orderId",
        )
        get() = this["orderId"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int>
    val org.jetbrains.kotlinx.dataframe.DataRow<_DataFrameType11>.orderId: kotlin.Int
        @JvmName("_DataFrameType11_orderId")
        get() = this["orderId"] as kotlin.Int

    @DataSchema
    interface _DataFrameType1 {
        val orders: List<_DataFrameType11>
        val user: kotlin.String
    }

    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<_DataFrameType1>.orders: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<_DataFrameType11>>
        @JvmName(
            "_DataFrameType1_orders",
        )
        get() = this["orders"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<_DataFrameType11>>
    val org.jetbrains.kotlinx.dataframe.DataRow<_DataFrameType1>.orders: org.jetbrains.kotlinx.dataframe.DataFrame<_DataFrameType11>
        @JvmName(
            "_DataFrameType1_orders",
        )
        get() = this["orders"] as org.jetbrains.kotlinx.dataframe.DataFrame<_DataFrameType11>
    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<_DataFrameType1>.user: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
        @JvmName(
            "_DataFrameType1_user",
        )
        get() = this["user"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
    val org.jetbrains.kotlinx.dataframe.DataRow<_DataFrameType1>.user: kotlin.String
        @JvmName("_DataFrameType1_user")
        get() = this["user"] as kotlin.String

    @DataSchema
    data class Customer1(val amount: Double, val orderId: Int)

    @DataSchema
    data class Customer(val orders: List<Customer1>, val user: String)

    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer1>.amount: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Double>
        @JvmName(
            "Customer1_amount",
        )
        get() = this["amount"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Double>
    val org.jetbrains.kotlinx.dataframe.DataRow<Customer1>.amount: kotlin.Double
        @JvmName("Customer1_amount")
        get() = this["amount"] as kotlin.Double
    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer1>.orderId: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int>
        @JvmName(
            "Customer1_orderId",
        )
        get() = this["orderId"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int>
    val org.jetbrains.kotlinx.dataframe.DataRow<Customer1>.orderId: kotlin.Int
        @JvmName("Customer1_orderId")
        get() = this["orderId"] as kotlin.Int

    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer>.orders: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Customer1>>
        @JvmName(
            "Customer_orders",
        )
        get() = this["orders"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Customer1>>
    val org.jetbrains.kotlinx.dataframe.DataRow<Customer>.orders: org.jetbrains.kotlinx.dataframe.DataFrame<Customer1>
        @JvmName(
            "Customer_orders",
        )
        get() = this["orders"] as org.jetbrains.kotlinx.dataframe.DataFrame<Customer1>
    val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer>.user: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
        @JvmName(
            "Customer_user",
        )
        get() = this["user"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
    val org.jetbrains.kotlinx.dataframe.DataRow<Customer>.user: kotlin.String
        @JvmName("Customer_user")
        get() = this["user"] as kotlin.String

    private val customers: List<Customer> = df.cast<Customer>().toList()

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
        df.cast<_DataFrameType1>().filter { orders.all { orderId >= 102 } }
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
        df.generateCode("Customer")
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
