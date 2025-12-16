# Setup And Overview

<web-summary>
Explore the Kotlin DataFrame Compiler Plugin —  
a powerful tool providing on-the-fly type-safe column-accessors for dataframes.
</web-summary>

<card-summary>
Explore the Kotlin DataFrame Compiler Plugin —  
a powerful tool providing on-the-fly type-safe column-accessors for dataframes.
</card-summary>

<link-summary>
Explore the Kotlin DataFrame Compiler Plugin —  
a powerful tool providing on-the-fly type-safe column-accessors for dataframes.
</link-summary>


> Now available in Gradle (IDEA 2025.2+) and Maven (IDEA 2025.3+) projects, is coming soon to Kotlin Notebook.

**Kotlin DataFrame Compiler Plugin** is a Kotlin compiler plugin that automatically generates  
**[type-safe extension properties](extensionPropertiesApi.md)** for your dataframes,  
allowing you to access columns and row values in a type-safe way and avoid mistakes in column names.

## Why use it?

- Access columns as regular properties: `df.name` instead of `df["name"]`.
- Get full IDE and compiler support: autocompletion, refactoring, and type checking.
- Improve code readability and safety when working with DataFrame.

Check out this video that shows how expressions update the schema of a dataframe: 

<video src="compiler_plugin.mp4" controls=""/>

## Setup

We recommend using an up-to-date IntelliJ IDEA and Kotlin version for the best experience. Requires at least versions 2025.2 and 2.2.20, respectively. 

<tabs>

<tab title="Gradle">

Setup plugins in build.gradle.kts:

```kotlin
kotlin("jvm") version "%compilerPluginKotlinVersion%"
```

```kotlin
kotlin("plugin.dataframe") version "%compilerPluginKotlinVersion%"
```

Setup library dependency:
```kotlin
implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
```

Add this line to `gradle.properties`:
```properties
kotlin.incremental=false
```

Sync the project.

Disabling incremental compilation will no longer be necessary
when https://youtrack.jetbrains.com/issue/KT-66735 is resolved.

</tab>

<tab title="Maven">

The DataFrame compiler plugin can be used in Maven projects starting from IntelliJ IDEA 2025.3, available now as EAP builds

Setup plugin in pom.xml:

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>%compilerPluginKotlinVersion%</version>

    <configuration>
        <compilerPlugins>
            <plugin>kotlin-dataframe</plugin>
        </compilerPlugins>
    </configuration>

    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-dataframe</artifactId>
            <version>%compilerPluginKotlinVersion%</version>
        </dependency>
    </dependencies>
</plugin>
```

Setup library dependency:

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>dataframe</artifactId>
    <version>%dataFrameVersion%</version>
</dependency>
```

Sync the project.

</tab>

</tabs>

## Features overview

### Static interpretation of DataFrame API

Plugin evaluates dataframe operations, given compile-time known arguments such as constant String, resolved types, property access calls.
It updates the return type of the function call to provide properties that match column names and types.
The goal is to reflect the result of operations you apply to dataframe in types and have convenient typed API 

```kotlin
val weatherData = dataFrameOf(
    "time" to columnOf(0, 1, 2, 4, 5, 7, 8, 9),
    "temperature" to columnOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
    "humidity" to columnOf(0.5, 0.32, 0.11, 0.89, 0.68, 0.57, 0.56, 0.5)
)

weatherData.filter { temperature > 15.0 }.print()
```

The schema of DataFrame, as the compiler plugin sees it,
is displayed when you hover on an expression or variable:

![image.png](schema_info.png)

### @DataSchema declarations

Untyped DataFrame can be assigned a data schema - top-level interface or class that describes names and types of columns in the dataframe.

```kotlin
@DataSchema
data class Repositories(
    @ColumnName("full_name")
    val fullName: String,
    @ColumnName("html_url")
    val htmlUrl: java.net.URL,
    @ColumnName("stargazers_count")
    val stargazersCount: Int,
    val topics: String,
    val watchers: Int
)

fun main() {
    val df = DataFrame
        .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
        .convertTo<Repositories>()

    df.filter { stargazersCount > 50 }.print()
}
```

[Learn more](dataSchema.md) about data schema declarations

## Examples

* [Kotlin DataFrame in the IntelliJ IDEA Gradle project example](https://github.com/Kotlin/dataframe/blob/master/examples/kotlin-dataframe-plugin-gradle-example)  
  — an IntelliJ IDEA Gradle project showcasing simple DataFrame expressions using the Compiler Plugin.
* [Kotlin DataFrame in the IntelliJ IDEA Maven project example](https://github.com/Kotlin/dataframe/blob/master/examples/kotlin-dataframe-plugin-maven-example)   
  — an IntelliJ IDEA Maven project showcasing simple DataFrame expressions using the Compiler Plugin.
* [](compilerPluginExamples.md) — few examples of Compiler Plugin usages.
