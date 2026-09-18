## :dataframe-csv

This module, published as `dataframe-csv`, contains all logic and tests for DataFrame to be able to work with `csv`
files.

This is the modern CSV/TSV integration, available since DataFrame v0.15 and based on
[Deephaven CSV](https://github.com/deephaven/deephaven-csv) for reading and
[Apache Commons CSV](https://commons.apache.org/proper/commons-csv/) for writing. The older csv/tsv code left in
[:core](../core) is deprecated in favor of it.

The `dataframe` artifact re-exports this module, so `org.jetbrains.kotlinx:dataframe` already includes it. Depend
on `org.jetbrains.kotlinx:dataframe-csv` explicitly only if you use `dataframe-core` on its own.

See [Read from CSV](https://kotlin.github.io/dataframe/read.html#read-from-csv) and
[Writing to CSV](https://kotlin.github.io/dataframe/write.html#writing-to-csv)
for more information about how to use it.
