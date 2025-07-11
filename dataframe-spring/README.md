# DataFrame Spring Integration

This module provides Spring Framework integration for Kotlin DataFrame, allowing you to define DataFrames as Spring beans and automatically populate them from CSV files using annotations.

## Features

- `@DataSource` annotation for automatic CSV file loading
- Spring BeanPostProcessor for dependency injection style DataFrame initialization
- Support for custom CSV delimiters and headers
- Seamless integration with Spring's dependency injection container

## Usage

### Basic Usage

```kotlin
@Component
class MyDataService {
    @DataSource(csvFile = "data.csv")
    lateinit var df: DataFrame<MyRow>

    fun process() {
        println(df.rowsCount())
    }
}
```

### With Custom Delimiter

```kotlin
@Component
class MyDataService {
    @DataSource(csvFile = "data.tsv", delimiter = '\t')
    lateinit var df: DataFrame<MyRow>
}
```

### Configuration

Make sure to enable component scanning for the DataFrame Spring package:

```kotlin
@Configuration
@ComponentScan(basePackages = ["org.jetbrains.kotlinx.dataframe.spring"])
class AppConfiguration
```

Or register the `DataFramePostProcessor` manually:

```kotlin
@Configuration
class AppConfiguration {
    @Bean
    fun dataFramePostProcessor() = DataFramePostProcessor()
}
```

## Dependencies

This module depends on:
- `org.jetbrains.kotlinx:dataframe-core`
- `org.jetbrains.kotlinx:dataframe-csv`
- `org.springframework:spring-context`
- `org.springframework:spring-beans`

## Annotation Reference

### @DataSource

Annotation to mark DataFrame fields/properties that should be automatically populated with data from a CSV file.

#### Parameters:
- `csvFile: String` - The path to the CSV file to read from
- `delimiter: Char = ','` - The delimiter character to use for CSV parsing (default: ',')
- `header: Boolean = true` - Whether the first row contains column headers (default: true)

#### Example:
```kotlin
@DataSource(csvFile = "users.csv", delimiter = ';', header = true)
lateinit var users: DataFrame<User>
```

## Error Handling

The module provides meaningful error messages for common issues:
- File not found
- Non-DataFrame fields annotated with @DataSource
- CSV parsing errors
- Reflection access errors

All errors are wrapped in `RuntimeException` with descriptive messages including bean names and property names for easier debugging.