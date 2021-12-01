[//]: # (title: Gradle plugin reference)

Here is full DSL for declaring data schemas:

```kotlin
dataframes {
    sourceSet = "mySources" // [optional; default: "main"]
    packageName = "org.jetbrains.data" // [optional; default: common package under source set]
    
    visibility = // [optional; default: if explicitApiMode enabled then EXPLICIT_PUBLIC, else IMPLICIT_PUBLIC]
    // KOTLIN SCRIPT: DataSchemaVisibility.INTERNAL DataSchemaVisibility.IMPLICIT_PUBLIC, DataSchemaVisibility.EXPLICIT_PUBLIC
    // GROOVY SCRIPT: 'internal', 'implicit_public', 'explicit_public'
    
    schema {
        sourceSet /* String */ = "…" // [optional; override default]
        packageName /* String */ = "…" // [optional; override default]
        visibility /* DataSchemaVisibility */ = "…" // [optional; override default]
        src /* File */ = file("…") // [optional; default: file("src/$sourceSet/kotlin")]
        
        data /* URL | File | String */ = "…" // Data in JSON or CSV formats
        name = "org.jetbrains.data.Person" // [optional; default: from filename]
    }
}
```

## Examples
In the best scenario, your schema could be defined as simple as this:
```kotlin
dataframes {
    // output: src/main/kotlin/org/example/dataframe/Jetbrains_repositories.Generated.kt
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
    }
}
```
In this case output path will depend on your directory structure. For project with package `org.example` path will be `src/main/kotlin/org/example/dataframe/Securities.Generated.kt
`. Note that name of the Kotlin file is derived from the name of the data file with the suffix `.Generated` and the package is derived from the directory structure with child directory `dataframe`. The name of the **data schema** itself is `Securities`. You could specify it explicitly:
```kotlin
schema {
    // output: src/main/kotlin/org/example/dataframe/MyName.Generated.kt
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
    // output: src/main/kotlin/org/example/data/OtherName.Generated.kt
    schema {
        packageName = "org.example.data"
        data = file("path/to/data.csv")
    }
}
```
If you want non-default name and package, consider using fully-qualified name:
```kotlin
dataframes {
    // output: src/main/kotlin/org/example/data/OtherName.Generated.kt
    schema {
        name = org.example.data.OtherName
        data = file("path/to/data.csv")
    }
}
```
By default, plugin will generate output in specified source set. Source set could be specified for all schemas or for specific schema:
```kotlin
dataframes {
    packageName = "org.example"
    sourceSet = "test"
    // output: src/test/kotlin/org/example/Data.kt
    schema {
        data = file("path/to/data.csv")
    }
    // output: src/integrationTest/kotlin/org/example/Data.kt
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
