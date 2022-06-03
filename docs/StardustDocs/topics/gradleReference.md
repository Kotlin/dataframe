[//]: # (title: Gradle plugin reference)

## Examples
In the best scenario, your schema could be defined as simple as this:
```kotlin
dataframes {
    // output: build/generated/dataframe/main/kotlin/org/example/dataframe/JetbrainsRepositories.Generated.kt
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
    }
}
```
Note than name of the file and the interface are normalized: split by '_' and ' ' and joined to camel case.
You can set parsing options for CSV:
```kotlin
dataframes {
    // output: build/generated/dataframe/main/kotlin/org/example/dataframe/Securities.Generated.kt
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/1765966904c5920154a4a480aa1fcff23324f477/data/securities.csv"
        csvOptions {
            delimiter = ';'
        }
    }
}
```
In this case output path will depend on your directory structure. For project with package `org.example` path will be `build/generated/dataframe/main/kotlin/org/example/dataframe/Securities.Generated.kt
`. Note that name of the Kotlin file is derived from the name of the data file with the suffix `.Generated` and the package is derived from the directory structure with child directory `dataframe`. The name of the **data schema** itself is `Securities`. You could specify it explicitly:
```kotlin
schema {
    // output: build/generated/dataframe/main/kotlin/org/example/dataframe/MyName.Generated.kt
    data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
    name = "MyName"
}
```
If you want to change default package for all schemas:
```kotlin
dataframes {
    packageName = "org.example"
    // Schemas...
}
```
Then you can set packageName for specific schema exclusively:
```kotlin
dataframes {
    // output: build/generated/dataframe/main/kotlin/org/example/data/OtherName.Generated.kt
    schema {
        packageName = "org.example.data"
        data = file("path/to/data.csv")
    }
}
```
If you want non-default name and package, consider using fully-qualified name:
```kotlin
dataframes {
    // output: build/generated/dataframe/main/kotlin/org/example/data/OtherName.Generated.kt
    schema {
        name = "org.example.data.OtherName"
        data = file("path/to/data.csv")
    }
}
```
By default, plugin will generate output in specified source set. Source set could be specified for all schemas or for specific schema:
```kotlin
dataframes {
    packageName = "org.example"
    sourceSet = "test"
    // output: build/generated/dataframe/test/kotlin/org/example/Data.Generated.kt
    schema {
        data = file("path/to/data.csv")
    }
    // output: build/generated/dataframe/integrationTest/kotlin/org/example/Data.Generated.kt
    schema {
        sourceSet = "integrationTest"
        data = file("path/to/data.csv")
    }
}
```
But if you need generated files in other directory, set `src`:
```kotlin
dataframes {
    // output: schemas/org/example/test/OtherName.Generated.kt
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        name = "org.example.test.OtherName"
        src = file("schemas")
    }
}
```

## DSL reference
Inside `dataframes` you can configure parameters that will apply to all schemas. Configuration inside `schema` will override these defaults for specific schema.
Here is full DSL for declaring data schemas:

```kotlin
dataframes {
    sourceSet = "mySources" // [optional; default: "main"]
    packageName = "org.jetbrains.data" // [optional; default: common package under source set]
    
    visibility = // [optional; default: if explicitApiMode enabled then EXPLICIT_PUBLIC, else IMPLICIT_PUBLIC]
    // KOTLIN SCRIPT: DataSchemaVisibility.INTERNAL DataSchemaVisibility.IMPLICIT_PUBLIC, DataSchemaVisibility.EXPLICIT_PUBLIC
    // GROOVY SCRIPT: 'internal', 'implicit_public', 'explicit_public'
        
    withoutDefaultPath() // disable default path for all schemas
    // i.e. plugin won't copy "data" property of the schemas to generated companion objects

    // split property names by delimiters (arguments of this method), lowercase parts and join to camel case
    // enabled by default
    withNormalizationBy('_') // [optional: default: ['\t', '_', ' ']]
    withoutNormalization() // disable property names normalization
    
    schema {
        sourceSet /* String */ = "…" // [optional; override default]
        packageName /* String */ = "…" // [optional; override default]
        visibility /* DataSchemaVisibility */ = "…" // [optional; override default]
        src /* File */ = file("…") // [optional; default: file("build/generated/dataframe/$sourceSet/kotlin")]
        
        data /* URL | File | String */ = "…" // Data in JSON or CSV formats
        name = "org.jetbrains.data.Person" // [optional; default: from filename]
        csvOptions {
            delimiter /* Char */ = ';' // [optional; default: ',']
        }

        // See names normalization
        withNormalizationBy('_') // enable property names normalization for this schema and use these delimiters
        withoutNormalization() // disable property names normalization for this schema
        
        withoutDefaultPath() // disable default path for this schema
        withDefaultPath() // enable default path for this schema
    }
}
```
