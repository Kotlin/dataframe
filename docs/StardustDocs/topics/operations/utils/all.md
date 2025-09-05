# all


<web-summary>
Discover `all` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `all` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `all` operation in Kotlin Dataframe.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.utils.AllSamples-->

Checks if all rows in the [](DataFrame.md) satisfy the predicate.

Returns `Boolean` — `true` if every row satisfies the predicate, `false` otherwise.

```kotlin
all { rowCondition }

rowCondition: (DataRow) -> Boolean
```

**Related operations**: [](any.md), [](filter.md), [](single.md), [](count.md).

### Examples

<!---FUN notebook_test_all_3-->

```kotlin
df
```

<!---END-->

<inline-frame src="./resources/notebook_test_all_3.html" width="100%" height="500px"></inline-frame>

Check if all persons' `age` is greater than 21:

<!---FUN notebook_test_all_4-->

```kotlin
df.all { age > 21 }
```

<!---END-->

Output:
```text
false
```

Check if all persons have `age` greater or equal to 15:

<!---FUN notebook_test_all_5-->

```kotlin
df.all { name.first().isUpperCase() && age >= 15 }
```

<!---END-->

Output:
```text
true
```
