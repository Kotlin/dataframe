# String API

The String API is the most basic and straightforward API
for selecting columns in the Kotlin DataFrame [operations](operations.md).

Column names as `String` values can be provided directly as function arguments:

```kotlin
// Select a sub-dataframe with the "name" and "age" columns
df.select("name", "age")
```

They can also be used inside the [Columns Selection DSL](ColumnSelectors.md):

```kotlin
// Select the "firstName" subcolumn of the "name" column group
// and the "age" column
df.select { "name"["firstName"] and "age" }
// or
df.select { colGroup("name").col("firstName") and col("age") }
```

Inside the Columns Selection DSL, you can also specify column types  
(this may throw a runtime exception if the specified type is incorrect):

```kotlin
// Calculate the mean value of the "age" column
df.mean { col<Int>("age") }
```

The String API can also be used in any operation with a [row expression](DataRow.md#row-expressions).
You can access row values in specific columns by invoking `String` values with their names:

```kotlin
// Add a new "fullName" column by combining
// the "firstName" and "lastName" column values
df.add("fullName") { 
    "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() 
}

// Filter rows where the "age" column value is greater than or equal to 18
df.filter { "age"<Int>() >= 18 }
```

The String API can be a good starting point for learning the library.
However, we highly recommend using the
[**Extension Properties API**](extensionPropertiesApi.md) instead —
it is more powerful, name- and type-safe, and more concise.
