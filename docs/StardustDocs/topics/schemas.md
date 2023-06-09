[//]: # (title: Working with Data Schemas)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

The Kotlin Dataframe library provides typed data access via [generation of extension properties](extensionPropertiesApi.md) for
a type `DataFrame<T>`, where
`T` is a marker class that represents `DataSchema` of [`DataFrame`](DataFrame.md).

Schema of [`DataFrame`](DataFrame.md) is a mapping from column names to column types of [`DataFrame`](DataFrame.md).
It ignores the order of columns in [`DataFrame`](DataFrame.md), but tracks column hierarchy.

In the Jupyter environment, compile-time [`DataFrame`](DataFrame.md)
schema is synchronized with real-time data after every cell execution.

In IDEA projects, you can use the [Gradle plugin](schemasGradle.md#configuration) to extract schema from the dataset
and generate extension properties.


## Popular use cases for Data Schemas

Here's a list of the most popular use cases for Data Schemas.

* [**Data Schemas in Gradle projects**](schemasGradle.md)<br/>
  If you are developing a server application and building it with Gradle.

* [**DataSchema workflow in Jupyter**](schemasJupyter.md)<br/>
  If you prefer Notebooks.

* [**Schema inheritance**](schemasInheritance.md)<br/>
  It's worth knowing how to reuse Data Schemas generated earlier.

* [**Custom Data Schemas**](schemasCustom.md)<br/>
  Sometimes it is necessary to create your own schema.

* [**Use external Data Schemas in Jupyter Notebook**](schemasExternalJupyter.md)<br/>
  Sometimes it is convenient to extract reusable code from Jupyter Notebook into the Kotlin JVM library.
  Schema interfaces should also be extracted if this code uses custom Data Schemas.

* [**Import OpenAPI Schemas in a Gradle project**](schemasImportOpenApiGradle.md)<br/>
  When you are using a JSON API that provides an OpenAPI schema.

* [**Import Data Schemas, e.g., from OpenAPI, in Jupyter**](schemasImportOpenApiJupyter.md)<br/>
  Similar to [importing OpenAPI Data Schemas in Gradle projects](schemasImportOpenApiGradle.md),
  you can also do this in Jupyter Notebooks.
