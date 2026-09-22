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

The keys are in the same order as the rows. A key that occurs in several rows appears
at the position of its first row, with the value of its last one.

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

Output:

```text
{
  Alice Cooper: 15,
  Bob Dylan: 45,
  Charlie Daniels: 20,
  Charlie Chaplin: 40,
  Bob Marley: 30,
  Alice Wolf: 20,
  Charlie Byrd: 30,
  Alice Smith: 30,
  Bob Brown: 15,
  Charlie Johnson: 18
}
```

<!---END-->

Both rules are visible at once if the city is taken as the key:
`London` comes first, because the first person from London is in the first row,
but it holds the last one. A row without a city gives a `null` key:

<!---FUN notebook_test_associate_3-->

```kotlin
df.associate { city to "${name.firstName} ${name.lastName}" }
```

Output:

```text
{
  London: Bob Brown,
  Dubai: Charlie Johnson,
  Moscow: Charlie Byrd,
  Milan: Alice Smith,
  Tokyo: Bob Marley,
  null: Alice Wolf
}
```

<!---END-->
