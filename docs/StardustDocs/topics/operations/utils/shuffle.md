# shuffle


<web-summary>
Discover `shuffle` operation in Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `shuffle` operation in Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `shuffle` operation in Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.utils.ShuffleSamples-->

Returns a new [`DataFrame`](DataFrame.md) with rows in random order.

**API reference**: [`shuffle`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/shuffle.html)

You can supply a [kotlin.random.Random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/)
instance with a fixed seed for reproducible results.

```Kotlin
df.shuffle()
df.shuffle(random: Random)
```

If called on a [`DataColumn`](DataColumn.md), returns a new [`DataColumn`](DataColumn.md) 
with the same values in random order using the provided source of randomness or the default randomness.

### Examples

<!---FUN notebook_test_shuffle_1-->

```kotlin
df
```

<inline-frame src="./resources/notebook_test_shuffle_1.html" width="100%" height="500px"></inline-frame>

<!---END-->

Deterministic shuffle using a fixed seed:
<!---FUN notebook_test_shuffle_2-->

```kotlin
df.shuffle(Random(42))
```

<inline-frame src="./resources/notebook_test_shuffle_2.html" width="100%" height="500px"></inline-frame>

<!---END-->
