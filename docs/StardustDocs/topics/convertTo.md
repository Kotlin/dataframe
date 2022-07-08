[//]: # (title: convertTo)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

[Converts](convert.md) columns in `DataFrame` to match given schema.

```kotlin
convertTo<T>()
```

Any additional columns will be dropped.

You can provide custom converters and parsers:
<!---FUN customConverters-->

```kotlin
class IntClass(val value: Int)

@DataSchema
class IntSchema(val ints: IntClass)

val df = dataFrameOf("ints")(1, 2, 3)
df.convertTo<IntSchema> {
    convert<Int>().with { IntClass(it) }
}
```

<!---END-->
