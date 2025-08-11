package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.jetbrains.kotlinx.dataframe.spring.annotations.DataSource
import java.io.File

/**
 * Example demonstrating the @DataSource annotation usage
 */
class ExampleDataService {
    @DataSource(csvFile = "example-data.csv")
    lateinit var customerData: DataFrame<*>
    
    @DataSource(csvFile = "sales.csv", delimiter = ';')
    lateinit var salesData: DataFrame<*>
    
    fun printCustomerCount() {
        println("Number of customers: ${customerData.rowsCount()}")
    }
    
    fun printSalesCount() {
        println("Number of sales: ${salesData.rowsCount()}")
    }
}

/**
 * Example main function showing how to use the DataFramePostProcessor
 */
fun main() {
    // Create sample CSV files
    createSampleData()
    
    try {
        // Create the post processor
        val processor = DataFramePostProcessor()
        
        // Create and process the service
        val service = ExampleDataService()
        processor.postProcessBeforeInitialization(service, "exampleService")
        
        // Use the service
        service.printCustomerCount()
        service.printSalesCount()
        
        println("✓ @DataSource annotation processing completed successfully!")
        
    } catch (e: Exception) {
        println("✗ Error processing @DataSource annotations: ${e.message}")
        e.printStackTrace()
    } finally {
        // Clean up sample files
        cleanupSampleData()
    }
}

private fun createSampleData() {
    // Create customer data
    File("example-data.csv").writeText("""
        id,name,email,age
        1,John Doe,john@example.com,28
        2,Jane Smith,jane@example.com,32
        3,Bob Johnson,bob@example.com,25
        4,Alice Brown,alice@example.com,30
    """.trimIndent())
    
    // Create sales data with semicolon delimiter
    File("sales.csv").writeText("""
        sale_id;customer_id;amount;date
        1;1;150.00;2023-01-15
        2;2;200.50;2023-01-16
        3;1;75.25;2023-01-17
        4;3;300.00;2023-01-18
    """.trimIndent())
}

private fun cleanupSampleData() {
    File("example-data.csv").delete()
    File("sales.csv").delete()
}