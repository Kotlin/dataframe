[//]: # (title: unfold)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) in which selected data columns are converted to [`ColumnGroup`](DataColumn.md#columngroup) / [`FrameColumn`](DataColumn.md#framecolumn) according to
the values of the properties of the objects.

It's a special case of [convert](convert.md) operation. 

This operation is useful when: 
1. you use a library API that gives you class instances
2. you do not want to or cannot annotate classes with [`@DataSchema`](schemas.md)

### Library API

<!---FUN convertToColumnGroupUseCase-->

```kotlin
class RepositoryInfo(val data: Any)

fun downloadRepositoryInfo(url: String) = RepositoryInfo("fancy response from the API")
```

<!---END-->

Consider you have an existing [`DataFrame`](DataFrame.md) with some URLs, arguments for an API call. 

<!---FUN convertToColumnGroupData-->

```kotlin
val interestingRepos = dataFrameOf("name", "url")(
    "dataframe", "/dataframe",
    "kotlin", "/kotlin",
)

val initialData = interestingRepos
    .add("response") { downloadRepositoryInfo("url"<String>()) }
```

<!---END-->

Using unfold you can convert `response` to a [`ColumnGroup`](DataColumn.md#columngroup) and use rich [modify](modify.md) capabilities.

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
