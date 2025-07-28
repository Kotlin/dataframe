# Apache Arrow

<web-summary>
Read and write Apache Arrow files in Kotlin — efficient binary format support with Kotlin DataFrame.
</web-summary>

<card-summary>
Work with Arrow files in Kotlin for fast I/O — supports both streaming and random access formats.
</card-summary>

<link-summary>
Kotlin DataFrame provides full support for reading and writing Apache Arrow files in high-performance workflows.
</link-summary>


Kotlin DataFrame supports reading from and writing to Apache Arrow files.

Requires the [`dataframe-arrow` module](Modules.md#dataframe-arrow), which is included by 
default in the general [`dataframe`](Modules.md#dataframe-general) artifact 
and in [`%use dataframe`](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe) for Kotlin Notebook.

> Make sure to follow the 
> [Apache Arrow Java compatibility guide](https://arrow.apache.org/docs/java/install.html#java-compatibility) 
> when using Java 9+.
> {style="warning"}

## Read

[`DataFrame`](DataFrame.md) supports both the 
[Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) 
and the [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files).

You can read a `DataFrame` from Apache Arrow data sources 
(via a file path, URL, or stream) using the [`readArrowFeather()`](read.md#read-apache-arrow-formats) method:

```kotlin
val df = DataFrame.readArrowFeather("example.feather")
```

```kotlin
val df = DataFrame.readArrowFeather("https://kotlin.github.io/dataframe/resources/example.feather")
```

## Write

A [`DataFrame`](DataFrame.md) can be written to Arrow format using the interprocess streaming or random access format. 
Output targets include `WritableByteChannel`, `OutputStream`, `File`, or `ByteArray`.

See [](write.md#writing-to-apache-arrow-formats) for more details.
