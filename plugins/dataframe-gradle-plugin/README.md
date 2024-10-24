## :plugins:dataframe-gradle-plugin

This module holds the Gradle plugin for DataFrame, published as "org.jetbrains.kotlinx.dataframe" on the
[Gradle Plugin Portal](https://plugins.gradle.org/plugin/org.jetbrains.kotlin.plugin.dataframe).

This plugin can let the user generate data schemas from a data sample using the simple
Gradle task `dataframes {}`. It also provides an automatic dependency on [:plugins:symbol-processor](./symbol-processor)
to generate column accessors and support the `@file:ImportDataSchema` notation.

Read more about how to use the Gradle plugin at
[Data Schemas in Gradle projects](https://kotlin.github.io/dataframe/schemasgradle.html).
