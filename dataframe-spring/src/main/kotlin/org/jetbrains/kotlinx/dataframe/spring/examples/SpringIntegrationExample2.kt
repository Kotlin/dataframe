package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.spring.DataFramePostProcessor
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.io.File

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
        println("1. Bootstrapping Spring context...")
        val ctx = AnnotationConfigApplicationContext().apply {
            register(DataFramePostProcessor::class.java)
            register(MyDataService::class.java)
            refresh()
        }

        println("2. Getting MyDataService bean from context...")
        val myDataService = ctx.getBean(MyDataService::class.java)
        
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
