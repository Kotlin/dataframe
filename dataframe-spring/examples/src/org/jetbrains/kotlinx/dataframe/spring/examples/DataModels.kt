package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.spring.annotations.CsvDataSource
import java.io.File

/**
 * Data schema for customer records.
 * 
 * This interface defines the structure of customer data loaded from CSV files.
 */
@DataSchema
interface CustomerRow {
    val id: Int
    val name: String
    val email: String
    val age: Int
}

/**
 * Data schema for sales records.
 * 
 * This interface defines the structure of sales data loaded from CSV files.
 */
@DataSchema
interface SalesRow {
    val saleId: Int
    val customerId: Int
    val amount: Double
    val date: String
}

/**
 * Example service class that uses DataFrame annotations.
 * 
 * This class demonstrates how to use the @CsvDataSource annotation
 * to automatically load data from CSV files into DataFrames.
 */
class ExampleDataService {
    @CsvDataSource(file = CUSTOMERS_CSV)
    lateinit var customerData: DataFrame<CustomerRow>

    @CsvDataSource(file = SALES_CSV, delimiter = ';')
    lateinit var salesData: DataFrame<SalesRow>

    /**
     * Prints the total number of customers.
     */
    fun printCustomerCount() {
        println("Number of customers: ${customerData.rowsCount()}")
    }

    /**
     * Prints the total number of sales.
     */
    fun printSalesCount() {
        println("Number of sales: ${salesData.rowsCount()}")
    }
}

// Constants for file paths
const val CUSTOMERS_CSV = "data\\customers.csv"
const val SALES_CSV = "data\\sales.csv"

/**
 * Creates sample CSV data files for the examples.
 * 
 * This function creates customer and sales data files in the specified directory.
 * 
 * @param directory The directory where the files will be created
 */
fun createSampleData(directory: String) {
    // Create customer data
    File("$directory\\$CUSTOMERS_CSV").apply {
        parentFile.mkdirs()
        writeText("""
            id,name,email,age
            1,John Doe,john@example.com,28
            2,Jane Smith,jane@example.com,32
            3,Bob Johnson,bob@example.com,25
            4,Alice Brown,alice@example.com,30
        """.trimIndent())
    }
    
    // Create sales data with semicolon delimiter
    File("$directory\\$SALES_CSV").apply {
        parentFile.mkdirs()
        writeText("""
            sale_id;customer_id;amount;date
            1;1;150.00;2023-01-15
            2;2;200.50;2023-01-16
            3;1;75.25;2023-01-17
            4;3;300.00;2023-01-18
        """.trimIndent())
    }
}

/**
 * Cleans up the sample data files.
 * 
 * @param directory The directory where the files were created
 */
fun cleanupSampleData(directory: String) {
    File("$directory\\$CUSTOMERS_CSV").delete()
    File("$directory\\$SALES_CSV").delete()
}
