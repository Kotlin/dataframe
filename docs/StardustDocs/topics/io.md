[//]: # (title: Input/output)

When you work with data, you have to [read](read.md) it from disk or from remote URLs and [write](write.md) it on disk.
This section describes how to do it. For now, CSV, TSV, JSON, XLS, XLSX, Apache Arrow formats are supported.

Working with [Apache Spark](https://spark.apache.org/) directly from Kotlin DataFrame is not possible at the time of 
writing. However, there is a [Kotlin Spark API](https://github.com/Kotlin/kotlin-spark-api) which we can recommend in
the meantime. If you do want to work with data from Spark in Kotlin DataFrame, we recommend exporting your data to CSV or
the Apache Arrow format and then reading it back into a [DataFrame](DataFrame.md).
