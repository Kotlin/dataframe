package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.jetbrains.kotlinx.dataframe.spring.annotations.CsvDataSource
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.io.File

private const val CUSTOMERS_CSV = "customers.csv"
private const val SALES_CSV = "sales.csv"

// Define the data schema
@DataSchema
interface CustomerRow {
    val id: Int
    val name: String
    val email: String
    val age: Int
}

@DataSchema
interface SalesRow {
    val saleId: Int
    val customerId: Int
    val amount: Double
    val date: String
}

class ExampleDataService {
    @CsvDataSource(file = CUSTOMERS_CSV)
    lateinit var customerData: DataFrame<CustomerRow>

    @CsvDataSource(file = SALES_CSV, delimiter = ';')
    lateinit var salesData: DataFrame<SalesRow>

    fun printCustomerCount() {
        println("Number of customers: ${customerData.rowsCount()}")
    }

    fun printSalesCount() {
        println("Number of sales: ${salesData.rowsCount()}")
    }
}

/**
 * Entry point for the application. This method demonstrates the use of a Spring context
 * with a custom annotation processor to load and process CSV data into DataFrames.
 *
 * The method performs the following steps:
 * 1. Generates sample customer and sales CSV files for demonstration purposes.
 * 2. Initializes a Spring application context and registers the required components, including
 *    DataFramePostProcessor and ExampleDataService.
 * 3. Loads the CSV data into DataFrames by leveraging the @CsvDataSource annotation.
 * 4. Outputs information about the loaded data, such as file name, number of rows, and column names.
 * 5. Executes example business logic using the ExampleDataService, such as printing customer and
 *    sales counts.
 * 6. Logs any errors encountered during processing and ensures cleanup of generated sample files.
 */
fun main() {
    // Create sample CSV files
    createSampleData()
    
    try {
        println("1. Bootstrapping Spring context...")
        val ctx = AnnotationConfigApplicationContext().apply {
            register(DataFramePostProcessor::class.java)
            register(ExampleDataService::class.java)
            refresh()
        }

        println("2. Getting MyDataService bean from context...")
        val myDataService = ctx.getBean(ExampleDataService::class.java)

        println("3. DataFrame loaded successfully!")
        println("   - CSV file: data.csv")
        println("   - Rows loaded: ${myDataService.customerData.rowsCount()}")
        println("   - Columns: ${myDataService.customerData.columnNames()}")

        println("4. Running business logic...")
        myDataService.printCustomerCount()
        myDataService.printSalesCount()

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
