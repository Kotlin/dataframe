[//]: # (title: Create)
<show-structure depth="3"/>
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

There are several ways to create [`DataFrame`](DataFrame.md) objects from data that is already loaded into memory:
* [create columns with data](createColumn.md) and then [bundle them](createDataFrame.md) into a [`DataFrame`](DataFrame.md)
* create and initialize [`DataFrame`](DataFrame.md) directly from values using `vararg` variants of the [corresponding functions](createDataFrame.md).
* [convert Kotlin objects](createDataFrame.md#todataframe) into [`DataFrame`](DataFrame.md) 

**API reference**: [`api` package](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/index.html)

To learn how to read dataframes from files and URLs, go to the [next section](read.md).
