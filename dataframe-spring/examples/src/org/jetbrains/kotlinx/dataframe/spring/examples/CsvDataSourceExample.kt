package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor

/**
 * Example demonstrating basic CSV data source processing.
 * 
 * This example shows how to use the DataFramePostProcessor to process
 * a bean with @CsvDataSource annotations outside of a Spring context.
 */
fun csvDataSourceExample() {
    // Create sample CSV files in the resources directory
    val resourcesDir = System.getProperty("user.dir") + "\\dataframe-spring\\examples\\resources"
    createSampleData(resourcesDir)
    
    try {
        println("1. Creating DataFramePostProcessor...")
        val processor = DataFramePostProcessor()

        println("2. Processing @CsvDataSource annotations...")
        val service = ExampleDataService()
        processor.postProcessBeforeInitialization(service, "exampleService")
        
        println("3. DataFrame loaded successfully!")
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
        cleanupSampleData(resourcesDir)
    }
}
