## :plugins

This folder holds all our Gradle- and Kotlin Compiler plugins:

### [~~:plugins:dataframe-gradle-plugin~~](./dataframe-gradle-plugin)
The Gradle plugin for DataFrame that can generate data schemas from a data sample using the simple
task `dataframes {}`. It uses [:plugins:symbol-processor](./symbol-processor) to generate column accessors.

NOTE: This plugin is disabled as KSP1 is no longer compatible with Kotlin 2.3+.

### [~~:plugins:symbol-processor~~](./symbol-processor)
The KSP plugin that can generate data schemas from a data sample using the `@file:ImportDataSchema` annotation.
It is also used to generate column accessors for in the form of extension properties for 
(both manually written- or generated) `@DataSchema` annotated classes/interfaces.

NOTE: This plugin is disabled as KSP1 is no longer compatible with Kotlin 2.3+.

### [~~:plugins:kotlin-dataframe~~](./kotlin-dataframe)
The Kotlin 2.x Compiler plugin of DataFrame.
A plugin for your Kotlin project that can generate on-the-fly column accessors for the compiler and IDE even without
having to provide data schemas!

NOTE: Development of this module was moved to the Kotlin repository:
https://github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe

### [:plugins:expressions-converter](./expressions-converter)
A small Kotlin Compiler plugin that provides intermediate expressions of DataFrame
operation chains, used internally by [:core](../core) to generate "explainer dataframes" on the documentation website.

### [:plugins:keywords-generator](./keywords-generator) 
A small Gradle plugin that is used internally to generate enums with restricted Kotlin keywords for the 
[:core](../core) module.
