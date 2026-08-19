[//]: # (title: Slice rows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.SliceRowsSamples-->

Returns a [`DataFrame`](DataFrame.md) with rows at given indices:

<!---FUN sliceRowsDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/sliceRowsDf.html" width="100%" height="500px"></inline-frame>

<!---FUN getSeveralRowsByIndices-->

```kotlin
df[0, 3, 4]
```

<!---END-->
<inline-frame src="./resources/getSeveralRowsByIndices.html" width="100%" height="500px"></inline-frame>

Returns a [`DataFrame`](DataFrame.md) with rows inside given index ranges (including boundary indices):

<!---FUN getSeveralRowsByRanges1-->

```kotlin
df[1..2]
```

<!---END-->
<inline-frame src="./resources/getSeveralRowsByRanges1.html" width="100%" height="500px"></inline-frame>

<!---FUN getSeveralRowsByRanges2-->

```kotlin
df[0..2, 4..5]
```

<!---END-->
<inline-frame src="./resources/getSeveralRowsByRanges2.html" width="100%" height="500px"></inline-frame>

## take

Returns a [`DataFrame`](DataFrame.md) containing first `n` rows

<!---FUN take-->

```kotlin
df.take(5)
```

<!---END-->
<inline-frame src="./resources/take.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), returns a [`DataColumn`](DataColumn.md) containing its first `n` values.

<!---FUN takeDataColumn-->

```kotlin
df.age.take(5)
```

<!---END-->
<inline-frame src="./resources/takeDataColumn.html" width="100%" height="500px"></inline-frame>

## takeLast

Returns a [`DataFrame`](DataFrame.md) containing last `n` rows

<!---FUN takeLast-->

```kotlin
df.takeLast(5)
```

<!---END-->
<inline-frame src="./resources/takeLast.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), returns a [`DataColumn`](DataColumn.md) containing its last `n` values.

<!---FUN takeLastDataColumn-->

```kotlin
df.age.takeLast(5)
```

<!---END-->
<inline-frame src="./resources/takeLastDataColumn.html" width="100%" height="500px"></inline-frame>

## takeWhile

Returns a [`DataFrame`](DataFrame.md) containing first rows that satisfy the given [condition](DataRow.md#row-conditions)

<!---FUN takeWhile-->

```kotlin
df.takeWhile { isHappy }
```

<!---END-->
<inline-frame src="./resources/takeWhile.html" width="100%" height="500px"></inline-frame>

## drop

Returns a [`DataFrame`](DataFrame.md) containing all rows except first `n` rows

<!---FUN drop-->

```kotlin
df.drop(5)
```

<!---END-->
<inline-frame src="./resources/drop.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), returns a [`DataColumn`](DataColumn.md) 
containing all values of this [`DataColumn`](DataColumn.md) except the first `n` values.

<!---FUN dropDataColumn-->

```kotlin
df.age.drop(5)
```

<!---END-->
<inline-frame src="./resources/dropDataColumn.html" width="100%" height="500px"></inline-frame>

## dropLast

Returns a [`DataFrame`](DataFrame.md) containing all rows except last `n` rows

<!---FUN dropLast1-->

```kotlin
df.dropLast() // default 1
```

<!---END-->
<inline-frame src="./resources/dropLast1.html" width="100%" height="500px"></inline-frame>

<!---FUN dropLast2-->

```kotlin
df.dropLast(5)
```

<!---END-->
<inline-frame src="./resources/dropLast2.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), returns a [`DataColumn`](DataColumn.md) 
containing all values of this [`DataColumn`](DataColumn.md) except the last `n` values.

<!---FUN dropLastDataColumn-->

```kotlin
df.age.dropLast(5)
```

<!---END-->
<inline-frame src="./resources/dropLastDataColumn.html" width="100%" height="500px"></inline-frame>

## dropWhile

Returns a [`DataFrame`](DataFrame.md) containing all rows except first rows that satisfy the given [condition](DataRow.md#row-conditions)

<!---FUN dropWhile-->

```kotlin
df.dropWhile { isHappy }
```

<!---END-->
<inline-frame src="./resources/dropWhile.html" width="100%" height="500px"></inline-frame>
