[//]: # (title: requireColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Require-->

Throws an exception if the specified column is missing or its type is not subtype of `C`.
From the compiler plugin perspective, a new column will appear in the compile-time schema as a result of this operation.
The aim here is to help incrementally migrate workflows to [extension properties API](extensionPropertiesApi.md).
We recommend considering declaring a [DataSchema](dataSchema.md) and use [](cast.md) or [](convertTo) if you end up with more than a few `requireColumn` calls.

Will work in compiler plugin starting from IntelliJ IDEA 2026.2 and Kotlin 2.4.0.

```text
requireColumn { column }
```

**Related operations**: [](cast.md), [](convertTo)

```kotlin
// Before `requireColumn` extension property will not be resolved
// peopleDf.select { name.firstName }

// Require a column with a runtime check
val df = peopleDf.requireColumn { "name"["firstName"]<String>() }
// Use extension property after `requireColumn`
val v: String = df.name.firstName[0]
```

### Advanced example

Let's start with a pipeline that uses only String Column Accessors and String API overloads:

```kotlin
val repos = DataFrame
    .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")

repos
    .filter { "stargazers_count"<Int>() > 100 }
    .sortByDesc("stargazers_count")
    .select("full_name", "stargazers_count")
```

Notice how stargazers_count String is repeated three times. We can refactor this code using `requireColumn`:

```kotlin
val repos = DataFrame
    .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
    .requireColumn { "stargazers_count"<Int>() }

repos
    .filter { stargazers_count > 100 }
    .sortByDesc { stargazers_count }
    .select { "full_name" and stargazers_count }
```

This way code becomes a bit more robust. For example, usages of a renamed column will become compile time errors that are easy to spot and update:

```kotlin
val repos = DataFrame
    .readCsv("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
    .requireColumn { "stargazers_count"<Int>() }
    .rename { stargazers_count }.into("stars")

repos
    .filter { stars > 100 }
    .sortByDesc { stars }
    .select { "full_name" and stars }
```
