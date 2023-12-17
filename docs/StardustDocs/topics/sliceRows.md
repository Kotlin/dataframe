[//]: # (title: Slice rows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns a [`DataFrame`](DataFrame.md) with rows at given indices:

<!---FUN getSeveralRowsByIndices-->

```kotlin
df[0, 3, 4]
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.getSeveralRowsByIndices.html"/>
<!---END-->

Returns a [`DataFrame`](DataFrame.md) with rows inside given index ranges (including boundary indices):

<!---FUN getSeveralRowsByRanges-->

```kotlin
df[1..2]
df[0..2, 4..5]
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.getSeveralRowsByRanges.html"/>
<!---END-->

## take

Returns a [`DataFrame`](DataFrame.md) containing first `n` rows

<!---FUN take-->

```kotlin
df.take(5)
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.take.html"/>
<!---END-->

## takeLast

Returns a [`DataFrame`](DataFrame.md) containing last `n` rows

<!---FUN takeLast-->

```kotlin
df.takeLast(5)
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.takeLast.html"/>
<!---END-->

## takeWhile

Returns a [`DataFrame`](DataFrame.md) containing first rows that satisfy the given [condition](DataRow.md#row-conditions)

<!---FUN takeWhile-->

```kotlin
df.takeWhile { isHappy }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.takeWhile.html"/>
<!---END-->

## drop

Returns a [`DataFrame`](DataFrame.md) containing all rows except first `n` rows

<!---FUN drop-->

```kotlin
df.drop(5)
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.drop.html"/>
<!---END-->

## dropLast

Returns a [`DataFrame`](DataFrame.md) containing all rows except last `n` rows

<!---FUN dropLast-->

```kotlin
df.dropLast() // default 1
df.dropLast(5)
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.dropLast.html"/>
<!---END-->

## dropWhile

Returns a [`DataFrame`](DataFrame.md) containing all rows except first rows that satisfy the given [condition](DataRow.md#row-conditions)

<!---FUN dropWhile-->

```kotlin
df.dropWhile { !isHappy }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.dropWhile.html"/>
<!---END-->
