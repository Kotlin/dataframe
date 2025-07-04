# Modules

Kotlin DataFrame is split into specific modules that give you the flexibility
to only use the modules you need.
In this topic you'll learn what these modules are and how to add module dependencies
to an existing Gradle/Maven project.

## Configure the repository

Kotlin DataFrame modules are available from the Maven Central repository.
To use them, add the appropriate dependency into your repositories mapping:

<tabs>
  <tab title="Kotlin DSL"> 

```kotlin
repositories {
    mavenCentral()
}
```

</tab>
  <tab title="Groovy DSL">

```kotlin
repositories {
    mavenCentral()
}
```

</tab>
</tabs>

## General Kotlin DataFrame dependency

If you don't need custom module adjustments, you can easily add a general dependency with  
all [core Kotlin DataFrame modules](#core-kotlin-dataframe-modules) — `dataframe-core` and all officially 
supported IO format modules, excluding [experimental ones](#experimental-kotlindataframe-modules).

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:%dataFrameVersion%'
}
```

</tab>
</tabs>

## Core Kotlin DataFrame modules

Core Kotlin DataFrame modules are `dataframe-core`, which contains all logic 
related to working with DataFrame,  
and optional modules for supporting the most important IO types:

| Module            | Function                                                                                  |
|-------------------|-------------------------------------------------------------------------------------------|
| `dataframe-core`  | The [DataFrame](DataFrame.md) API and its implementation.                                 |
| `dataframe-json`  | Provides support for JSON format writing and reading.                                     |
| `dataframe-csv`   | Provides support for CSV format writing and reading.                                      |
| `dataframe-excel` | Provides support for XSL/XLSX format writing and reading.                                 |
| `dataframe-jdbc`  | Provides support for JDBC data sources writing and reading.                               |
| `dataframe-arrow` | Provides support for [Apache Arrow](https://arrow.apache.org) format writing and reading. |

Add the required Kotlin DataFrame modules to your project's dependencies:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%")
    
    implementation("org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%")
    implementation("org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%'
   
    implementation 'org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%'
    implementation 'org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%'
}
```

</tab>

</tabs>

Note that `dataframe-json` is included with `dataframe-csv` and `dataframe-excel` by default.
This is to support JSON structures inside CSV and Excel files.
If you don't need this functionality, you can exclude it like so:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%") {
        exclude("org.jetbrains.kotlinx", "dataframe-json")
    }
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation('org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%') {
        exclude group: 'org.jetbrains.kotlinx', module: 'dataframe-json'
    }
}
```

</tab>

</tabs>


## Experimental KotlinDataFrame modules

These modules are experimental and may be unstable.

| Module                        | Function                                                                                              |
|-------------------------------|-------------------------------------------------------------------------------------------------------|
| `dataframe-geo`               | Provides new API for working with geospatial data and IO for geographic formats (GeoJSON, Shapefile). |
| `dataframe-openapi`           | Provides support for [OpenAPI JSON format](https://www.openapis.org) reading and writing.             |
| `dataframe-openapi-generator` | Provides [schema generation](schemas.md) from OpenAPI specifications. Requires `dataframe-openapi`.   |


## Kotlin DataFrame plugin

The Kotlin DataFrame compiler plugin enables support for [extension properties](extensionPropertiesApi.md)  
in Gradle projects, allowing you to work with dataframes in a name- and type-safe manner.

See [Compiler Plugin setup](Compiler-Plugin.md#setup) for installation 
and usage in Gradle project instructions.
