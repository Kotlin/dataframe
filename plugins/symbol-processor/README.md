## ~~:plugins:symbol-processor~~

This module holds the KSP plugin, published as the library "symbol-processor-all" that can generate data schemas from 
a data sample using the `@file:ImportDataSchema` annotation. This annotation can be used as an alternative to
the `dataframes {}` syntax of the [Gradle plugin](../dataframe-gradle-plugin) that's declared closer to the source code.
It is also used to detect (both manually written- or generated) `@DataSchema` annotated classes/interfaces to generate
column accessors for in the form of extension properties.

If you use the [Gradle plugin](../dataframe-gradle-plugin), this module and KSP are added as a dependency automatically.

Read more about how to use this at
[Data Schemas in Gradle projects](https://kotlin.github.io/dataframe/schemasgradle.html) and 
[Extension Properties API](https://kotlin.github.io/dataframe/extensionpropertiesapi.html).

### DISABLED!

This plugin is disabled as KSP1 is no longer compatible with Kotlin 2.3+.
See https://kotlin.github.io/dataframe/gradle-plugin.html.
The recommended alternative is to use the [Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html)
and [generating schemas manually from dataframes in runtime](https://kotlin.github.io/dataframe/dataschemagenerationmethods.html).
