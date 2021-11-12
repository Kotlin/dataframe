[//]: # (title: Slice rows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns `DataFrame` with rows at given indices:

<!---FUN getSeveralRowsByIndices-->

```kotlin
df[0, 3, 4]
```

<!---END-->

Returns `DataFrame` with rows inside given index ranges:

<!---FUN getSeveralRowsByRanges-->

```kotlin
df[1..2]
df[0..2, 4..5]
```

<!---END-->

## take

Returns `DataFrame` containing first `n` rows

<!---FUN take-->

```kotlin
df.take(5)
```

<!---END-->

### takeLast

Returns `DataFrame` containing last `n` rows

<!---FUN takeLast-->

```kotlin
df.takeLast(5)
```

<!---END-->

## drop

Returns `DataFrame` containing all rows except first `n` rows

<!---FUN drop-->

```kotlin
df.drop(5)
```

<!---END-->

## dropLast

Returns `DataFrame` containing all rows except last `n` rows

<!---FUN dropLast-->

```kotlin
df.dropLast(5)
```

<!---END-->
