# String API

<web-summary>
Work with columns in Kotlin DataFrame using simple string-based selectors.
</web-summary>

<card-summary>
Use the String API in Kotlin DataFrame to select columns directly by name and build expressions with minimal setup.
</card-summary>

<link-summary>
An introduction to the Kotlin DataFrame String API for column selection.
</link-summary>

The String API is the most basic and straightforward way to select columns
in Kotlin DataFrame [operations](operations.md).

In String API overloads, column names are provided directly as `String` values
in function arguments:

```kotlin
// Select a sub-dataframe with the "name" and "age" columns
df.select("name", "age")
```

## Column Accessors

The String API can also be used inside the
[Columns Selection DSL](ColumnSelectors.md) and
[row expressions](DataRow.md#row-expressions)
via *column accessors*.

Column accessors allow you to access nested columns and combine them with the
[](extensionPropertiesApi.md) or with any other [CS DSL functions](ColumnSelectors.md#functions-overview).

They are created using `String` invocation.
In the Columns Selection DSL, they have the special type `ColumnAccessor`,
while in row expressions they resolve to concrete value types.

You can optionally specify the column type as a type argument of the invocation.
This is required for row expressions and for some operations with a column selection.
If the specified type does not match the actual column type,
a runtime exception may be thrown.

```kotlin
/* Column Selection DSL */

// Select the "firstName" subcolumn of the "name" column group
// and the "age" column
df.select { "name"["firstName"]() and "age"() }

// Calculate the mean value of the "age" column;
// specify the column type as an invocation type argument
df.mean { "age"<Int>() }

/* Row Expressions */

// Add a new "fullName" column by combining
// the "firstName" and "lastName" column values
df.add("fullName") {
    "name"["firstName"]<String>() + " " + "name"["lastName"]<String>()
}

// Filter rows where the "age" column value is greater than or equal to 18
df.filter { "age"<Int>() >= 18 }
```

The String API is a good starting point for learning the library
and understanding how column selection works.

However, for production code we strongly recommend using the
[**Extension Properties API**](extensionPropertiesApi.md) instead.
It is more concise, fully type-safe, and provides better IDE support.
