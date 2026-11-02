# cast / castTo

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CastSamples-->

<web-summary>
Discover the `cast` and `castTo` operations for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `cast` and `castTo` operations for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `cast` and `castTo` operations for Kotlin DataFrame.
</link-summary>

## `cast`

Changes the schema type argument of a [`DataFrame`](DataFrame.md) to a schema marker `T`.
Returns a `DataFrame<T>`, optionally checking its columns against the target schema.
Existing values are not converted, and the input dataframe is not modified.

**API reference**: [`cast`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/cast.html)

```text
df.cast<T>(): DataFrame<T>
df.cast<T>(verify: Boolean = true): DataFrame<T>
```

Calling `cast<T>()` without arguments selects the unchecked overload. It returns the same dataframe with
only its schema type argument changed. Values and the runtime schema remain unchanged.
Passing `verify = false` has the same effect.

To check schema compatibility, explicitly pass `verify = true`. Verification checks column types and nested schemas
against `T`; column order does not matter. If schema verification fails, an `IllegalArgumentException` is thrown.
For example, a `String` column containing `"20"` cannot satisfy an `Int` column in the target schema.

Verification can also produce a dataframe with additional or different columns:

* Missing nullable [value columns](DataColumn.md#valuecolumn) are added and filled with `null`.
* A missing [`ColumnGroup`](DataColumn.md#columngroup) is added if its nested columns can be filled
  with `null` or empty dataframes.
* Missing [frame columns](DataColumn.md#framecolumn) are filled with empty dataframes of the target schema.
* Extra top-level columns are kept for a non-empty input.
* An input with no rows produces an empty dataframe with exactly the target schema,
  even if its original columns have different names or types. Extra columns are not kept in this case.

See also:

* [`convertTo`](convertTo.md) — converts column values to match a target schema.
* [Adjust schema](adjustSchema.md) — explains how to align a dataframe's schema type argument with its contents.
* [Data schemas](schemas.md) — schema markers and generated extension properties.

### Parameters {id="parameters_df"}

* `T` — the target schema marker, for example a class or interface annotated with
  [`@DataSchema`](dataSchema.md).
* `verify: Boolean = true` — whether to check column types and nested schemas against `T`.
  This parameter belongs to the overload that performs verification. The no-argument overload
  `cast<T>()` always skips verification.

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
data class Person(val name: String, val age: Int)
```

<!---END-->

Check the dataframe against this schema:

<!---FUN verifiedCast-->

```kotlin
df.cast<Person>(verify = true)
```

<!---END-->

The no-argument overload changes only the schema type argument without checks:

<!---FUN uncheckedCast-->

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

Extra top-level columns remain available after a verified cast of a non-empty dataframe:

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

`PersonWithCity` has the same properties as `Person` plus `city: String?`.
The missing nullable column is added with `null` values; the existing `name` and `age` values are preserved:

<!---FUN castMissingNullableColumn-->

```kotlin
df.cast<PersonWithCity>(verify = true)
```

<inline-frame src="./resources/castMissingNullableColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

For an empty input, verification creates the target columns and drops columns outside the target schema:

<!---FUN castEmptyDataFrame-->

```kotlin
// Input schema: extra: Boolean
dataFrameOf("extra" to columnOf(values = emptyArray<Boolean>()))
    .cast<Person>(verify = true)
    // Result schema: name: String, age: Int
```

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
It does not copy that dataframe's values or inspect its runtime schema to choose the target schema.
The function overload infers `T` from the function's return type; the function is not invoked
and can take arguments.

Both overloads follow the verification rules described above for `DataFrame.cast`.
Passing `verify = false` returns the same dataframe with only its schema type argument changed.

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

## `cast` on `DataRow`

Changes the schema type argument of a [`DataRow`](DataRow.md).
The result keeps the original row index and existing values.

```text
row.cast<T>(): DataRow<T>
row.cast<T>(verify: Boolean = true): DataRow<T>
```

The no-argument overload returns the same row without checking its schema.
The overload with `verify = true` checks the containing dataframe using the `DataFrame.cast` rules,
including how missing columns are filled. Existing values are not converted in either mode.

### Parameters {id="parameters_row"}

* `T` — the target schema marker.
* `verify: Boolean = true` — whether to check the containing dataframe against `T`.
  Pass `false` to skip verification. The no-argument overload is always unchecked.

### Examples {id="examples_row"}

Check the containing dataframe's schema and return the corresponding typed row:

<!---FUN rowCast-->

```kotlin
df[0].cast<Person>(verify = true)
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
The nested dataframes are not checked against `T`, even if their runtime schemas differ.
Their columns, values, and the frame column's runtime schema remain unchanged.

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

## `cast` on `ColumnGroup`

Changes the schema type argument of a `ColumnGroup`, returning the same column group instance.
Its nested columns, their runtime types, and their structure are preserved without verification.

```text
colGroup.cast<T>(): ColumnGroup<T>
```

`T` is the schema marker to use at compile time for the group. There is no `verify` parameter, and the operation
does not check whether `T` matches the group's actual nested schema.

### Examples {id="examples_columngroup"}

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

## `cast` on `GroupBy`

Changes the schema type arguments of a [`GroupBy`](groupBy.md), returning the same grouping object.
The key columns and groups are not checked or converted.

```text
groupBy.cast<T, G>(): GroupBy<T, G>
```

`T` is the schema marker for the keys, and `G` is the schema marker for the grouped rows.
There is no `verify` parameter, and the operation does not check whether these markers match the actual key and
group schemas.

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
