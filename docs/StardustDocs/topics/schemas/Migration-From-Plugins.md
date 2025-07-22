# Migration from Gradle/KSP Plugin

Gradle and KSP plugins were useful tools in earlier versions of Kotlin DataFrame.  
However, they are now being phased out. This section provides an overview of their current state and migration guidance.

## Gradle Plugin

> Do not confuse this with the [](Compiler-Plugin.md), which is a Kotlin compiler plugin
> and has a different plugin ID.  
> {style="note"}

1. **Generation of [data schemas](schemas.md)** from data sources  
   (files, databases, or external URLs).
    - You could copy already generated schemas from `build/generate` into your project sources.
    - To generate a `DataSchema` for a [`DataFrame`](DataFrame.md) now, use
      the [`generate..()` methods](DataSchemaGenerationMethods.md).

2. **Generation of [extension properties](extensionPropertiesApi.md)** from data schemas  
   This is now handled by the [](Compiler-Plugin.md), which:
    - Generates extension properties for declared data schemas.
    - Automatically updates the schema and regenerates properties after structural DataFrame operations.

> The Gradle plugin still works and may be helpful for generating schemas from data sources.  
> However, it is planned for deprecation, and **we do not recommend using it going forward**.  
> {style="warning"}

If you still choose to use it, make sure to disable the automatic KSP dependency 
to avoid compatibility issues with Kotlin 2.1+ by adding this line to `gradle.properties`:

```properties
kotlin.dataframe.add.ksp=false
```

## KSP Plugin

> The KSP plugin is **not compatible with Kotlin 2.1 or newer**.  
> It is planned for deprecation or major changes, and **we do not recommend using it at this time**.  
> {style="warning"}

- **Generation of [data schemas](schemas.md)** from data sources  
  (files, databases, or external URLs).
    - You could copy already generated schemas from `build/generate/ksp` into your project sources.
    - To generate a `DataSchema` for a [`DataFrame`](DataFrame.md) now, use the  
      [`generate..()` methods](DataSchemaGenerationMethods.md) instead.
