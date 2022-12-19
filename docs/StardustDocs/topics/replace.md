[//]: # (title: replace)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces one or several columns with new columns.

```kotlin
replace { columns }
    .with(newColumns) | .with { columnExpression }

columnExpression: DataFrame.(DataColumn) -> DataColumn
```

See [column selectors](ColumnSelectors.md)

<!---FUN replace-->

```kotlin
df.replace { name }.with { name.firstName }
df.replace { colsOf<String?>() }.with { it.lowercase() }
df.replace { age }.with { 2021 - age named "year" }
```

<!---END-->

<tip>

`replace { columns }.with { columnExpression } ` is equivalent to `convert { columns }.to { columnExpression }`. See [`convert`](convert.md) for details.

</tip>

### Advanced example

To explore the power of the `replace` operation, let's consider the following example.

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

We can use `replace` and `with` to read a `DataFrame` for every row in `contributors`,
effectively converting it into `FrameColumn`.

The resulting `FrameColumn` can be used to create a `GroupBy` and compute [summary statistics](summaryStatistics.md)
or perform [aggregation](groupBy.md#aggregation).

<!---FUN customUnfoldRead-->

```kotlin
val contributors by column<URL>()

val df = interestingRepos
    .replace { contributors }
    .with {
        it.mapNotNullValues { url -> DataFrame.readJsonStr(url.readText()) }
    }

df.asGroupBy("contributors").max("contributions")
```

<!---END-->
