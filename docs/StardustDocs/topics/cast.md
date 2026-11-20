[//]: # (title: cast)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Changes the type argument of the [`DataFrame`](DataFrame.md) instance without changing its contents.

**API reference**: [`cast`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/cast.html)

```kotlin
cast<T>(verify = true)
castUnchecked<T>()
```

Related operations: [](adjustSchema.md), [](require.md)

**Parameters:**
* `verify: Boolean = true` —
  when `true`, the function throws an exception if the [`DataFrame`](DataFrame.md) instance doesn't match the given schema:
  a column of the schema is missing, or a column has a type that is not a subtype of the type declared in the schema.
  Columns that are not declared in the schema are kept.
  When `false`, it just changes the formal type without actual data checks.

`castUnchecked<T>()` is the same as `cast<T>(verify = false)`, but without reified type parameter.

Use this operation to change the formal type of a [`DataFrame`](DataFrame.md) instance
to match the expected schema and enable generated [extension properties](extensionPropertiesApi.md) for it.

```kotlin
@DataSchema
interface Person {
    val age: Int
    val name: String
}

df.cast<Person>()
```

To convert [`DataFrame`](DataFrame.md) columns to match given schema, use [`convertTo`](convertTo.md) operation.

**Casting `GroupBy` and column groups**

```text
groupBy.cast<Keys, Groups>(verify = true)
groupBy.castUnchecked<Keys, Groups>()

columnGroup.cast<T>(verify = true)
columnGroup.castUnchecked<T>()
```

For a [`GroupBy`](groupBy.md), `Keys` is the schema of the key columns and `Groups` is the schema of each group.
With `verify = true`, the key columns are checked against `Keys` and every group is checked against `Groups`.
Because `Keys` describes only the key columns, casting with the schema of the original
[`DataFrame`](DataFrame.md) as `Keys` throws unless all of its columns are keys.
Use `castUnchecked` to change only the formal type.

A [column group](DataColumn.md#columngroup) is checked against `T` the same way a [`DataFrame`](DataFrame.md) is.

**Reusing implicitly generated schema**

```kotlin
castTo<T>(df: DataFrame<T>)
```

In notebooks, dataframe types are implicitly generated.

![Implicitly generated schema](implicitlyGeneratedSchema.png)

This type can be referred to, but its name will change whenever you re-execute cells.
Here's how you can do it in a more robust way:

<!---FUN castToGenerateSchema-->

```kotlin
val sample = DataFrame.readJson("sample.json")
```

<!---END-->

<!---FUN castTo-->

```kotlin
for (file in files) {
    // df here is expected to have the same structure as sample
    val df = DataFrame.readJson(file).castTo(sample)
    val count = df.count { perf > 10.0 }
    println("$file: $count")
}
```

<!---END-->
