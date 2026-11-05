[//]: # (title: convertTo)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ConvertToSamples-->

Converts all columns in the [`DataFrame`](DataFrame.md) to match a given schema [`Schema`](schema.md).

**API reference**: [`convertTo`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/convert-to.html)

```text
convertTo<Schema>(
    excessiveColumnsBehavior = ExcessiveColumns.Keep,
) { customization }
```

**Related operations**: [](adjustSchema.md), [](convert.md), [](cast.md)

> `convertTo` is the name of two different operations, and they are easy to confuse:
>
> * `DataFrame<*>.convertTo<Schema>()` — **this page**: converts the whole dataframe
>   so that its columns match a schema
> * [`DataColumn<*>.convertTo<ValueType>()`](convert.md#single-columns) —
>   converts one column to a value type
>
> The receivers differ: a whole dataframe against a single column.

## How it works

Columns are matched to the schema by their full column path, including columns nested
in [groups](DataColumn.md#columngroup). Every matched column is converted with the same
automatic conversions as [`convert { }.to<Type>()`](convert.md#to) — the supported types are listed
in [](convert.md#supported-target-types). Nested columns are converted too, each on its own.

When the automatic conversion is not enough, you can provide a custom converter, parser, or filler
for it. These have priority over the automatic ones.

## Customization DSL

* `convert<A>.with { it.toB() }`
  * Provides `convertTo<>()` with the knowledge of how to convert `A` to `B`
* `parser { YourType.fromString(it) }`
  * Provides `convertTo<>()` with the knowledge of how to parse strings/chars into `YourType`
  * Shortcut for `convert<String>().with { YourType.fromString(it) }`
  * Chars are treated as strings unless you explicitly specify `convert<Char>().with { YourType.fromChar(it) }`
* `fill { some cols }.with { rowExpression }`
  * Makes `convertTo<>()` fill missing (or existing) columns from the target schema
    with values computed by the given row expression
* `convertIf({ fromType, toSchema -> ... }) { value -> ... }`
  * Advanced version of `convert` for multiple or unknown target types;
    the condition decides whether the converter applies
  * The exact-type converters win over matching `convertIf` ones,
    and among `convertIf` converters the first match is used

<!---FUN customConvertersData-->

```kotlin
class MyType(val value: Int)
```

<!---END-->

with a schema that uses it:

```text
@DataSchema
class MySchema(val a: MyType, val b: MyType, val c: Int)
```

<!---FUN customConverters-->
<tabs>
<tab title="Properties">

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

</tab>
<tab title="Strings">

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
    fill { c }.with { getValue<MyType>("a").value + getValue<MyType>("b").value }
}
```

</tab></tabs>
<!---END-->

A custom `parser { }` is also the way to convert date-like strings that the default date parser
can't read:

<!---FUN convertToWithDateParser-->

```kotlin
val df: AnyFrame = dataFrameOf(
    "name" to columnOf("KotlinConf", "ADF Meetup"),
    "date" to columnOf("22.11.2025", "05.12.2025"),
)
// the default date parser can't read `dd.MM.yyyy`; a custom parser can
df.convertTo<Event> {
    parser {
        LocalDate.parse(
            it,
            LocalDate.Format {
                dayOfMonth()
                chars(".")
                monthNumber()
                chars(".")
                year()
            },
        )
    }
}
```

<inline-frame src="./resources/convertToWithDateParser.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Excessive columns

Columns of the dataframe that the schema doesn't have are called excessive.
`excessiveColumnsBehavior` says what happens to them:

* `ExcessiveColumns.Keep` — the default: they stay in the resulting dataframe as is
* `ExcessiveColumns.Remove` — they are dropped
* `ExcessiveColumns.Fail` — `convertTo` throws `ExcessiveColumnsException`

<!---FUN convertToExcessiveColumns-->

```kotlin
val df: AnyFrame = dataFrameOf(
    "name" to columnOf("Alice", "Bob"),
    "age" to columnOf(15, 45),
    "weight" to columnOf(54, 87),
)
// `weight` is not in the schema:
df.convertTo<PersonCore>() // kept, because `ExcessiveColumns.Keep` is the default
df.convertTo<PersonCore>(excessiveColumnsBehavior = ExcessiveColumns.Remove) // dropped
```

<inline-frame src="./resources/convertToExcessiveColumns.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Nested schemas

Schema conversion is especially relevant for structured dataframes: column groups are matched
recursively, and the nested columns are converted too:

<!---FUN convertToNestedSchema-->

```kotlin
val df: AnyFrame = dataFrameOf("firstName", "lastName", "city", "weight")(
    "Alice",
    "Cooper",
    "London",
    "54",
    "Bob",
    "Dylan",
    "Dubai",
    "87",
).group("firstName", "lastName").into("name")
    .group("city", "weight").into("info")
// `info.weight` is a String column; the schema requires Int, so it is converted inside the group
df.convertTo<Person>()
```

<inline-frame src="./resources/convertToNestedSchema.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Troubleshooting

`convertTo<Schema>()` fails with one of these exceptions:

| Exception | When it is thrown | What to check |
|---|---|---|
| `IllegalArgumentException` | The schema requires a column that the dataframe doesn't have, and its type is not nullable | the column names in the schema; add the column, or fill it with `fill { }.with { }` |
| `ExcessiveColumnsException` | The dataframe has columns the schema doesn't, and `excessiveColumnsBehavior = Fail` | the extra columns; pick `Keep` or `Remove`, or extend the schema |
| `TypeConverterNotFoundException` | No conversion is known for a column's type | provide a `convert<A>().with { }` or a `parser { }` for that type |
| `TypeConversionException` | A conversion failed; most often a `null` in a column the schema declares non-null, or a string that can't be parsed | clean up the values, make the schema column nullable, or provide a custom converter |

When several custom converters could apply, the exact-type converter wins over a `convertIf` one,
and among `convertIf` converters the first one that matches is used.

<!---FUN convertToMissingColumnFails-->

```kotlin
// `age` is required by the schema but missing in the dataframe
shouldThrow<IllegalArgumentException> {
    dataFrameOf("name")("Alice", "Bob").convertTo<PersonWithAge>()
}
```

Output:

```text
The following columns were not found in DataFrame: [age], and their type was not nullable. Use `fill` to initialize these columns
```

<!---END-->

## When to use convertTo

* `convertTo<Schema>()` — many columns at once, matched to a known schema; converts values and keeps the structure
* [`cast<Schema>()`](cast.md) — changes only the static type, converts nothing; use it when the values already match the schema
* [`convert { }.to<Type>()`](convert.md#to) — a few selected columns, converted by hand
* [`add`](addRemove.md) / [`update`](update.md) — new columns or per-row changes, without schema matching
