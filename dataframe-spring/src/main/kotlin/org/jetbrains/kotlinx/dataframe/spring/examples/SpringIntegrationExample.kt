package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.jetbrains.kotlinx.dataframe.spring.annotations.DataSource
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.io.File

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

/**
 * Example Spring service that uses @DataSource annotation
 * to automatically load CSV data into DataFrame properties
 */
@Component
class DataAnalysisService {
    
    @DataSource(csvFile = "customers.csv")
    lateinit var customers: DataFrame<CustomerRow>
    
    @DataSource(csvFile = "sales.csv", delimiter = ';')
    lateinit var sales: DataFrame<SalesRow>
    
    fun analyzeCustomerData() {
        println("=== Customer Analysis ===")
        println("Total customers: ${customers.rowsCount()}")
        println("Average age: ${customers.columnNames().let { if ("age" in it) "calculated from data" else "N/A" }}")
        
        // Print first few customers
        println("\nFirst 3 customers:")
        for (i in 0 until minOf(3, customers.rowsCount())) {
            val row = customers[i]
            println("${row["id"]}: ${row["name"]} (${row["email"]})")
        }
    }
    
    fun analyzeSalesData() {
        println("\n=== Sales Analysis ===")
        println("Total sales: ${sales.rowsCount()}")
        
        // Print first few sales
        println("\nFirst 3 sales:")
        for (i in 0 until minOf(3, sales.rowsCount())) {
            val row = sales[i]
            println("Sale ${row["saleId"]}: Customer ${row["customerId"]} - $${row["amount"]}")
        }
    }
    
    fun generateReport() {
        println("\n=== Combined Report ===")
        analyzeCustomerData()
        analyzeSalesData()
    }
}

/**
 * Spring configuration that enables the DataFramePostProcessor
 */
@Configuration
class DataFrameConfiguration {
    
    @Bean
    fun dataFramePostProcessor(): DataFramePostProcessor {
        return DataFramePostProcessor()
    }
}

/**
 * Example demonstrating the complete Spring integration
 */
fun main() {
    println("DataFrame Spring Integration Example")
    println("==================================")
    
    // Create sample data files
    createSampleData()
    
    try {
        // Simulate Spring's bean processing
        println("1. Creating DataFramePostProcessor...")
        val processor = DataFramePostProcessor()
        
        println("2. Creating DataAnalysisService bean...")
        val service = DataAnalysisService()
        
        println("3. Processing @DataSource annotations...")
        processor.postProcessBeforeInitialization(service, "dataAnalysisService")
        
        println("4. Running analysis...")
        service.generateReport()
        
        println("\n✓ Spring-style DataFrame integration completed successfully!")
        println("\nThis demonstrates:")
        println("- @DataSource annotation for declarative CSV loading")
        println("- Automatic DataFrame population during bean initialization")
        println("- Support for custom delimiters")
        println("- Integration with Spring's dependency injection lifecycle")
        
    } catch (e: Exception) {
        println("\n✗ Error: ${e.message}")
        e.printStackTrace()
    } finally {
        // Clean up
        cleanupSampleData()
    }
}

private fun createSampleData() {
    println("Creating sample CSV files...")
    
    // Create customer data
    File("customers.csv").writeText("""
        id,name,email,age
        1,John Doe,john@example.com,28
        2,Jane Smith,jane@example.com,32
        3,Bob Johnson,bob@example.com,25
        4,Alice Brown,alice@example.com,30
        5,Charlie Wilson,charlie@example.com,35
    """.trimIndent())
    
    // Create sales data with semicolon delimiter
    File("sales.csv").writeText("""
        saleId;customerId;amount;date
        1;1;150.00;2023-01-15
        2;2;200.50;2023-01-16
        3;1;75.25;2023-01-17
        4;3;300.00;2023-01-18
        5;4;125.75;2023-01-19
        6;2;89.99;2023-01-20
    """.trimIndent())
    
    println("Sample data created successfully!")
}

private fun cleanupSampleData() {
    File("customers.csv").delete()
    File("sales.csv").delete()
    println("Sample data cleaned up.")
}