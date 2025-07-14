[//]: # (title: Data Schemas)

The Kotlin DataFrame library provides typed data access via 
[generation of extension properties](extensionPropertiesApi.md) for type 
[`DataFrame<T>`](DataFrame.md) (as well as [`DataRow<T>`](DataRow.md)), where
`T` is a marker class that represents `DataSchema` of [`DataFrame`](DataFrame.md).

Schema of [`DataFrame`](DataFrame.md) is a mapping from column names to column types of [`DataFrame`](DataFrame.md).
Data schema can be interpreted as a Kotlin interface or class. If the dataframe is hierarchical -  contains 
[column group](DataColumn.md#columngroup) or [column of dataframes](DataColumn.md#framecolumn), data schema
takes it into account and there is a separate class for each column group or inner `DataFrame`.

For example, consider a simple hierarchical dataframe from
<resource src="example.csv"></resource>.

This dataframe consists of two columns: `name`, which is a `String` column, and `info`,
which is a [**column group**](DataColumn.md#columngroup) containing two nested
[value columns](DataColumn.md#valuecolumn) —
`age` of type `Int`, and `height` of type `Double`.

<table>
  <thead>
    <tr>
      <th>name</th>
      <th colspan="2">info</th>
    </tr>
    <tr>
      <th></th>
      <th>age</th>
      <th>height</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Alice</td>
      <td>23</td>
      <td>175.5</td>
    </tr>
    <tr>
      <td>Bob</td>
      <td>27</td>
      <td>160.2</td>
    </tr>
  </tbody>
</table>

Data schema corresponding to this dataframe can be represented like this :

```kotlin
// Data schema of the "info" column group
@DataSchema
data class Info(
    val age: Int,
    val height: Float
)

// Data schema of the entire dataframe
@DataSchema
data class Person(
    val info: Info,
    val name: String
)
```

[Extension properties](extensionPropertiesApi.md) for the `DataFrame<Person>`
are generated according to this schema and can be used for accessing columns and usage in operations:

```kotlin
// Assuming `df` has type DataFrame<Person>

// Get "age" column from "info" group
df.info.age

// Select "name" and "height" columns
df.select { name and info.height }

// Filter rows by age value
df.filter { age >= 18}
```


## Popular use cases with Data Schemas

Here's a list of the most popular use cases with Data Schemas.

* [**Data Schemas in Gradle projects**](schemasGradle.md) <br/>
  If you are developing a server application and building it with Gradle.

* [**DataSchema workflow in Jupyter**](schemasJupyter.md) <br/>
  If you prefer Notebooks.

* [**Schema inheritance**](schemasInheritance.md) <br/>
  It's worth knowing how to reuse Data Schemas generated earlier.

* [**Custom Data Schemas**](schemasCustom.md) <br/> 
  Sometimes it is necessary to create your own scheme.

* [**Use external Data Schemas in Jupyter**](schemasExternalJupyter.md) <br/>
  Sometimes it is convenient to extract reusable code from Jupyter Notebook into the Kotlin JVM library.
  Schema interfaces should also be extracted if this code uses Custom Data Schemas.

* [**Schema Definitions from SQL Databases in Gradle Project**](schemasImportSqlGradle.md) <br/>
  When you need to take data from the SQL database.

* [**Import OpenAPI 3.0.0 Schemas (Experimental) in Gradle Project**](schemasImportOpenApiGradle.md) <br/>
  When you need to take data from the endpoint with OpenAPI Schema.

* [**Import Data Schemas, e.g. from OpenAPI 3.0.0 (Experimental), in Jupyter**](schemasImportOpenApiJupyter.md) <br/>
  Similar to [importing OpenAPI Data Schemas in Gradle projects](schemasImportOpenApiGradle.md), 
  you can also do this in Jupyter Notebooks.
