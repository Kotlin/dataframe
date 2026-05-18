# Migration from Gradle/KSP Plugin

Gradle and KSP plugins were useful tools in earlier versions of Kotlin DataFrame.  
However, they are now **deprecated**.
Their latest release is 1.0.0-Beta4 and will not have future releases.
This page provides migration guidance.

Our plans for the next iteration of schema generation from source feature can be found in 
[Issue #1844](https://github.com/Kotlin/dataframe/issues/1844).

## Gradle Plugin

> Do not confuse this with the [compiler plugin](Compiler-Plugin.md), which is a Kotlin compiler plugin
> and has a different plugin ID.  
> {style="note"}

1. **Generation of [data schemas](schemas.md)** from data sources  
   (files, databases, or external URLs).
    - You could copy already generated schemas from `build/generate` into your project sources.
    - To generate a `DataSchema` for a [`DataFrame`](DataFrame.md) now, use
      the [`generate..()` methods](DataSchemaGenerationMethods.md).

2. **Generation of [extension properties](extensionPropertiesApi.md)** from data schemas  
   This is now handled by the [compiler plugin](Compiler-Plugin.md), which:
    - Generates extension properties for declared data schemas.
    - Automatically updates the schema and regenerates properties after structural DataFrame operations.


## KSP Plugin

- **Generation of [data schemas](schemas.md)** from data sources  
  (files, databases, or external URLs).
    - You could copy already generated schemas from `build/generate/ksp` into your project sources.
    - To generate a `DataSchema` for a [`DataFrame`](DataFrame.md) now, use the  
      [`generate..()` methods](DataSchemaGenerationMethods.md) instead.
