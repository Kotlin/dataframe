# shuffle


<web-summary>
Discover `shuffle` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `shuffle` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `shuffle` operation in Kotlin Dataframe.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.utils.ShuffleSamples-->

Returns a new [`DataFrame`](DataFrame.md) with rows in random order.

You can supply a [kotlin.random.Random](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/)
instance for reproducible results.

```Kotlin
df.shuffle()
df.shuffle(random: Random)
```

### Examples

<!---FUN notebook_test_shuffle_1-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/notebook_test_shuffle_1.html" width="100%" height="500px"></inline-frame>

Deterministic shuffle using a fixed seed:
<!---FUN notebook_test_shuffle_2-->

```kotlin
df.shuffle(Random(42))
```

<!---END-->
<inline-frame src="./resources/notebook_test_shuffle_2.html" width="100%" height="500px"></inline-frame>
