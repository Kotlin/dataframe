package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import java.io.File

private const val CUSTOMERS_CSV = "customers.csv"
private const val SALES_CSV = "sales.csv"

/**
 * The entry point of the application.
 *
 * This method demonstrates how a `DataFramePostProcessor` processes Spring beans
 * that are annotated with custom `@CsvDataSource` annotations and loads DataFrames
 * from CSV files. The method performs the following actions:
 *
 * 1. Creates sample CSV files containing customer and sales data.
 * 2. Initializes a `DataFramePostProcessor` to handle data source annotations.
 * 3. Processes the annotations for a Spring service (`ExampleDataService`) to load
 *    DataFrames from the sample CSV files.
 * 4. Outputs the results of the loaded DataFrames, including row count and column names.
 * 5. Executes business logic from the service to print customer and sales counts.
 * 6. Cleans up the generated sample CSV files.
 */
fun main() {
    // Create sample CSV files
    createSampleData()
    
    try {
        println("1. Creating DataFramePostProcessor...")
        val processor = DataFramePostProcessor()

        println("2. Processing @CsvDataSource annotations...")
        val service = ExampleDataService()
        processor.postProcessBeforeInitialization(service, "exampleService")
        
        println("3. DataFrame loaded successfully!")
        println("   - CSV file: data.csv")
        println("   - Rows loaded: ${service.customerData.rowsCount()}")
        println("   - Columns: ${service.customerData.columnNames()}")

        println("4. Running business logic...")
        service.printCustomerCount()
        service.printSalesCount()
        
        println("✓ @CsvDataSource annotation processing completed successfully!")
        
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
    File(CUSTOMERS_CSV).writeText("""
        id,name,email,age
        1,John Doe,john@example.com,28
        2,Jane Smith,jane@example.com,32
        3,Bob Johnson,bob@example.com,25
        4,Alice Brown,alice@example.com,30
    """.trimIndent())
    
    // Create sales data with semicolon delimiter
    File(SALES_CSV).writeText("""
        sale_id;customer_id;amount;date
        1;1;150.00;2023-01-15
        2;2;200.50;2023-01-16
        3;1;75.25;2023-01-17
        4;3;300.00;2023-01-18
    """.trimIndent())
}

private fun cleanupSampleData() {
    File(CUSTOMERS_CSV).delete()
    File(SALES_CSV).delete()
}
