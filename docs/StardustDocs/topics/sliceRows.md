[//]: # (title: Slice rows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns a `DataFrame` with rows at given indices:

<!---FUN getSeveralRowsByIndices-->

```kotlin
df[0, 3, 4]
```

<!---END-->

Returns a `DataFrame` with rows inside given index ranges (including boundary indices):

<!---FUN getSeveralRowsByRanges-->

```kotlin
df[1..2]
df[0..2, 4..5]
```

<!---END-->

## take

Returns a `DataFrame` containing first `n` rows

<!---FUN take-->

```kotlin
df.take(5)
```

<!---END-->

## takeLast

Returns a `DataFrame` containing last `n` rows

<!---FUN takeLast-->

```kotlin
df.takeLast(5)
```

<!---END-->

## takeWhile

Returns a `DataFrame` containing first rows that satisfy the given [condition](DataRow.md#row-conditions)

<!---FUN takeWhile-->

```kotlin
df.takeWhile { isHappy }
```

<!---END-->

## drop

Returns a `DataFrame` containing all rows except first `n` rows

<!---FUN drop-->

```kotlin
df.drop(5)
```

<!---END-->

## dropLast

Returns a `DataFrame` containing all rows except last `n` rows

<!---FUN dropLast-->

```kotlin
df.dropLast() // default 1
df.dropLast(5)
```

<!---END-->

## dropWhile

Returns a `DataFrame` containing all rows except first rows that satisfy the given [condition](DataRow.md#row-conditions)

<!---FUN dropWhile-->

```kotlin
df.dropWhile { !isHappy }
```

<!---END-->
