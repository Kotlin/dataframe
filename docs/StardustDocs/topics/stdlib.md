[//]: # (title: Interop with Stdlib)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

`DataFrame` can be interpreted as an `Iterable<DataRow>`. Although `DataFrame` doesn't implement `Iterable` interface, it defines most extension functions available for `Iterable`
<!---FUN iterableApi-->

```kotlin
df.forEach { println(it) }
df.take(5)
df.drop(2)
df.chunked(10)
```

<!---END-->

### asIterable / asSequence

`DataFrame` can be converted to `Iterable` or to `Sequence`:
<!---FUN asIterableOrSequence-->

```kotlin
df.asIterable()
df.asSequence()
```

<!---END-->
