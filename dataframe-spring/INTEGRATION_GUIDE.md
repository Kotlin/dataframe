# DataFrame Spring Integration Guide

## Quick Start

### 1. Add Dependency

Add the DataFrame Spring module to your project:

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-spring:${dataframeVersion}")
}
```

### 2. Enable Component Scanning

```kotlin
@Configuration
@ComponentScan(basePackages = ["org.jetbrains.kotlinx.dataframe.spring"])
class AppConfiguration
```

### 3. Use @CsvDataSource Annotation

```kotlin
@Component
class CustomerService {
    @CsvDataSource(file = "customers.csv")
    lateinit var customers: DataFrame<CustomerRow>
    
    @CsvDataSource(file = "orders.csv", delimiter = ';')
    lateinit var orders: DataFrame<OrderRow>
    
    fun analyzeCustomers() {
        println("Total customers: ${customers.rowsCount()}")
        // Access data using DataFrame API
    }
}
```

### 4. Define Your Data Schema

```kotlin
@DataSchema
interface CustomerRow {
    val id: Int
    val name: String
    val email: String
    val registrationDate: String
}
```

## Advanced Configuration

### Manual Bean Registration

If you prefer manual configuration:

```kotlin
@Configuration
class DataFrameConfig {
    @Bean
    fun dataFramePostProcessor() = DataFramePostProcessor()
}
```

### Custom File Locations

Use Spring's property placeholders:

```kotlin
@CsvDataSource(file = "${app.data.customers.file}")
lateinit var customers: DataFrame<CustomerRow>
```

### Error Handling

The post-processor provides detailed error messages:

```
// File not found
RuntimeException: Failed to process @CsvDataSource annotations for bean 'customerService'
Caused by: IllegalArgumentException: CSV file not found: /path/to/customers.csv

// Wrong property type
IllegalArgumentException: Property 'data' is annotated with @CsvDataSource but is not a DataFrame type

// CSV parsing error
RuntimeException: Failed to read CSV file 'customers.csv' for property 'customers'
```

## Best Practices

1. **Use meaningful file paths**: Place CSV files in `src/main/resources/data/`
2. **Define data schemas**: Use `@DataSchema` for type safety
3. **Handle initialization**: Use `lateinit var` for DataFrame properties
4. **Validate data**: Add business logic validation after initialization
5. **Resource management**: CSV files are loaded once during bean initialization

## Troubleshooting

### Common Issues

1. **ClassNotFoundException**: Ensure Spring dependencies are available
2. **FileNotFoundException**: Check CSV file paths are correct
3. **PropertyAccessException**: Ensure DataFrame properties are `lateinit var`
4. **NoSuchBeanDefinitionException**: Enable component scanning or register manually

### Debug Tips

- Enable Spring debug logging: `logging.level.org.springframework=DEBUG`
- Check bean post-processor registration: Look for `DataFramePostProcessor` in logs
- Verify CSV file locations: Use absolute paths for testing

## Integration with Spring Boot

```kotlin
@SpringBootApplication
@ComponentScan(basePackages = ["your.package", "org.jetbrains.kotlinx.dataframe.spring"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

## Testing

```kotlin
@SpringBootTest
class DataFrameServiceTest {
    @Autowired
    private lateinit var customerService: CustomerService
    
    @Test
    fun `should load customer data`() {
        assertTrue(customerService.customers.rowsCount() > 0)
    }
}
```
