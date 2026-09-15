[//]: # (title: single)

<web-summary>
Discover `single` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `single` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `single` operation in Kotlin Dataframe.
</link-summary>

Returns the single [row](DataRow.md) in this [`DataFrame`](DataFrame.md).
Throws [`NoSuchElementException`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-no-such-element-exception/)
if the [`DataFrame`](DataFrame.md) is empty, and
[`IllegalArgumentException`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-illegal-argument-exception/)
if it has more than one row.

The examples on this page use the following [`DataFrame`](DataFrame.md):

<!---FUN singleDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/singleDf.html" width="100%" height="500px"></inline-frame>

<!---FUN single-->

```kotlin
val dylan = df
    .filter { age == 45 } // one row is left after filtering
    .single()
```

<!---END-->

If a [condition](DataRow.md#row-conditions) is specified,
returns the single [row](DataRow.md) that matches it,
and throws an exception if there is no matching row or if there is more than one.

<!---FUN singleCondition-->
<tabs>
<tab title="Properties">

```kotlin
val dylan = df.single { age == 45 } // only Bob Dylan is 45
```

</tab>
<tab title="Strings">

```kotlin
val dylan = df.single { "age"<Int>() == 45 } // only Bob Dylan is 45
```

</tab></tabs>
<!---END-->

## singleOrNull

Returns the single [row](DataRow.md) in this [`DataFrame`](DataFrame.md),
or `null` if the [`DataFrame`](DataFrame.md) is empty or has more than one row.

<!---FUN singleOrNull-->

```kotlin
val noOne = df
    .filter { age > 50 } // df is empty after filtering
    .singleOrNull() // returns null
```

<!---END-->

If a [condition](DataRow.md#row-conditions) is specified,
returns the single [row](DataRow.md) that matches it,
or `null` if there is no matching row or if there is more than one.

<!---FUN singleOrNullCondition-->
<tabs>
<tab title="Properties">

```kotlin
val noOne = df.singleOrNull { age == 30 } // returns null: three people are 30
```

</tab>
<tab title="Strings">

```kotlin
val noOne = df.singleOrNull { "age"<Int>() == 30 } // returns null: three people are 30
```

</tab></tabs>
<!---END-->
