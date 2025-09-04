[//]: # (title: convertTo)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

[Converts](convert.md) all columns in the [`DataFrame`](DataFrame.md) to match a given schema [`Schema`](schema.md).

```kotlin
convertTo<Schema>(excessiveColumns = ExcessiveColumns.Keep)
```

**Related operations**: [](adjustSchema.md), [](convert.md)

Conversion to match the target schema is done mostly automatically;
DataFrame knows how to convert between many types (see [](convert.md) for details and the supported types).

However, if you have a custom type in your target schema, or the automatic conversion fails,
you can provide a custom converter, parser, or filler for it.
These have priority over the automatic ones.

Customization DSL:
* `convert<A>.with { it.toB() }`
  * Provides `convertTo<>()` with the knowledge of how to convert `A` to `B`
* `parser { YourType.fromString(it) }`
  * Provides `convertTo<>()` with the knowledge of how to parse strings/chars into `YourType`
  * Shortcut for `convert<String>().with { YourType.fromString(it) }`
  * Chars are treated as strings unless you explicitly specify `convert<Char>().with { YourType.fromChar(it) }`
* `fill { some cols }.with { rowExpression }`
  * Makes `convertTo<>()` fill missing (or existing) columns from the target schema 
    with values computed by the given row expression

<!---FUN customConvertersData-->

```kotlin
class MyType(val value: Int)

@DataSchema
class MySchema(val a: MyType, val b: MyType, val c: Int)
```

<!---END-->
<!---FUN customConverters-->

```kotlin
val df: AnyFrame = dataFrameOf(
    "a" to columnOf(1, 2, 3),
    "b" to columnOf("1", "2", "3"),
)
df.convertTo<MySchema> {
    // providing the converter: Int -> MyType, so column `a` can be converted
    convert<Int>().with { MyType(it) }
    // providing the parser: String -> MyType, so column `b` can be converted
    parser { MyType(it.toInt()) }
    // providing the filler for `c`, as it's missing in `df`
    fill { c }.with { a.value + b.value }
}
```

<!---END-->
