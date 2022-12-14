[//]: # (title: unfold)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` in which selected data columns are converted to `ColumnGroup` / `FrameColumn` according to
1. value properties for objects
2. dataframe structure parsed from data for `File` / `URL`

Special case of [convert](convert.md) operation. 

Operation is useful when you have 
1. a library API that gives you class instances
2. there is a `File` / `URL` pointing to one of the supported formats

### Library API

<!---FUN convertToColumnGroupUseCase-->

```kotlin
class RepositoryInfo(val data: Any)

fun download(url: String) = RepositoryInfo("fancy response from the API")
```

<!---END-->

Consider you have an existing dataframe with some URLs, arguments for an API call. 

<!---FUN convertToColumnGroupData-->

```kotlin
val interestingRepos = dataFrameOf("name", "url")(
    "dataframe", "/dataframe",
    "kotlin", "/kotlin",
)

val initialData = interestingRepos
    .add("response") { download("url"()) }
```

<!---END-->

Using unfold you can convert `response` to a `ColumnGroup` and use rich [modify](modify.md) capabilities 

<!---FUN convertToColumnGroup-->

```kotlin
val df = initialData.unfold("response")
```

<!---END-->

<!---FUN convertToColumnGroupBenefits-->

```kotlin
df.move { response.data }.toTop()
df.rename { response.data }.into("description")
```

<!---END-->

### File / URL resource

Let's create a dataframe with column `contributors` pointing to JSON resources

<!---FUN convertToFrameColumnAPI-->

```kotlin
fun testResource(resourcePath: String): URL = UtilTests::class.java.classLoader.getResource(resourcePath)!!

val interestingRepos = dataFrameOf("name", "url", "contributors")(
    "dataframe", "/dataframe", testResource("dataframeContributors.json"),
    "kotlin", "/kotlin", testResource("kotlinContributors.json"),
)
```

<!---END-->

Use `unfold` to read `DataFrame` for every row in `contributors`, effectively converting it into `FrameColumn`.
Resulting `FrameColumn` can be used to create `GroupBy` and compute [summary statistics](summaryStatistics.md) or perform [aggregation](groupBy.md#aggregation).

<!---FUN convertToFrameColumn-->

```kotlin
val df = interestingRepos.unfold("contributors")

df.asGroupBy("contributors").max("contributions")
```

<!---END-->

### Advanced use cases

Fallback to basic operators if you want to customize `unfold` for your needs. 
Using custom HTTP client to create `FrameColumn` can be done with [replace](replace.md): 

<!---FUN customUnfoldRead-->

```kotlin
val contributors by column<URL>()

val df = interestingRepos
    .replace(contributors)
    .with {
        it.mapNotNullValues { url -> DataFrame.readJsonStr(url.readText()) }
    }

df.asGroupBy("contributors").max("contributions")
```

<!---END-->
