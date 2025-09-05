# associate

<web-summary>
Discover `associate` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `associate` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `associate` operation for Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop.AssociateSamples-->

The `associate` function builds a `Map` from key–value `Pair`s produced by applying a transformation to each row
of this [`DataFrame`](DataFrame.md)
using a [row expression](DataRow.md#row-expressions).

If multiple rows produce the same key, only the last value for that key is kept. This matches the behavior of Kotlin’s standard [`kotlin.collections.associate`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/associate.html) function.

```kotlin
df.associate { pairSelector }

pairSelector: (DataRow) -> Pair
```

### Related functions
- [`toMap`](toMap.md) — converts a [`DataFrame`](DataFrame.md) into a `Map` by using column names as keys and their values as map values.
- [`associateBy`](associateBy.md) — creates a map with rows as values.

### Example

<!---FUN notebook_test_associate_1-->

```kotlin
df
```

<!---END-->

<inline-frame src="./resources/notebook_test_associate_1.html" width="100%" height="500px"></inline-frame>

Create a map from name to age using a pair selector:

<!---FUN notebook_test_associate_2-->

```kotlin
df.associate { "${name.firstName} ${name.lastName}" to age }
```

<!---END-->

Output:

```text
{
  Alice Cooper: 15,
  Bob Dylan: 45,
  Charlie Daniels: 20,
  Charlie Chaplin: 40,
  Bob Marley: 30,
  Alice Wolf: 20,
  Charlie Byrd: 30
}
```
