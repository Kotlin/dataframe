[//]: # (title: Working with Data Schemas)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

The Kotlin Dataframe library provides typed data access via [generation of extension properties](extensionPropertiesApi.md) for
type `DataFrame<T>`, where
`T` is a marker class that represents `DataSchema` of [`DataFrame`](DataFrame.md).

Schema of [`DataFrame`](DataFrame.md) is a mapping from column names to column types of [`DataFrame`](DataFrame.md).
It ignores order of columns in [`DataFrame`](DataFrame.md), but tracks column hierarchy.

In Jupyter environment compile-time [`DataFrame`](DataFrame.md) schema is synchronized with real-time data after every cell execution.

In IDEA projects, you can use the [Gradle plugin](gradle.md#configuration) to extract schema from the dataset
and generate extension properties.
