## :plugins

This folder holds all our Gradle- and Kotlin Compiler plugins:

### [:plugins:dataframe-gradle-plugin](./dataframe-gradle-plugin)
The Gradle plugin for DataFrame that can generate data schemas from a data sample using the simple
task `dataframes {}`. It uses [:plugins:symbol-processor](./symbol-processor) to generate column accessors.

### [:plugins:symbol-processor](./symbol-processor)
The KSP plugin that can generate data schemas from a data sample using the `@file:ImportDataSchema` annotation.
It is also used to detect (both manually written- or generated) `@DataSchema` annotated classes/interfaces to generate
column accessors for in the form of extension properties.

### [:plugins:kotlin-dataframe](./kotlin-dataframe)
The Kotlin 2.x Compiler plugin of DataFrame. A [work-in-progress](https://github.com/Kotlin/dataframe/issues/704)
plugin for your Gradle project that can generate on-the-fly column accessors for the compiler and IDE even without
having to provide data schemas!

### [:plugins:expressions-converter](./expressions-converter)
A small Kotlin Compiler plugin that provides intermediate expressions of DataFrame
operation chains, used internally by [:core](../core) to generate "explainer dataframes" on the documentation website.

### [generator](../generator) 
A small Gradle plugin that is used internally to generate enums with restricted Kotlin keywords for the 
[:core](../core) module.

This module can probably be moved under [:plugins](../plugins):
[Issue #899](https://github.com/Kotlin/dataframe/issues/899).
