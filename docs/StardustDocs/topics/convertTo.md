[//]: # (title: convertTo)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

[Converts](convert.md) columns in [`DataFrame`](DataFrame.md) to match given schema [`Schema`](schema.md).

```kotlin
convertTo<Schema>(excessiveColumns = ExcessiveColumns.Keep)
```

Customization DSL:
* `convert` - how specific column types should be converted
* `parser` - how to parse strings into custom types
* `fill` - how to fill missing columns

<!---FUN customConvertersData-->

```kotlin
class MyType(val value: Int)

@DataSchema
class MySchema(val a: MyType, val b: MyType, val c: Int)
```

<!---END-->
<!---FUN customConverters-->

```kotlin
val df = dataFrameOf("a", "b")(1, "2")
df.convertTo<MySchema> {
    convert<Int>().with { MyType(it) } // converts `a` from Int to MyType
    parser { MyType(it.toInt()) } // converts `b` from String to MyType
    fill { c }.with { a.value + b.value } // computes missing column `c`
}
```

<!---END-->
