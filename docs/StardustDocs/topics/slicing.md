[//]: # (title: Slice rows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

The following operations return `DataFrame` with a subset of rows from original `DataFrame`.

<!---FUN getSeveralRows-->

```kotlin
df[0, 3, 4]
df[1..2]

df.take(5) // first 5 rows
df.takeLast(5) // last 5 rows
df.drop(5) // all rows except first 5
df.dropLast(5) // all rows except last 5
```

<!---END-->

## take

Returns `DataFrame` containing first `n` rows

<!---FUN take-->

### takeLast

Returns `DataFrame` containing last `n` rows

<!---FUN takeLast-->

## drop

Returns `DataFrame` containing all rows except first `n` rows

<!---FUN drop-->

## dropLast

Returns `DataFrame` containing all rows except last `n` rows

<!---FUN dropLast-->
