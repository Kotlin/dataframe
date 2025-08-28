# any


<web-summary>
Discover `any` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `any` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `any` operation in Kotlin Dataframe.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.utils.AnySamples-->

Checks if there is at least one row in the [](DataFrame.md) that satisfies the predicate.

Returns `Boolean` — `true` if there is at least one row that satisfies the predicate, `false` otherwise.

```kotlin
any { rowCondition }
```

**Related operations**: [](all.md)

### Examples

<!---FUN notebook_test_any_3-->

```kotlin
df
```

<!---END-->

<inline-frame src="./resources/notebook_test_any_3.html" width="100%" height="500px"></inline-frame>

Check if any person `age` is greater than 21:

<!---FUN notebook_test_any_4-->

```kotlin
df.any { age > 21 }
```

<!---END-->

Output:
```text
false
```

Check if there is any person with `age` equal to 15 and `name` equal to "Alice":

<!---FUN notebook_test_any_5-->

```kotlin
df.any { age == 15 && name == "Alice" }
```

<!---END-->

Output:
```text
true
```
