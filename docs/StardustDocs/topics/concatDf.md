[//]: # (title: concat)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) with the union of rows from several given [`DataFrames`](DataFrame.md).

<!---FUN concatDataFrames-->

```kotlin
df.concat(df1, df2)
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.concatDfs.html"/>
<!---END-->

<!---FUN concatIterable-->

```kotlin
listOf(df1, df2).concat()
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.concatIterable.html"/>
<!---END-->

See [all use cases of 'concat' operation](concat.md).
