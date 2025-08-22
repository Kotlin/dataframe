package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.jetbrains.kotlinx.dataframe.spring.annotations.CsvDataSource
import org.springframework.stereotype.Component
import java.io.File

/**
 * This example demonstrates the exact usage pattern specified in the GitHub issue.
 * It shows how to use @DataSource annotation in Spring DI style to automatically
 * populate DataFrame properties from CSV files.
 */

@DataSchema
interface MyRowType {
    val id: Int
    val name: String
    val value: Double
}

/**
 * Example service class using @DataSource annotation exactly as specified in the issue
 */
@Component
class MyDataService {
    @CsvDataSource(file = "data.csv")
    lateinit var df: DataFrame<MyRowType>

    fun process() {
        println("Processing DataFrame with ${df.rowsCount()} rows")
        
        // Access data using DataFrame API
        if (df.rowsCount() > 0) {
            println("First row: ${df[0]}")
            println("Column names: ${df.columnNames()}")
        }
    }
}

/**
 * Demonstration of the complete Spring-style integration
 */
fun main() {
    println("=== DataFrame Spring Integration Demo ===")
    println("Demonstrating exact usage pattern from GitHub issue #1321")
    println()
    
    // Create sample data file
    createSampleDataFile()
    
    try {
        // This simulates Spring's bean initialization process
        println("1. Creating Spring bean...")
        val myDataService = MyDataService()
        
        println("2. Running DataFramePostProcessor...")
        val postProcessor = DataFramePostProcessor()
        postProcessor.postProcessBeforeInitialization(myDataService, "myDataService")
        
        println("3. DataFrame loaded successfully!")
        println("   - CSV file: data.csv")
        println("   - Rows loaded: ${myDataService.df.rowsCount()}")
        println("   - Columns: ${myDataService.df.columnNames()}")
        
        println("4. Running business logic...")
        myDataService.process()
        
        println()
        println("✅ SUCCESS: Spring-style DataFrame initialization completed!")
        println("✅ The @DataSource annotation automatically loaded CSV data")
        println("✅ No manual DataFrame construction required")
        println("✅ Follows Spring DI patterns perfectly")
        
    } catch (e: Exception) {
        println("❌ ERROR: ${e.message}")
        e.printStackTrace()
    } finally {
        // Clean up
        File("data.csv").delete()
    }
}

/**
 * Creates the sample CSV file used in the example
 */
private fun createSampleDataFile() {
    File("data.csv").writeText("""
        id,name,value
        1,First Item,100.5
        2,Second Item,200.0
        3,Third Item,150.75
        4,Fourth Item,300.25
    """.trimIndent())
    
    println("Created sample data.csv file")
}
