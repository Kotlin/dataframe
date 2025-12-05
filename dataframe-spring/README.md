# Kotlin DataFrame Spring Integration

This module provides Spring Framework integration for Kotlin DataFrame, enabling developers to use **dependency injection patterns** for automatic data loading from **multiple file formats and data sources**.

Inspired by Spring Data's approach to data source management, this integration supports CSV, JSON, Arrow/Parquet, and JDBC data sources through declarative annotations.

## 🚀 Features

### Multi-Format Data Source Support
- **@CsvDataSource** - CSV and TSV files with custom delimiters and headers
- **@JsonDataSource** - JSON files with type clash handling and key-value processing
- **@ArrowDataSource** - Arrow/Parquet/Feather files with format auto-detection
- **@JdbcDataSource** - Database tables and custom queries with connection pooling

### Spring Data Inspiration
- **Declarative Configuration**: Data sources specified through annotations
- **Unified API**: Consistent DataFrame initialization across all formats
- **Spring Context Integration**: Leverages Spring's dependency injection lifecycle
- **Bean Reference Support**: Use existing Spring beans for connections
- **Property Placeholder Support**: Externalized configuration through properties

### Advanced Parameter Handling
- **Type-Safe Parameters**: Format-specific parameters with compile-time validation
- **Flexible Configuration**: Support for complex parameter combinations
- **Sensible Defaults**: Minimal configuration required for common use cases
- **Error Handling**: Comprehensive validation with meaningful error messages

## 📋 Quick Start

### Basic Usage

```kotlin
@Component
class MyDataService {
    // CSV data source
    @CsvDataSource(file = "data/sales.csv")
    lateinit var salesData: DataFrame<*>
    
    // JSON data source  
    @JsonDataSource(file = "data/users.json")
    lateinit var userData: DataFrame<*>
    
    // Arrow/Parquet data source
    @ArrowDataSource(file = "data/analytics.parquet")
    lateinit var analyticsData: DataFrame<*>
    
    // JDBC data source
    @JdbcDataSource(
        connectionBean = "dataSource",
        tableName = "customers"
    )
    lateinit var customerData: DataFrame<*>
    
    fun processData() {
        println("Sales: ${salesData.rowsCount()} records")
        println("Users: ${userData.rowsCount()} users")
        println("Analytics: ${analyticsData.rowsCount()} metrics")
        println("Customers: ${customerData.rowsCount()} customers")
    }
}
```

### Configuration

```kotlin
@Configuration
@ComponentScan(basePackages = ["org.jetbrains.kotlinx.dataframe.spring"])
class DataFrameConfiguration {
    
    @Bean
    fun dataSource(): DataSource {
        // Configure your database connection
        return DriverManagerDataSource().apply {
            setDriverClassName("org.h2.Driver")
            url = "jdbc:h2:mem:testdb"
            username = "sa"
            password = ""
        }
    }
}
```

## 📖 Data Source Types

### CSV Data Sources

```kotlin
// Basic CSV
@CsvDataSource(file = "data.csv")
lateinit var basicData: DataFrame<*>

// Custom delimiter (TSV)
@CsvDataSource(file = "data.tsv", delimiter = '\t')
lateinit var tsvData: DataFrame<*>

// No header row
@CsvDataSource(file = "raw_data.csv", header = false)
lateinit var rawData: DataFrame<*>
```

### JSON Data Sources

```kotlin
// Basic JSON
@JsonDataSource(file = "data.json")
lateinit var jsonData: DataFrame<*>

// Handle type clashes
@JsonDataSource(
    file = "complex.json",
    typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS
)
lateinit var complexData: DataFrame<*>

// Key-value path processing
@JsonDataSource(
    file = "nested.json",
    keyValuePaths = ["user.preferences", "config.settings"]
)
lateinit var nestedData: DataFrame<*>
```

### Arrow/Parquet Data Sources

```kotlin
// Auto-detect format from extension
@ArrowDataSource(file = "data.feather")
lateinit var featherData: DataFrame<*>

// Explicit format specification
@ArrowDataSource(file = "data.arrow", format = ArrowFormat.IPC)
lateinit var arrowData: DataFrame<*>

// Nullability handling
@ArrowDataSource(
    file = "large_dataset.parquet",
    nullability = NullabilityOptions.Widening
)
lateinit var parquetData: DataFrame<*>
```

### JDBC Data Sources

```kotlin
// Table access with connection bean
@JdbcDataSource(
    connectionBean = "dataSource",
    tableName = "employees"
)
lateinit var employeeData: DataFrame<*>

// Custom query with limit
@JdbcDataSource(
    connectionBean = "dataSource", 
    query = "SELECT * FROM orders WHERE status = 'COMPLETED'",
    limit = 1000
)
lateinit var recentOrders: DataFrame<*>

// Direct connection parameters
@JdbcDataSource(
    url = "jdbc:h2:mem:testdb",
    username = "sa",
    password = "",
    tableName = "products"
)
lateinit var productData: DataFrame<*>
```

## 🔧 Advanced Configuration

### Property Placeholder Support

```kotlin
@Component
class ConfigurableDataService {
    
    @CsvDataSource(file = "\${app.data.csv-file}")
    lateinit var configuredData: DataFrame<*>
    
    @JdbcDataSource(
        connectionBean = "\${app.datasource.bean-name}",
        tableName = "\${app.data.table-name}"
    )
    lateinit var dbData: DataFrame<*>
}
```

### Application Properties

```properties
# application.properties
app.data.csv-file=data/production-data.csv
app.datasource.bean-name=productionDataSource
app.data.table-name=user_metrics
```

### Real-World Analytics Example

```kotlin
@Component
class AnalyticsService {
    
    // Customer data from CSV export
    @CsvDataSource(file = "exports/customers.csv")
    lateinit var customers: DataFrame<*>
    
    // Event data from JSON logs
    @JsonDataSource(file = "logs/events.json")
    lateinit var events: DataFrame<*>
    
    // ML features from Parquet
    @ArrowDataSource(file = "ml/features.parquet")
    lateinit var features: DataFrame<*>
    
    // Real-time metrics from database
    @JdbcDataSource(
        connectionBean = "metricsDataSource",
        query = """
            SELECT metric_name, value, timestamp 
            FROM metrics 
            WHERE timestamp >= NOW() - INTERVAL '1 hour'
        """
    )
    lateinit var realtimeMetrics: DataFrame<*>
    
    fun generateReport() {
        // Combine all data sources using DataFrame API
        println("Customer segments: ${customers.rowsCount()}")
        println("Recent events: ${events.rowsCount()}")
        println("ML features: ${features.rowsCount()}")
        println("Live metrics: ${realtimeMetrics.rowsCount()}")
    }
}
```

## 🏗️ Architecture

### Spring Data-Inspired Design

The module follows **Spring Data patterns** for consistent and familiar developer experience:

1. **Declarative Annotations**: Similar to `@Query` in Spring Data JPA
2. **Bean Integration**: Leverages existing Spring infrastructure
3. **Type Safety**: Compile-time validation of configuration
4. **Extensible Design**: Easy to add new data source types
5. **Error Handling**: Meaningful error messages with context

### Processing Pipeline

1. **Bean Post-Processing**: DataFramePostProcessor scans for annotations
2. **Strategy Pattern**: Format-specific processors handle different data sources
3. **Context Integration**: Access to Spring ApplicationContext for bean resolution
4. **Error Recovery**: Comprehensive error handling and reporting

### Supported File Extensions

- **CSV**: `.csv`, `.tsv`
- **JSON**: `.json`
- **Arrow**: `.arrow` (IPC format)
- **Feather**: `.feather`
- **Parquet**: `.parquet`

## 🛠️ Setup Instructions

### Gradle

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-spring:$dataframe_version")
}
```

### Maven

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>dataframe-spring</artifactId>
    <version>${dataframe.version}</version>
</dependency>
```

### Spring Boot Auto-Configuration

The module is automatically configured when present on the classpath.

## 🔍 Migration Guide

### From Manual Loading

**Before:**
```kotlin
@Component
class DataService {
    
    fun loadData() {
        val csvData = DataFrame.readCsv("data.csv")
        val jsonData = DataFrame.readJson("data.json") 
        // Process data...
    }
}
```

**After:**
```kotlin
@Component 
class DataService {
    
    @CsvDataSource(file = "data.csv")
    lateinit var csvData: DataFrame<*>
    
    @JsonDataSource(file = "data.json")
    lateinit var jsonData: DataFrame<*>
    
    fun processData() {
        // Data automatically loaded and ready to use
    }
}
```

## 🐛 Troubleshooting

### Common Issues

**File Not Found**
```
CSV file not found: /path/to/missing.csv
```
- Verify file path and existence
- Check working directory
- Ensure proper file permissions

**Connection Bean Not Found**
```
Bean 'dataSource' is not a Connection or DataSource
```
- Verify bean name in @JdbcDataSource
- Ensure bean implements javax.sql.DataSource or java.sql.Connection
- Check Spring configuration

**Type Clash in JSON**
```
JSON type clash detected
```
- Use appropriate typeClashTactic
- Consider restructuring JSON data
- Use ANY_COLUMNS for mixed types

### Debug Mode

Enable debug logging:
```properties
logging.level.org.jetbrains.kotlinx.dataframe.spring=DEBUG
```

## 🤝 Contributing

This module demonstrates the power of combining Spring's dependency injection with DataFrame's unified data processing API. The Spring Data-inspired approach provides a consistent, declarative way to handle multiple data sources while maintaining the flexibility and power of the DataFrame API.

For more examples and advanced usage patterns, see the `examples/` directory in the module.
