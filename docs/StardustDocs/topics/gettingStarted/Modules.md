# Modules

Kotlin DataFrame is composed of modules, allowing you to include only the functionality you need.
In addition, Kotlin DataFrame provides several [plugins](#plugins) 
that significantly enhance the development experience 
— making it more convenient, powerful, and enjoyable to work with.

<table>
  <thead>
    <tr>
      <th>Module</th>
      <th>Function</th>
    </tr>
  </thead>
  <tbody>
    <tr><td colspan="2"><strong><a href="#dataframe-general">General</a></strong></td></tr>
    <tr>
      <td><a href="#dataframe-general"><code>dataframe</code></a></td>
      <td>General artifact – combines all core and IO artifacts except experimental ones.</td>
    </tr>
    <tr>
        <td colspan="2"><strong><a href="#core-modules">Core</a></strong></td></tr>
    <tr>
      <td><code><a href="#dataframe-core">dataframe-core</a></code></td>
      <td>The <a href="DataFrame.md">DataFrame</a> API and its implementation.</td>
    </tr>
<tr><td colspan="2"><strong><a href="#io-modules">IO</a></strong></td></tr>
    <tr>
      <td><code><a href="#dataframe-json">dataframe-json</a></code></td>
      <td>Provides support for JSON format writing and reading.</td>
    </tr>
    <tr>
      <td><code><a href="#dataframe-csv">dataframe-csv</a></code></td>
      <td>Provides support for CSV format writing and reading.</td>
    </tr>
    <tr>
      <td><code><a href="#dataframe-excel">dataframe-excel</a></code></td>
      <td>Provides support for XSL/XLSX format writing and reading.</td>
    </tr>
    <tr>
      <td><code><a href="#dataframe-jdbc">dataframe-jdbc</a></code></td>
      <td>Provides support for JDBC data sources reading.</td>
    </tr>
    <tr>
      <td><code><a href="#dataframe-arrow">dataframe-arrow</a></code></td>
      <td>Provides support for <a href="https://arrow.apache.org">Apache Arrow</a> format writing and reading.</td>
    </tr>
    <tr><td colspan="2"><strong><a href="#experimental-modules">Experimental modules</a></strong></td></tr>
    <tr>
      <td><code><a href="#dataframe-geo">dataframe-geo</a></code></td>
      <td>Provides a new API for working with geospatial data and IO for geographic formats (GeoJSON, Shapefile).</td>
    </tr>
    <tr>
      <td><code><a href="#dataframe-openapi">dataframe-openapi</a></code></td>
      <td>Provides support for <a href="https://www.openapis.org">OpenAPI JSON format</a> reading and writing.</td>
    </tr>
    <tr>
      <td><code><a href="#dataframe-openapi-generator">dataframe-openapi-generator</a></code></td>
      <td>Provides <a href="schemas.md">schema generation</a> from OpenAPI specifications. Requires <code>dataframe-openapi</code>.</td>
    </tr>
<tr><td colspan="2"><strong><a href="#plugins">Plugins</a></strong></td></tr>
 <tr>
      <td><code><a href="#kotlin.plugin.dataframe">kotlin.plugin.dataframe</a></code></td>
      <td>Kotlin compiler plugin. Provides compile-time <a href="extensionPropertiesApi.md">extension properties</a> generation.
</td>
    </tr>
    <tr>
      <td><code><a href="#kotlinx.dataframe">kotlinx.dataframe</a></code></td>
      <td>Gradle plugin. Provides <a href="schemas.md">schemas generation</a> using Gradle.</td>
    </tr>
    <tr>
      <td><code><a href="#ksp-plugin">kotlinx.dataframe:symbol-processor-all</a></code></td>
      <td>KSP plugin. Provides <a href="schemas.md">schemas generation</a> using KSP.</td>
    </tr>
  </tbody>
</table>


## Configure the repository

All Kotlin DataFrame modules are available from the Maven Central repository.
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

## `dataframe` - general Kotlin DataFrame dependency {id="dataframe-general"}

General-purpose artifact that includes all [core](#core-modules) and [IO](#io-modules) modules.  
Does **not** include any [experimental modules](#experimental-modules).  
Recommended if you don’t need fine-grained control over individual module dependencies.

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

## Core Kotlin DataFrame modules {id="core-modules"}

#### `dataframe-core`

The core [DataFrame](DataFrame.md) API and its implementation.  
Includes all core functionality for working with data structures, expressions, schema management, and operations.

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-core:%dataFrameVersion%'
}
```

</tab>
</tabs>

## IO Kotlin DataFrame modules {id="io-modules"}

#### `dataframe-json` {id="dataframe-json"}

Provides all logic for DataFrame to be able to work with
JSON data sources; [reading](https://kotlin.github.io/dataframe/read.html#read-from-json)
and [writing](https://kotlin.github.io/dataframe/write.html#writing-to-json).
It's based on [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization).

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-json:%dataFrameVersion%'
}
```

</tab>
</tabs>

#### `dataframe-csv` {id="dataframe-csv"}

Provides support for reading and writing CSV files.  
Supports standard CSV format features such as delimiters, headers, and quotes.

Based on high-performance [Deephaven CSV](https://github.com/deephaven/deephaven-csv).

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-csv:%dataFrameVersion%'
}
```

</tab>
</tabs>

Note that `dataframe-json` is included with `dataframe-csv` by default.
This is to support JSON structures inside CSV files.
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

#### `dataframe-excel` {id="dataframe-excel"}

Provides support for reading and writing Excel files (`.xls` and `.xlsx`).  
Compatible with standard spreadsheet editors and supports embedded structured data.

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%'
}
```

</tab>
</tabs>

Note that `dataframe-json` is included with `dataframe-excel` by default.
This is to support JSON structures inside Excel files.
If you don't need this functionality, you can exclude it like so:

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%") {
        exclude("org.jetbrains.kotlinx", "dataframe-json")
    }
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation('org.jetbrains.kotlinx:dataframe-excel:%dataFrameVersion%') {
        exclude group: 'org.jetbrains.kotlinx', module: 'dataframe-json'
    }
}
```

</tab>
</tabs>

#### `dataframe-jdbc` {id="dataframe-jdbc"}

Provides all logic for DataFrame to be able to work with  
SQL databases that implement the JDBC protocol.

See [Read from SQL databases](https://kotlin.github.io/dataframe/readsqldatabases.html) for more information
about how to use it.

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-jdbc:%dataFrameVersion%'
}
```

</tab>
</tabs>

#### `dataframe-arrow` {id="dataframe-arrow"}

Provides all logic and tests for DataFrame to be able to work with
[Apache Arrow](https://arrow.apache.org).

See [Read Apache Arrow formats](https://kotlin.github.io/dataframe/read.html#read-apache-arrow-formats) and
[Writing to Apache Arrow formats](https://kotlin.github.io/dataframe/write.html#writing-to-apache-arrow-formats)
for more information about how to use it.

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-arrow:%dataFrameVersion%'
}
```

</tab>
</tabs>


## Experimental Kotlin DataFrame modules {id="experimental-modules"}

These modules are experimental and may be unstable.

#### `dataframe-geo`

Provides a new API for working with geospatial data, 
including reading and writing geospatial formats (GeoJSON, Shapefile), 
and performing geometry-aware operations.

See [Geo guide](https://kotlin.github.io/kandy/geo-plotting-guide.html) for more details and examples.

Requires [OSGeo Repository](https://repo.osgeo.org).

<tabs>
<tab title="Kotlin DSL">

```kotlin
repositories {
    maven("https://repo.osgeo.org/repository/release")
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-geo:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
repositories {
    maven {
        url 'https://repo.osgeo.org/repository/release'
    }
}

dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-geo:%dataFrameVersion%'
}
```

</tab>
</tabs>

#### `dataframe-openapi`

Provides functionality to support auto-generated data schemas from OpenAPI 3.0.0 specifications.  
This module is a companion to [`dataframe-openapi-generator`](#dataframe-openapi-generator):

- `dataframe-openapi-generator` is used internally by the Gradle plugin and Jupyter integration
  to generate data schemas from OpenAPI specs.  
  In the Gradle plugin, it powers the `dataschemas {}` DSL and the `@file:ImportDataSchema()` annotation.  
  In Jupyter, it enables the `importDataSchema()` function.

- `dataframe-openapi` must be added as a dependency to the user project in order to use those generated data schemas.

See:
- [Import OpenAPI Schemas in Gradle project](https://kotlin.github.io/dataframe/schemasimportopenapigradle.html)
- [Import Data Schemas, e.g. from OpenAPI, in Jupyter](https://kotlin.github.io/dataframe/schemasimportopenapijupyter.html)

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-openapi:%dataFrameVersion%'
}
```

</tab>
</tabs>

#### `dataframe-openapi-generator`

Provides the logic and tooling necessary to import OpenAPI 3.0.0 specifications  
as auto-generated data schemas for Kotlin DataFrame.  
This module works in conjunction with [`dataframe-openapi`](#dataframe-openapi):

- `dataframe-openapi-generator` is used internally by the Gradle plugin and Jupyter integration  
  to generate data schemas from OpenAPI specifications.
    - In Gradle, it enables the `dataschemas {}` DSL and the `@file:ImportDataSchema()` annotation.
    - In Jupyter, it powers the `importDataSchema()` function.

- `dataframe-openapi` must be added as a dependency to the user project to actually use the generated schemas.

See:
- [Import OpenAPI Schemas in Gradle project](https://kotlin.github.io/dataframe/schemasimportopenapigradle.html)
- [Import Data Schemas, e.g. from OpenAPI, in Jupyter](https://kotlin.github.io/dataframe/schemasimportopenapijupyter.html)

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe-openapi-generator:%dataFrameVersion%")
}
```

</tab>

<tab title="Groovy DSL">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe-openapi-generator:%dataFrameVersion%'
}
```

</tab>
</tabs>

## Plugins

<!-- TODO improve compiler plugin setup after release--->

#### `kotlin.plugin.dataframe` — Kotlin DataFrame Compiler Plugin {id="kotlin.plugin.dataframe"}

The Kotlin DataFrame compiler plugin enables support for [extension properties](extensionPropertiesApi.md)  
in Gradle projects, allowing you to work with dataframes in a name- and type-safe manner.

See the [Compiler Plugin setup guide](Compiler-Plugin.md#setup) for installation  
and usage instructions for Gradle projects.

Published as a Kotlin official plugin.
[Source code is available in the Kotlin repository](https://github.com/JetBrains/kotlin/tree/master/plugins/kotlin-dataframe).

#### `kotlinx.dataframe` – Gradle Plugin {id="kotlinx.dataframe"}

> The current Gradle plugin is **under consideration for deprecation** and may be officially marked as deprecated in future releases.
>
> At the moment, **[data schema generation is handled via dedicated methods](DataSchemaGenerationMethods.md)** instead of relying on the plugin.
{style="warning"}

The Gradle plugin allows generating [data schemas](schemas.md) from samples of data  
(of supported formats) like JSON, CSV, Excel files, or URLs, as well as from data fetched from SQL databases  
using Gradle.

See the [Gradle Plugin Reference](Gradle-Plugin.md) for installation  
and usage instructions in Gradle projects.

> By default, the Gradle plugin also applies the [KSP plugin](#ksp-plugin).


<tabs>
<tab title="Kotlin DSL">

```kotlin
plugins {
    id("org.jetbrains.kotlinx.dataframe") version "%dataFrameVersion%"
}
```
</tab> 

<tab title="Groovy DSL">

```groovy
plugins {
    id 'org.jetbrains.kotlinx.dataframe' version '%dataFrameVersion%'
}
```
</tab>
</tabs> 


#### `kotlinx.dataframe:symbol-processor-all` – KSP Plugin {id="ksp-plugin"}

> The KSP plugin is **not compatible with [KSP2](https://github.com/google/ksp?tab=readme-ov-file#ksp2-is-here)**
> and may **not work properly with Kotlin 2.1 or newer**.
> 
> At the moment, **[data schema generation is handled via dedicated methods](DataSchemaGenerationMethods.md)** instead of relying on the plugin.
{style="warning"}

The Gradle plugin allows generating [data schemas](schemas.md) from samples of data  
(of supported formats) like JSON, CSV, Excel files, or URLs, as well as from data fetched from SQL databases  
using Kotlin Symbol Processing (KSP).
This is useful for projects where you prefer or require schema generation at the source level.

See [Data Schemas in Gradle Projects](schemasGradle.md) for usage details.

<tabs>
<tab title="Kotlin DSL">

```kotlin
dependencies {
    ksp("org.jetbrains.kotlinx.dataframe:symbol-processor-all:%dataFrameVersion%")
}
```
</tab> 

<tab title="Groovy DSL">

```groovy
dependencies {
    ksp 'org.jetbrains.kotlinx.dataframe:symbol-processor-all:%dataFrameVersion%'
}
```
</tab>
</tabs> 
