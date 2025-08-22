package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.io.JSON
import org.jetbrains.kotlinx.dataframe.spring.annotations.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Comprehensive examples of the multi-format DataFrame Spring integration.
 * 
 * This demonstrates the Spring Data-inspired approach to DataFrame initialization
 * with support for CSV, JSON, Arrow/Parquet, and JDBC data sources.
 */
@Component
class MultiFormatDataService {

    // === CSV Data Sources ===
    
    @CsvDataSource(file = "data/sales.csv")
    lateinit var salesData: DataFrame<*>
    
    @CsvDataSource(file = "data/products.tsv", delimiter = '\t')
    lateinit var productData: DataFrame<*>
    
    @CsvDataSource(file = "data/raw_data.csv", header = false)
    lateinit var rawData: DataFrame<*>
    
    // === JSON Data Sources ===
    
    @JsonDataSource(file = "data/users.json")
    lateinit var userData: DataFrame<*>
    
    @JsonDataSource(
        file = "data/complex.json",
        typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS,
        unifyNumbers = false
    )
    lateinit var complexData: DataFrame<*>
    
    @JsonDataSource(
        file = "data/nested.json",
        keyValuePaths = ["user.preferences", "config.settings"]
    )
    lateinit var nestedData: DataFrame<*>
    
    // === Arrow/Parquet Data Sources ===
    
    @ArrowDataSource(file = "data/analytics.feather")
    lateinit var analyticsData: DataFrame<*>
    
    @ArrowDataSource(file = "data/timeseries.arrow", format = ArrowFormat.IPC)
    lateinit var timeseriesData: DataFrame<*>
    
    @ArrowDataSource(
        file = "data/large_dataset.parquet", 
        nullability = NullabilityOptions.Widening
    )
    lateinit var largeDataset: DataFrame<*>
    
    // === JDBC Data Sources ===
    
    @JdbcDataSource(
        connectionBean = "dataSource",
        tableName = "customers"
    )
    lateinit var customerData: DataFrame<*>
    
    @JdbcDataSource(
        url = "jdbc:h2:mem:testdb",
        username = "sa",
        password = "",
        query = "SELECT * FROM orders WHERE status = 'COMPLETED'"
    )
    lateinit var completedOrders: DataFrame<*>
    
    @JdbcDataSource(
        connectionBean = "dataSource",
        tableName = "employees",
        limit = 1000
    )
    lateinit var employeeSample: DataFrame<*>
    
    // === Configuration-driven data sources ===
    
    @CsvDataSource(file = "\${app.data.csv-path}")
    lateinit var configuredCsvData: DataFrame<*>
    
    @JsonDataSource(file = "\${app.data.json-path}")
    lateinit var configuredJsonData: DataFrame<*>

    // === Service methods ===
    
    fun generateSalesReport() {
        println("Sales data loaded with ${salesData.rowsCount()} records")
        println("Product data loaded with ${productData.rowsCount()} products")
    }
    
    fun analyzeUserBehavior() {
        println("User data loaded with ${userData.rowsCount()} users")
        println("Complex data structure: ${complexData.columnsCount()} columns")
    }
    
    fun processAnalytics() {
        println("Analytics data: ${analyticsData.rowsCount()} rows")
        println("Timeseries data: ${timeseriesData.rowsCount()} data points")
    }
    
    fun generateCustomerReport() {
        println("Customer data: ${customerData.rowsCount()} customers")
        println("Completed orders: ${completedOrders.rowsCount()} orders")
        println("Employee sample: ${employeeSample.rowsCount()} employees")
    }
}

/**
 * Configuration class demonstrating Spring Data-style approach
 * with explicit bean definitions for data sources.
 */
@Component
class DataSourceConfig {
    
    // This approach allows for more complex configuration
    // and follows Spring Data repository pattern
    
    fun configureDataSources() {
        // Configuration logic can be added here
        // For example, dynamic data source creation based on profiles
    }
}
