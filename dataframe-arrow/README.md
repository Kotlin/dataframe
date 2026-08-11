## :dataframe-arrow

This module, published as `dataframe-arrow`, contains all logic and tests for DataFrame to be able to work with 
Apache Arrow.

It supports reading and writing both Apache Arrow IPC formats — the streaming format (`readArrowIPC`/`writeArrowIPC`)
and the random-access/Feather format (`readArrowFeather`/`writeArrowFeather`) — as well as reading
[Apache Parquet](https://parquet.apache.org/) files via `readParquet` (implemented on top of
[Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html)). Note that Parquet is read-only; there is no
Parquet writer.

See [Read Apache Arrow formats](https://kotlin.github.io/dataframe/read.html#read-apache-arrow-formats) and
[Writing to Apache Arrow formats](https://kotlin.github.io/dataframe/write.html#writing-to-apache-arrow-formats)
for more information about how to use it.
