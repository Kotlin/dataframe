package org.jetbrains.kotlinx.dataframe.spring.examples

/**
 * Main entry point for running all DataFrame Spring examples.
 * 
 * This class provides a centralized way to run all the examples in the module.
 * Each example demonstrates a different aspect of the DataFrame Spring integration.
 */
fun main() {
    println("Running DataFrame Spring Examples")
    println("================================")
    
    println("\nExample 1: CSV Data Source")
    runCsvExample()
    
    println("\nExample 2: CSV Data Source with Spring Context")
    runCsvWithContextExample()
    
    println("\nCompleted all examples!")
}

/**
 * Runs the basic CSV Data Source example.
 * 
 * This example demonstrates how to use the @CsvDataSource annotation
 * with a DataFramePostProcessor to load data from CSV files.
 */
fun runCsvExample() {
    try {
        csvDataSourceExample()
    } catch (e: Exception) {
        println("Error running CSV example: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * Runs the CSV Data Source with Spring Context example.
 * 
 * This example demonstrates how to use the @CsvDataSource annotation
 * within a Spring application context.
 */
fun runCsvWithContextExample() {
    try {
        csvDataSourceWithContextExample()
    } catch (e: Exception) {
        println("Error running CSV with Context example: ${e.message}")
        e.printStackTrace()
    }
}
