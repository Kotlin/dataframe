[//]: # (title: DataSchema workflow in Jupyter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

After execution of cell

<!---FUN createDfNullable-->

```kotlin
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", null,
)
```

<!---END-->

the following actions take place:

1. Columns in `df` are analyzed to extract data schema
2. Empty interface with [`DataSchema`](schema.md) annotation is generated:

```kotlin
@DataSchema
interface DataFrameType
```

3. Extension properties for this [`DataSchema`](schema.md) are generated:

```kotlin
val ColumnsContainer<DataFrameType>.age: DataColumn<Int?> @JvmName("DataFrameType_age") get() = this["age"] as DataColumn<Int?>
val DataRow<DataFrameType>.age: Int? @JvmName("DataFrameType_age") get() = this["age"] as Int?
val ColumnsContainer<DataFrameType>.name: DataColumn<String> @JvmName("DataFrameType_name") get() = this["name"] as DataColumn<String>
val DataRow<DataFrameType>.name: String @JvmName("DataFrameType_name") get() = this["name"] as String
```

Every column produces two extension properties:

* Property for `ColumnsContainer<DataFrameType>` returns column
* Property for `DataRow<DataFrameType>` returns cell value

4. `df` variable is typed by schema interface:

```kotlin
val temp = df
```

```kotlin
val df = temp.cast<DataFrameType>()
```

> _Note, that object instance after casting remains the same. See [cast](cast.md).

To log all these additional code executions, use cell magic

```
%trackExecution -all
```
