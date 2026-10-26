[//]: # (title: API Reference)

<web-summary>
Browse the complete Kotlin DataFrame API reference documentation generated with Dokka across all modules.
</web-summary>

<card-summary>
Explore the comprehensive Dokka-generated API documentation for Kotlin DataFrame, including core types, operations, and IO modules.
</card-summary>

<link-summary>
Access the full Dokka API reference documentation for Kotlin DataFrame classes, functions, and extension properties.
</link-summary>

# API Reference

The Kotlin DataFrame API reference is generated directly from source code and KDocs using Dokka. It contains full
signatures, parameter descriptions, return types, and class hierarchies for all public APIs across all modules.

<tip>
<b><a href="https://kotlin.github.io/dataframe/api-docs/index.html">Open Full Dokka API Reference</a></b>
</tip>

---

## Core Types and Classes

| Type                  | Description                                                             | Dokka Reference                                                                                                                                   |
|-----------------------|-------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `DataFrame<T>`        | The central 2-dimensional table interface representing structured data. | [DataFrame API](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-data-frame/index.html)                          |
| `DataColumn<T>`       | Represents a named column containing values of a specific type.         | [DataColumn API](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-data-column/index.html)                        |
| `DataRow<T>`          | Represents an individual row within a DataFrame.                        | [DataRow API](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe/-data-row/index.html)                              |
| `ColumnsSelectionDsl` | DSL for selecting, filtering, and resolving column sets.                | [ColumnsSelectionDsl API](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/-columns-selection-dsl/index.html) |
| `GroupBy`             | Represents data grouped by specified key columns for aggregation.       | [GroupBy API](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/-group-by/index.html)                          |
| `Pivot`               | Represents pivoted data for multi-dimensional aggregation.              | [Pivot API](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/-pivot/index.html)                               |

---

## Modules

The library is organized modularly. You can browse the API reference for each module:

### Core (`core`)

- **[Browse Core Module API](https://kotlin.github.io/dataframe/api-docs/core/index.html)**
- Core data structures (`DataFrame`, `DataColumn`, `DataRow`, `ColumnGroup`, `FrameColumn`).
- Complete data transformation operations (`filter`, `select`, `update`, `move`, `join`, `groupBy`, `pivot`, etc.).
- Statistical calculations, aggregations, schema manipulation, and type inference.

### I/O, Formats, and Integrations

- **[dataframe-csv](https://kotlin.github.io/dataframe/api-docs/dataframe-csv/index.html)**: Reading and writing CSV and
  TSV formats with automatic header and delimiter detection.
- **[dataframe-json](https://kotlin.github.io/dataframe/api-docs/dataframe-json/index.html)**: Reading and writing JSON
  data with nested hierarchical structures.
- **[dataframe-excel](https://kotlin.github.io/dataframe/api-docs/dataframe-excel/index.html)**: Reading and writing
  Excel workbooks (`.xls`, `.xlsx`).
- **[dataframe-arrow](https://kotlin.github.io/dataframe/api-docs/dataframe-arrow/index.html)**: Interoperability with
  Apache Arrow memory format, Feather, and IPC streams.
- **[dataframe-jdbc](https://kotlin.github.io/dataframe/api-docs/dataframe-jdbc/index.html)**: Reading from and writing
  to SQL databases via JDBC (PostgreSQL, MySQL, MariaDB, MS SQL Server, SQLite, H2, DuckDB).
- **[dataframe-jupyter](https://kotlin.github.io/dataframe/api-docs/dataframe-jupyter/index.html)**: Module for
  Jupyter/Kotlin Notebook and Datalore support.
- **[dataframe-geo](https://kotlin.github.io/dataframe/api-docs/dataframe-geo/index.html)**: Geospatial column types,
  geometries, and GeoJSON support.
- **[dataframe-geo-jupyter](https://kotlin.github.io/dataframe/api-docs/dataframe-geo-jupyter/index.html)**: Interactive
  map rendering for Jupyter Notebook and Datalore.
- **[dataframe-openapi](https://kotlin.github.io/dataframe/api-docs/dataframe-openapi/index.html)** &
  **[dataframe-openapi-generator](https://kotlin.github.io/dataframe/api-docs/dataframe-openapi-generator/index.html)**:
  OpenAPI schema extraction and DataFrame schema generation.
