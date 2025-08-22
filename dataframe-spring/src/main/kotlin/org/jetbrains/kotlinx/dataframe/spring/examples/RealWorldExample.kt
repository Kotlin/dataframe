package org.jetbrains.kotlinx.dataframe.spring.examples

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.spring.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.sql.DataSource

/**
 * Real-world example of a Spring Data-style analytics service that demonstrates
 * combining multiple data sources for comprehensive data analysis.
 */
@Component
class AnalyticsService {

    // Customer data from CSV export
    @CsvDataSource(file = "analytics/customers.csv", delimiter = ',')
    lateinit var customers: DataFrame<*>
    
    // Order data from JSON API export
    @JsonDataSource(file = "analytics/orders.json")
    lateinit var orders: DataFrame<*>
    
    // Product catalog from Parquet data warehouse
    @ArrowDataSource(file = "analytics/products.parquet")
    lateinit var products: DataFrame<*>
    
    // Real-time metrics from database
    @JdbcDataSource(
        connectionBean = "analyticsDataSource",
        query = """
            SELECT 
                metric_name, 
                metric_value, 
                recorded_at 
            FROM metrics 
            WHERE recorded_at >= CURRENT_DATE - INTERVAL '7 days'
        """
    )
    lateinit var weeklyMetrics: DataFrame<*>
    
    // Geographic data from Feather format
    @ArrowDataSource(file = "analytics/geo_data.feather")
    lateinit var geoData: DataFrame<*>

    fun generateComprehensiveReport() {
        println("=== Comprehensive Analytics Report ===")
        println("Customers: ${customers.rowsCount()} records")
        println("Orders: ${orders.rowsCount()} transactions")
        println("Products: ${products.rowsCount()} items")
        println("Weekly Metrics: ${weeklyMetrics.rowsCount()} data points")
        println("Geographic Regions: ${geoData.rowsCount()} locations")
        
        // Combine data sources for analysis
        // This is where the power of unified DataFrame API shines
        println("\n=== Cross-Data Analysis ===")
        // Implementation would use DataFrame joins, aggregations, etc.
    }
}

/**
 * Configuration demonstrating Spring Data approach with custom data source beans.
 * This follows the Spring Data pattern of explicit configuration alongside annotations.
 */
@Component
class SpringDataConfig {
    
    @Autowired
    lateinit var primaryDataSource: DataSource
    
    // Example of how you might configure specialized data sources
    // following Spring Data patterns
    
    fun configureAnalyticsDataSource(): DataSource {
        // Custom configuration for analytics database
        // This would be a @Bean method in a real @Configuration class
        return primaryDataSource
    }
}

/**
 * Example showing parameter handling with Spring's property resolution.
 * This demonstrates how to handle complex parameter scenarios similar to 
 * Spring Data's approach with repositories.
 */
@Component  
class ConfigurableDataService {
    
    // Parameters can be externalized to properties files
    @CsvDataSource(file = "\${analytics.data.customer-file}")
    lateinit var customers: DataFrame<*>
    
    @JsonDataSource(
        file = "\${analytics.data.order-file}",
        typeClashTactic = JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS
    )
    lateinit var orders: DataFrame<*>
    
    @JdbcDataSource(
        connectionBean = "\${analytics.datasource.bean-name}",
        tableName = "\${analytics.data.table-name}",
        limit = 10000
    )
    lateinit var transactionHistory: DataFrame<*>
}