[//]: # (title: concat)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ConcatDfSamples-->

<web-summary>
Discover the `concat` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `concat` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `concat` operation for Kotlin DataFrame.
</link-summary>

Combines rows from multiple [`DataFrame`](DataFrame.md) objects and returns a single `DataFrame`.

**API reference**: [`concat`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/concat.html)

<!---FUN concatDfDataFrames-->

```kotlin
df.concat(df1, df2)
```

<!---END-->

<!---FUN concatDfInfix-->

```kotlin
df concat df1
```

<!---END-->

<!---FUN concatDfDataFrameIterable-->

```kotlin
df.concat(listOf(df1, df2))
```

<!---END-->

<!---FUN concatDfIterable-->

```kotlin
listOf(df1, df2).concat()
```

<!---END-->

See [all use cases of 'concat' operation](concat.md).
