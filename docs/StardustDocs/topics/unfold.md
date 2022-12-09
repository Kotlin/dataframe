[//]: # (title: unfold)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` in which selected data columns are converted to `ColumnGroup` according to
1. value properties for objects
2. dataframe structure parsed from data for `File` / `URL`

Special case of [convert](convert.md) operation. 

Operation is useful when you have an API that gives you class instances or there is a `File` / `URL` pointing to one of the supported formats

<!---FUN convertToColumnGroupUseCase-->

```kotlin
class RepositoryInfo(val data: Any)

fun download(url: String) = RepositoryInfo("fancy response from the API")
```

<!---END-->

Consider you have an existing dataframe with some URLs

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

convertToColumnGroupBenefits

<!---FUN convertToColumnGroupBenefits-->

```kotlin
df.move { response.data }.toTop()
df.rename { response.data }.into("description")
```

<!---END-->
