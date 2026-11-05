[//]: # (title: requireColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.RequireSamples-->

Resolves the column in this [`DataFrame`](DataFrame.md) and checks that its runtime type is a subtype of `C`.
Throws an exception if the column can't be resolved or if its type doesn't match.
From the compiler plugin perspective, a new column will appear in the compile-time schema as a result of this operation.
The aim here is to help incrementally migrate workflows to [extension properties API](extensionPropertiesApi.md).
We recommend considering declaring a [DataSchema](dataSchema.md) and using [](cast.md) or [](convertTo.md) if you end up with more than a few `requireColumn` calls.

**API reference**: [`requireColumn`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/require-column.html)

> Will work in the compiler plugin starting from IntelliJ IDEA 2026.2 and Kotlin 2.4.0.
> {style="note"}

```text
requireColumn<C> { column }
```

**Related operations**: [](cast.md), [](convertTo.md)

<!---FUN requireColumnNested-->

```kotlin
val df = DataFrame.readJson(file)
// Without `requireColumn` the extension property will not resolve:
// df.select { name.firstName }
val typed = df.requireColumn { "name"["firstName"]<String>() }
val v: String = typed.name.firstName[0]
```

<inline-frame src="./resources/requireColumnNested.html" width="100%" height="500px"></inline-frame>

<!---END-->

### Advanced example

Let's start with a pipeline that uses only String Column Accessors and String API overloads:

<!---FUN requireColumnStringApi-->

```kotlin
val repos = DataFrame.readCsv(file)

repos
    .filter { "stargazers_count"<Int>() > 100 }
    .sortByDesc("stargazers_count")
    .select("full_name", "stargazers_count")
```

<inline-frame src="./resources/requireColumnStringApi.html" width="100%" height="500px"></inline-frame>

<!---END-->

Notice how the `stargazers_count` string is repeated three times. We can refactor this code using `requireColumn`:

<!---FUN requireColumnTyped-->

```kotlin
val repos = DataFrame.readCsv(file)
    .requireColumn { "stargazers_count"<Int>() }

repos
    .filter { stargazers_count > 100 }
    .sortByDesc { stargazers_count }
    .select { "full_name" and stargazers_count }
```

<inline-frame src="./resources/requireColumnTyped.html" width="100%" height="500px"></inline-frame>

<!---END-->

This way code becomes a bit more robust. For example, after `.rename { stargazers_count }.into("stars")`
usages of the `stargazers_count` property become compile-time errors that are easy to spot and update to `stars`:

<!---FUN requireColumnRename-->

```kotlin
val repos = DataFrame.readCsv(file)
    .requireColumn { "stargazers_count"<Int>() }
    .rename { stargazers_count }.into("stars")

// usages of the `stargazers_count` property no longer resolve:
// repos.filter { stargazers_count > 100 }
repos.filter { stars > 100 }
```

<inline-frame src="./resources/requireColumnRename.html" width="100%" height="500px"></inline-frame>

<!---END-->

`requireColumn` can also change the compile-time type of a column that is already in the schema: pass a [cast](cast.md) selector,
and the runtime type of the column must be a subtype of the new type.
