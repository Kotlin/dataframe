# DataFrame Spring Examples

This directory contains examples demonstrating the usage of the DataFrame Spring integration.

## Overview

The examples show how to use the DataFrame Spring module to automatically load data from various sources into DataFrames using annotations.

## Examples

1. **CSV Data Source** - Demonstrates loading DataFrames from CSV files using the `@CsvDataSource` annotation
2. **CSV Data Source with Spring Context** - Shows how to use the `@CsvDataSource` annotation within a Spring application context

## Running Examples

To run all examples:

```bash
./gradlew :dataframe-spring:runExamples
```

## Example Descriptions

### CSV Data Source

This example demonstrates how to use the `DataFramePostProcessor` to process a bean with `@CsvDataSource` annotations outside of a Spring context.

Key features:
- Loading CSV data with default comma delimiter
- Loading CSV data with custom delimiter (semicolon)
- Accessing the loaded data through a typed DataFrame

### CSV Data Source with Spring Context

This example demonstrates how to use the `DataFramePostProcessor` within a Spring application context to process beans with `@CsvDataSource` annotations.

Key features:
- Registering the `DataFramePostProcessor` in a Spring context
- Automatically processing beans with `@CsvDataSource` annotations
- Retrieving processed beans from the Spring context

## Data Models

The examples use the following data models:

- `CustomerRow` - Represents customer data with id, name, email, and age
- `SalesRow` - Represents sales data with sale ID, customer ID, amount, and date

## File Structure

```
examples/
├── src/                           # Source code for examples
│   └── org/jetbrains/kotlinx/dataframe/spring/examples/
│       ├── CsvDataSourceExample.kt             # Basic CSV example
│       ├── CsvDataSourceWithContextExample.kt  # Spring context example
│       ├── DataModels.kt                       # Data models and utilities
│       └── ExampleRunner.kt                    # Main entry point
└── resources/                     # Resource files for examples
    └── data/                      # Data files
        ├── customers.csv          # Customer data (created at runtime)
        └── sales.csv              # Sales data (created at runtime)
```

## Learning Path

1. Start with the basic CSV example to understand how the `@CsvDataSource` annotation works
2. Move on to the Spring context example to see how to integrate with Spring

## Additional Resources

For more information, see:
- [DataFrame Spring README](../README.md)
- [DataFrame Spring Integration Guide](../INTEGRATION_GUIDE.md)
