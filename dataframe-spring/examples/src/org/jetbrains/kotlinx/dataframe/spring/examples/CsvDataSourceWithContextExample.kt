package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Example demonstrating CSV data source processing with Spring context.
 * 
 * This example shows how to use the DataFramePostProcessor within a Spring
 * application context to process beans with @CsvDataSource annotations.
 */
fun csvDataSourceWithContextExample() {
    // Create sample CSV files in the resources directory
    val resourcesDir = System.getProperty("user.dir") + "\\dataframe-spring\\examples\\resources"
    createSampleData(resourcesDir)
    
    try {
        println("1. Bootstrapping Spring context...")
        val ctx = AnnotationConfigApplicationContext().apply {
            register(DataFramePostProcessor::class.java)
            register(ExampleDataService::class.java)
            refresh()
        }

        println("2. Getting ExampleDataService bean from context...")
        val dataService = ctx.getBean(ExampleDataService::class.java)

        println("3. DataFrame loaded successfully!")
        println("   - Rows loaded: ${dataService.customerData.rowsCount()}")
        println("   - Columns: ${dataService.customerData.columnNames()}")

        println("4. Running business logic...")
        dataService.printCustomerCount()
        dataService.printSalesCount()

        println("✓ @CsvDataSource annotation processing with Spring context completed successfully!")
        
    } catch (e: Exception) {
        println("✗ Error processing @DataSource annotations: ${e.message}")
        e.printStackTrace()
    } finally {
        // Clean up sample files
        cleanupSampleData(resourcesDir)
    }
}
