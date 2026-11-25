# cast / castUnchecked / castTo

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CastSamples-->

<web-summary>
Discover the `cast`, `castUnchecked`, and `castTo` operations for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `cast`, `castUnchecked`, and `castTo` operations for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `cast`, `castUnchecked`, and `castTo` operations for Kotlin DataFrame.
</link-summary>

## `cast`

Changes the schema type argument of a [`DataFrame`](DataFrame.md) to a schema marker `T`,
checking its columns against the target schema by default.
Returns the same dataframe as `DataFrame<T>`: no columns are added, removed, or reordered,
and values are not converted.

A schema marker is a type, usually an interface or a data class annotated with [`@DataSchema`](dataSchema.md),
whose properties describe the columns of a dataframe (see [Data schemas](schemas.md)).
Use `cast` to assign `T` as the schema type argument and check the dataframe's columns against it by default.
Use `castUnchecked<T>()` or pass `verify = false` to change the schema type argument without checking the
dataframe's columns. Both forms still return the receiver without changing its columns or values. When the
[compiler plugin](Compiler-Plugin.md) is enabled, both operations make the generated
[extension properties](extensionPropertiesApi.md) for `T` available on the result.

**API reference**: [`cast`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/cast.html)

```text
df.cast<T>(verify: Boolean = true): DataFrame<T>
```

`cast<T>()` checks schema compatibility by default. Verification checks column types and nested schemas against `T`.
Column order does not matter, actual column types may be subtypes of the types declared by `T`, and extra columns
are allowed.
If verification fails, an `IllegalArgumentException` is thrown.
For example, a `String` column containing `"20"` cannot satisfy an `Int` column in the target schema.

See also:

* [`convertTo`](convertTo.md) — converts column values to match a target schema.
* [Adjust schema](adjustSchema.md) — explains how to align a dataframe's schema type argument with its contents.
* [Data schemas](schemas.md) — schema markers and generated extension properties.
* [`requireColumn`](require.md) — resolves a column and checks that its runtime type is a subtype of the required type;
  with the compiler plugin, the column is added to the compile-time schema.

### Parameters {id="parameters_df"}

* `T` — the target schema marker, for example a class or interface annotated with
  [`@DataSchema`](dataSchema.md).
* `verify: Boolean = true` —
  whether to check this [`DataFrame`](DataFrame.md) against `T`. When `true`, an incompatible schema causes an
  `IllegalArgumentException`. When `false`, no schema check is performed.

### Examples {id="examples_df"}

The following dataframe is used in examples that share one input. Its variable `df` has type `DataFrame<*>`:

<!---FUN castDf-->

```kotlin
df
```

<inline-frame src="./resources/castDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

The target schema is declared as follows:

<!---FUN PersonSchema-->

```kotlin
@DataSchema
interface Person {
    val name: String
    val age: Int
}
```

<!---END-->

Check the dataframe against this schema using the default `verify = true`:

<!---FUN verifiedCast-->

```kotlin
df.cast<Person>()
```

<!---END-->

The overload with `verify` can also skip verification explicitly:

<!---FUN castWithoutVerification-->

```kotlin
df.cast<Person>(verify = false)
```

<!---END-->

After the cast, the columns of `Person` can be accessed through extension properties:

<!---FUN castExtensionProperties-->

```kotlin
df.cast<Person>(verify = true)
    .filter { age > 21 }
    .name
```

<inline-frame src="./resources/castExtensionProperties.html" width="100%" height="500px"></inline-frame>

<!---END-->

The `city` column is not part of `Person`, but it does not cause verification to fail and stays in the result:

<!---FUN castKeepsExtraColumns-->

```kotlin
dataFrameOf(
    "name" to columnOf("Alice"),
    "age" to columnOf(20),
    "city" to columnOf("London"),
).cast<Person>(verify = true)
```

<inline-frame src="./resources/castKeepsExtraColumns.html" width="100%" height="500px"></inline-frame>

<!---END-->

Verification rejects incompatible column types. Here `age` is a `String` column, while `Person` requires `Int`:

<!---FUN castIncompatibleColumnType-->

```kotlin
// Throws IllegalArgumentException because age is String instead of Int.
dataFrameOf(
    "name" to columnOf("Alice"),
    "age" to columnOf("20"),
).cast<Person>(verify = true)
```

<!---END-->

A missing required column also causes verification to fail:

<!---FUN castMissingRequiredColumn-->

```kotlin
// Throws IllegalArgumentException because the required age column is missing.
dataFrameOf("name" to columnOf("Alice"))
    .cast<Person>(verify = true)
```

<!---END-->

To turn the `String` age values into `Int` values, use `convertTo`:

<!---FUN castConvertValues-->

```kotlin
// Converts age values from String to Int.
dataFrameOf(
    "name" to columnOf("Alice", "Bob"),
    "age" to columnOf("20", "25"),
).convertTo<Person>()
```

<!---END-->

## `castUnchecked`

Changes the schema type argument of a [`DataFrame`](DataFrame.md) to `T` without checking its columns.
Returns the same dataframe as `DataFrame<T>`: no columns are added, removed, or reordered, and values are not
converted.

When the [compiler plugin](Compiler-Plugin.md) is enabled, the generated
[extension properties](extensionPropertiesApi.md) for `T` become available on the result.

**API reference**: [`castUnchecked`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/cast-unchecked.html)

```text
df.castUnchecked<T>(): DataFrame<T>
```

`castUnchecked<T>()` is the same as `cast<T>(verify = false)`, but its type parameter is not
reified.

### Parameters {id="parameters_unchecked_df"}

* `T` — the schema marker to assign to the dataframe without verification.

### Examples {id="examples_unchecked_df"}

Change only the schema type argument without checks:

<!---FUN uncheckedCast-->

```kotlin
df.castUnchecked<Person>()
```

<!---END-->

> `castUnchecked` does not ensure that `T` matches the dataframe's columns. Accessing a missing column or using
> a column with an incompatible type through an extension property of `T` may throw an exception.
> {style="note"}

Here `Person` declares an `age` column, but the dataframe has none, so accessing `age` throws an exception:

<!---FUN castExtensionPropertyFailsAtRuntime-->

```kotlin
val people = dataFrameOf("name" to columnOf("Alice")).castUnchecked<Person>()
// Throws IllegalStateException because there is no age column.
people.age
```

<!---END-->

## `castTo`

Infers the target schema marker `T` from another typed dataframe or from a function returning one.
Returns the receiver as `DataFrame<T>`, checking its schema by default.
This is useful for reusing a schema generated by the compiler plugin,
especially when its type cannot be named explicitly in `cast<T>()`.

**API reference**: [`castTo`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/cast-to.html)

```text
df.castTo(schemaFrom: DataFrame<T>, verify: Boolean = true): DataFrame<T>
df.castTo(schemaFrom: Function<DataFrame<T>>, verify: Boolean = true): DataFrame<T>
```

The `DataFrame` overload infers `T` from the type argument of `schemaFrom`.
It does not copy or inspect that dataframe's columns or values to choose the target schema.
The function overload infers `T` from the function's return type; the function is not invoked
and can take arguments.

Both overloads follow the verification rules described above for `DataFrame.cast`.
Passing `verify = false` skips the check.

With the compiler plugin, the schema marker of a dataframe can be a local type generated by the plugin.
Such a type cannot be referred to directly, for example as a type argument of `cast<T>()`.
`castTo` infers it from a dataframe or a function that has this type instead.

### Parameters {id="parameters_castto"}

* `schemaFrom: DataFrame<T> | schemaFrom: Function<DataFrame<T>>` — a typed dataframe,
  or a function returning one, whose type supplies the target schema marker `T`.
* `verify: Boolean = true` — whether to check the receiver against `T`.

### Examples {id="examples_castto"}

Reuse the schema marker of another dataframe. The result contains `Alice` and `Bob`, the values from `df`,
while `schemaSource` supplies the type `Person`:

<!---FUN castToDataFrame-->

```kotlin
val schemaSource = dataFrameOf(
    "name" to columnOf("Charlie"),
    "age" to columnOf(30),
).cast<Person>(verify = true)
df.castTo(schemaFrom = schemaSource, verify = true)
```

<!---END-->

With the compiler plugin, a dataframe's schema marker can be inferred directly from its creation:

<!---FUN castToInferredSchema-->

```kotlin
val schemaSource = dataFrameOf(
    "name" to columnOf("Charlie"),
    "age" to columnOf(30),
)
df.castTo(schemaFrom = schemaSource)
```

<!---END-->

A function returning a typed dataframe can supply the schema too. There is no need to create a sample dataframe
by calling the function:

<!---FUN castToFunction-->

```kotlin
fun createPeopleDf(age: Int) =
    dataFrameOf(
        "age" to columnOf(age),
        "name" to columnOf("Sample"),
    )

df.castTo(schemaFrom = ::createPeopleDf, verify = true)
```

<!---END-->

For example, deserialization returns `DataFrame<*>`, so the schema marker of the original dataframe is lost.
Here `convert` returns a dataframe whose schema marker is a local type generated by the compiler plugin.
`deserializeAndUse` cannot name that type in `cast<T>()` and has no dataframe of that type to pass to `castTo`,
but `castTo(schemaFrom = ::convert)` restores the marker from the return type of `convert`,
so the columns can be accessed through extension properties again:

<!---FUN CastToLocalSchema-->

```kotlin
// parse listOf("b:1:abc", "c:2:bca")
// Return type: DataFrame<plugin generated local type>
private fun convert(data: List<String>) =
    data.map { it.split(":") }.toDataFrame {
        "part1" from { it[0] }
        "part2" from { it[1].toInt() }
        "part3" from { it[2] }
    }

fun serialize(data: List<String>): String = convert(data).toJson()

fun deserializeAndUse(json: String): DataColumn<String> {
    val df = DataFrame.readJsonStr(json).castTo(schemaFrom = ::convert)
    // Possible to use properties
    return df.part1
}
```

<!---END-->

## `cast` / `castUnchecked` on `DataRow`

Changes the schema type argument of a [`DataRow`](DataRow.md).
The result keeps the original row index and existing values.

```text
row.cast<T>(verify: Boolean = true): DataRow<T>
row.castUnchecked<T>(): DataRow<T>
```

`cast<T>()` checks the containing dataframe using the `DataFrame.cast` rules by default.
Pass `verify = false` or use `castUnchecked<T>()` to skip verification.
The row index and existing values are preserved in every mode.

### Parameters {id="parameters_row"}

* `T` — the target schema marker.
* `verify: Boolean = true` — whether to check the containing dataframe against `T`.
  An incompatible schema causes an `IllegalArgumentException`. Pass `false` to skip verification.

### Examples {id="examples_row"}

Check the containing dataframe's schema and return the corresponding typed row:

<!---FUN rowCast-->

```kotlin
df[0].cast<Person>()
```

<inline-frame src="./resources/rowCast.html" width="100%" height="500px"></inline-frame>

<!---END-->

## `cast` on `DataColumn`

Changes the value type argument of a [`DataColumn`](DataColumn.md) or a `ValueColumn`.
Returns the same column instance. Runtime column types and stored values are not checked or converted.

```text
col.cast<T>(): DataColumn<T>
valueCol.cast<T>(): ValueColumn<T>
```

`T` is the value type to use at compile time. These overloads have no `verify` parameter and do not check whether
the specified type matches the actual column values.

### Examples {id="examples_column"}

Assign the known value type to a column:

<!---FUN dataColumnCast-->

```kotlin
df["age"].cast<Int>()
```

<inline-frame src="./resources/dataColumnCast.html" width="100%" height="500px"></inline-frame>

<!---END-->

The `ValueColumn` overload keeps that more specific column kind:

<!---FUN valueColumnCast-->

```kotlin
df["age"].asValueColumn().cast<Int>()
```

<!---END-->

## `castFrameColumn`

Changes the schema type argument of a `FrameColumn`, returning the same column instance.
The nested dataframes are not checked against `T`, even if they have different schemas.
Their columns, values, and schemas remain unchanged.

```text
frameCol.castFrameColumn<T>(): FrameColumn<T>
```

`T` is the schema marker to use at compile time for the dataframes stored in the column.
There is no `verify` parameter, and the operation does not check whether `T` matches their actual schemas.

### Examples {id="examples_framecolumn"}

<!---FUN frameColumnCast-->

```kotlin
columnOf(df).named("people").castFrameColumn<Person>()
```

<inline-frame src="./resources/frameColumnCast.html" width="100%" height="500px"></inline-frame>

<!---END-->

## `cast` / `castUnchecked` on `ColumnGroup`

Changes the schema type argument of a [`ColumnGroup`](DataColumn.md#columngroup), returning the same column group
instance. Its nested columns, their runtime types, and their structure are preserved.

```text
colGroup.cast<T>(verify: Boolean = true): ColumnGroup<T>
colGroup.castUnchecked<T>(): ColumnGroup<T>
```

`cast<T>()` checks the nested columns against `T` by default, using the `DataFrame.cast` compatibility rules.
An incompatible schema causes an `IllegalArgumentException`. Pass `verify = false` or use `castUnchecked<T>()`
to skip verification. Neither form converts values or changes the nested columns.

### Parameters {id="parameters_columngroup"}

* `T` — the target schema marker for the nested columns.
* `verify: Boolean = true` — whether to check the nested columns against `T`.

### Examples {id="examples_columngroup"}

Check the nested columns against the `Person` schema:

<!---FUN columnGroupCast-->

```kotlin
df.asColumnGroup().cast<Person>()
```

<!---END-->

## `cast` on column references and resolvers

The overloads for column references, accessors, and [column resolvers](ColumnSelectors.md#column-resolvers)
return the same object with a different type argument. They do not check or convert column values.

```text
colWithPath.cast<T>(): ColumnWithPath<T>
colAccessor.cast<T>(): ColumnAccessor<T>
columnSet.cast<C>(): ColumnSet<C>
resolver.cast<C>(): ColumnsResolver<C>
singleCol.cast<C>(): SingleColumn<C>
colReference.cast<C>(): ColumnReference<C>
```

The type argument is the value type assigned at compile time to the referenced or resolved column or columns.
For resolvers that return several columns, it is their common value type. None of these overloads has a `verify`
parameter, and they do not check whether the specified type matches the actual columns. Runtime types are not changed.

## `cast` / `castUnchecked` on `GroupBy`

Changes the schema type arguments of a [`GroupBy`](groupBy.md), returning the same grouping object.
The key columns, groups, and values are not modified or converted.

```text
groupBy.cast<T, G>(verify: Boolean = true): GroupBy<T, G>
groupBy.castUnchecked<T, G>(): GroupBy<T, G>
```

`cast<T, G>()` checks the key columns against `T` and every group against `G` by default, using the `DataFrame.cast`
compatibility rules.
An incompatible key or group schema causes an `IllegalArgumentException`.
Pass `verify = false` or use `castUnchecked<T, G>()` to skip verification.

### Parameters {id="parameters_groupby"}

* `T` — the target schema marker for the grouping keys only.
* `G` — the target schema marker for every group.
* `verify: Boolean = true` — whether to check the keys against `T` and every group against `G`.

### Examples {id="examples_groupby"}

The key schema is declared as follows:

<!---FUN AgeKeySchema-->

```kotlin
@DataSchema
data class AgeKey(val age: Int)
```

<!---END-->

Use `AgeKey` for the grouping keys and `Person` for the grouped rows:

<!---FUN groupByCast-->

```kotlin
df.groupBy("age").cast<AgeKey, Person>()
```

<inline-frame src="./resources/groupByCast.html" width="100%" height="500px"></inline-frame>

<!---END-->
