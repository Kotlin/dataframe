## :dataframe-json

This module, published as `dataframe-json`, contains all logic and tests for DataFrame to be able to work with
JSON data sources; [reading](https://kotlin.github.io/dataframe/read.html#read-from-json)
and [writing](https://kotlin.github.io/dataframe/write.html#writing-to-json).
It's based on [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization).

It also contains some logic specific to encoding dataframes as JSON objects with metadata for
the [custom table component in Kotlin Notebook](https://kotlin.github.io/dataframe/usage-with-kotlin-notebook-plugin.html).
See [serialization_format](../docs/serialization_format.md) for more information about the format.

This module is optional but is included by default by the `dataframe` module, `dataframe-jupyter`,
`dataframe-csv`, and `dataframe-excel`.
If you want to use DataFrame without JSON support, you can exclude this module from the dependency.
