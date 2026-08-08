# none


<web-summary>
Discover `none` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `none` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `none` operation in Kotlin Dataframe.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.utils.NoneSamples-->

Checks that none of the rows in the [`DataFrame`](DataFrame.md) satisfy the predicate.

Returns `Boolean` — `true` if none of the rows satisfy the predicate, `false` otherwise.

```kotlin
df.none { rowCondition }

rowCondition: (DataRow) -> Boolean
```

When called on a [`DataColumn`](DataColumn.md), checks that none of the values in the [`DataColumn`](DataColumn.md) satisfy the predicate.

**Related operations**: [](any.md), [](all.md), [](filter.md), [](count.md).

### Examples

<!---FUN noneDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/noneDf.html" width="100%" height="500px"></inline-frame>

Check that none of the persons' `age` is greater than 21:

<!---FUN noneSample1-->

```kotlin
df.none { age > 21 }
```

<!---END-->

Output:
```text
true
```

Check that there is no person with `age` equal to 15 and `name` equal to "Alice":

<!---FUN noneSample2-->

```kotlin
df.none { age == 15 && name == "Alice" }
```

<!---END-->

Output:
```text
false
```

Check that there is no name "Charlie" in the `name` column:

<!---FUN noneSample3-->

```kotlin
df.name.none { it == "Charlie" }
```

<!---END-->

Output:
```text
true
```
