[//]: # (title: Schema inheritance)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

In order to reduce amount of generated code, previously generated [`DataSchema`](schema.md) interfaces are reused and only new
properties are introduced

Let's filter out all `null` values from `age` column and add one more column of type `Boolean`:

```kotlin
val filtered = df.filter { age != null }.add("isAdult") { age!! > 18 }
```

New schema interface for `filtered` variable will be derived from previously generated `DataFrameType`:

```kotlin
@DataSchema
interface DataFrameType1 : DataFrameType
```

Extension properties for data access are generated only for new and overridden members of `DataFrameType1` interface:

```kotlin
val ColumnsContainer<DataFrameType1>.age: DataColumn<Int> get() = this["age"] as DataColumn<Int>
val DataRow<DataFrameType1>.age: Int get() = this["age"] as Int
val ColumnsContainer<DataFrameType1>.isAdult: DataColumn<Boolean> get() = this["isAdult"] as DataColumn<Boolean>
val DataRow<DataFrameType1>.isAdult: String get() = this["isAdult"] as Boolean
```

Then variable `filtered` is cast to new interface:

```kotlin
val temp = filtered
```

```kotlin
val filtered = temp.cast<DataFrameType1>()
```
