[//]: # (title: Data Shemas Generation in Gradle)

> The current Gradle plugin is **under consideration for deprecation** and may be officially marked as deprecated in future releases.
>
> At the moment, **[data schema generation is handled via dedicated methods](DataSchemaGenerationMethods.md)** instead of relying on the plugin.
{style="warning"}

This page describes the Gradle plugin that generates `@DataSchema` from data samples.
```Kotlin
id("org.jetbrains.kotlinx.dataframe") version "%dataFrameVersion%"
```

It's different from the DataFrame compiler plugin:
```kotlin
kotlin("plugin.dataframe") version "%compilerPluginKotlinVersion%"
```

Gradle plugin by default adds a KSP annotation processor to your build:

```kotlin
ksp("org.jetbrains.kotlinx.dataframe:symbol-processor-all:%dataFrameVersion%")
```

You should disable it if you want to use the Gradle plugin together with the compiler plugin.

Add this to `gradle.properties`:
```properties
kotlin.dataframe.add.ksp=false
```

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
Note that the name of the file and the interface are normalized: split by '_' and ' ' and joined to CamelCase.
You can set parsing options for CSV:
```kotlin
dataframes {
    // output: build/generated/dataframe/main/kotlin/org/example/dataframe/JetbrainsRepositories.Generated.kt
    schema {
        data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
        csvOptions {
            delimiter = ','
        }
    }
}
```
In this case, the output path will depend on your directory structure. 
For project with package `org.example` path will be `build/generated/dataframe/main/kotlin/org/example/dataframe/JetbrainsRepositories.Generated.kt
`. 

Note that the name of the Kotlin file is derived from the name of the data file with the suffix
`.Generated` and the package 
is derived from the directory structure with child directory `dataframe`.

The name of the **data schema** itself is `JetbrainsRepositories`.
You could specify it explicitly:

```kotlin
schema {
    // output: build/generated/dataframe/main/kotlin/org/example/dataframe/MyName.Generated.kt
    data = "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"
    name = "MyName"
}
```

If you want to change the default package for all schemas:

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

If you want non-default name and package, consider using fully qualified name:

```kotlin
dataframes {
    // output: build/generated/dataframe/main/kotlin/org/example/data/OtherName.Generated.kt
    schema {
        name = "org.example.data.OtherName"
        data = file("path/to/data.csv")
    }
}
```

By default, the plugin will generate output in a specified source set. 
Source set could be specified for all schemas or for specific schema:

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

If you need the generated files to be put in another directory, set `src`:

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
## Schema Definitions from SQL Databases

To generate a schema for an existing SQL table, 
you need to define a few parameters to establish a JDBC connection:
URL (passing to `data` field), username, and password.

Also, the `tableName` parameter should be specified to convert the data from the table with that name to the dataframe.

```kotlin
dataframes {
    schema {
        data = "jdbc:mariadb://localhost:3306/imdb"
        name = "org.example.imdb.Actors"
        jdbcOptions {
            user = "root"
            password = "pass" 
            tableName = "actors"
        }
    }
}
```

To generate a schema for the result of an SQL query,
you need to define the same parameters as before together with the SQL query to establish connection.

```kotlin
dataframes {
    schema {
        data = "jdbc:mariadb://localhost:3306/imdb"
        name = "org.example.imdb.TarantinoFilms"
        jdbcOptions {
            user = "root" 
            password = "pass"
            sqlQuery = """
                SELECT name, year, rank,
                GROUP_CONCAT (genre) as "genres"
                FROM movies JOIN movies_directors ON movie_id = movies.id
                JOIN directors ON directors.id=director_id LEFT JOIN movies_genres ON movies.id = movies_genres.movie_id
                WHERE directors.first_name = "Quentin" AND directors.last_name = "Tarantino"
                GROUP BY name, year, rank
                ORDER BY year
                """
        }
    }
}
```

Find full example code [here](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples/blob/master/src/main/kotlin/Example_3_Import_schema_via_Gradle.kt).

**NOTE:** This is an experimental functionality and, for now,
we only support four databases: MariaDB, MySQL, PostgreSQL, and SQLite.

Additionally, support for JSON and date-time types is limited.
Please take this into consideration when using these functions.

## DSL reference
Inside `dataframes` you can configure parameters that will apply to all schemas. 
Configuration inside `schema` will override these defaults for a specific schema.
Here is the full DSL for declaring data schemas:

```kotlin
dataframes {
    sourceSet = "mySources" // [optional; default: "main"]
    packageName = "org.jetbrains.data" // [optional; default: common package under source set]
    
    visibility = // [optional; default: if explicitApiMode enabled then EXPLICIT_PUBLIC, else IMPLICIT_PUBLIC]
    // KOTLIN SCRIPT: DataSchemaVisibility.INTERNAL DataSchemaVisibility.IMPLICIT_PUBLIC, DataSchemaVisibility.EXPLICIT_PUBLIC
    // GROOVY SCRIPT: 'internal', 'implicit_public', 'explicit_public'
        
    withoutDefaultPath() // disable a default path for all schemas
    // i.e., plugin won't copy "data" property of the schemas to generated companion objects

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
        
        withoutDefaultPath() // disable the default path for this schema
        withDefaultPath() // enable the default path for this schema
    }
}
```
