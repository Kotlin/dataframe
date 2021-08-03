## DataFrame Gradle integration and annotation processing

DataFrame Gradle plugin can generate type-safe accessors for your data for interfaces annotated with `@DataSchema` - **data schemas**, and infer data schemas from your data.

### Setup
#### Groovy DSL 
```groovy
plugins {
    id("org.jetbrains.dataframe.schema-generator") version "1.0-SNAPSHOT"
}

sourceSets {
    main.kotlin.srcDir("build/generated/ksp/main/kotlin/")
}
```
#### Kotlin DSL
```kotlin
plugins {
    id("org.jetbrains.dataframe.schema-generator") version "1.0-SNAPSHOT"
}

kotlin.sourceSets.getByName("main").kotlin.srcDir("build/generated/ksp/main/kotlin/")
```

### Gradle integration
For the following configuration, file `GeneratedRawCityPopulation` will be generated. See [reference](#reference) and [examples](#examples) for more details.
```kotlin
dataframes {
    schema {
        data = "https://datalore-samples.s3-eu-west-1.amazonaws.com/datalore_gallery_of_samples/city_population.csv"
        name = "org.example.RawCityPopulation"
    }
}
```

#### GeneratedRawCityPopulation.kt
```kotlin
package org.example

@DataSchema(isOpen = false)
interface RawCityPopulation{
    @ColumnName("City / Urban area")
    val `City - Urban area`: String
    val Country: String
    val Density: String
    @ColumnName("Land area")
    val `Land area`: String
    val Population: String
}
```
### Annotation processing
Each of the data schemas is populated with extension properties.

```kotlin
@file:Suppress("UNCHECKED_CAST")
package org.example

val DataFrameBase<CityPopulation>.`City - Urban area`: DataColumn<String> get() = this["City / Urban area"] as DataColumn<String>
val DataRowBase<CityPopulation>.`City - Urban area`: String get() = this["City / Urban area"] as String
val DataFrameBase<CityPopulation>.`Country`: DataColumn<String> get() = this["Country"] as DataColumn<String>
val DataRowBase<CityPopulation>.`Country`: String get() = this["Country"] as String
val DataFrameBase<CityPopulation>.`Density`: DataColumn<Int> get() = this["Density"] as DataColumn<Int>
val DataRowBase<CityPopulation>.`Density`: Int get() = this["Density"] as Int
val DataFrameBase<CityPopulation>.`Land area`: DataColumn<Int> get() = this["Land area"] as DataColumn<Int>
val DataRowBase<CityPopulation>.`Land area`: Int get() = this["Land area"] as Int
val DataFrameBase<CityPopulation>.`Population`: DataColumn<Int> get() = this["Population"] as DataColumn<Int>
val DataRowBase<CityPopulation>.`Population`: Int get() = this["Population"] as Int
```
   
## Reference 
```kotlin
dataframes {
    sourceSet = "mySources" // [optional; default: "main"]
    packageName = "org.jetbrains.data" // [optional; default: common package under source set]
    
    schema {
        sourceSet /* String */ = "…" // [optional; override default]
        packageName /* String */ = "…" // [optional; override default]
        src /* File */ = file("…") // [optional; default: file("src/$sourceSet/kotlin")]
        
        data /* URL | File | String */ = "…" 
        name = "org.jetbrains.data.Person" // [optional; default: from filename]
    }
}
```

## Examples

```kotlin
dataframes {
    packageName = "org.example"
    // buildDir/src/main/kotlin/org/example/GeneratedMyName.kt
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/1765966904c5920154a4a480aa1fcff23324f477/data/securities.csv"
        name = "MyName"
    }
    // buildDir/src/main/kotlin/org/example/test/GeneratedOtherName.kt
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/1765966904c5920154a4a480aa1fcff23324f477/data/securities.csv"
        name = "org.example.test.OtherName"
    }
    // output: buildDir/src/main/kotlin/org/example/data/GeneratedData.kt
    schema {
        packageName = "org.example.data"
        data = file("path/to/data.csv")
    }
}
```
