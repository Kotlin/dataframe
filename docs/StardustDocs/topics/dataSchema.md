# @DataSchema declarations

`DataSchema` can be used as an argument for [cast](cast.md) and [convertTo](convertTo.md) functions. 
It provides typed data access for raw dataframes you read from I/O sources and serves as a starting point for the compiler plugin to derive schema changes.

Example 1:
```kotlin
@DataSchema
interface Person { 
  val firstName: String
}
```

Generated code:
```kotlin
val DataRow<Person>.firstName: Int = this["firstName"] as String
val ColumnsScope<Person>.firstName: DataColumn<Int> = this["firstName"] as DataColumn<String>
```

Example 2:
```kotlin
@DataSchema
interface Person {
    @ColumnName("first_name")
    val firstName: String
}
```

`ColumnName` annotation changes how generated extension properties pull the data from a dataframe:

Generated code:
```kotlin
val DataRow<Person>.firstName: Int = this["first_name"] as String
val ColumnsScope<Person>.firstName: DataColumn<Int> = this["first_name"] as DataColumn<String>
```

Generated extension properties are used to access values in `DataRow` and to access columns in `ColumnsScope`, which is either `DataFrame` or `ColumnSelectionDsl` 

`DataRow`:
```kotlin
val row = df[0]
row.firstName
```

```kotlin
df.filter { firstName.startsWith("L") }
df.add("newCol") { firstName }
```

`DataFrame`:
```kotlin
val col = df.firstName
val value = col[0]
```

`ColumnSelectionDsl`:

```kotlin
df.convert { firstName }.with { it.uppercase() }
df.select { firstName }
df.rename { firstName }.into("name")
```

## Data Class

DataSchema can be a top-level data class, in which case two additional API become available

```kotlin
@DataSchema
class WikiData(val name: String, val paradigms: List<String>)
```

1. `dataFrameOf` overload that creates a dataframe instance from objects

```kotlin
val languages = dataFrameOf(
    WikiData("Kotlin", listOf("object-oriented", "functional", "imperative")), 
    WikiData("Haskell", listOf("Purely functional")),
    WikiData("C", listOf("imperative")),
    WikiData("Pascal", listOf("imperative")),
    WikiData("Idris", listOf("functional")),
)
```

2. `append` overload that takes an object and appends it as a row

```kotlin
val ocaml = WikiData("OCaml", listOf("functional", "imperative", "modular", "object-oriented"))
val languages1 = languages.append(ocaml)
```

## Schemas for nested structures

Nested structure can be a JSON that you read from a file.

```json
[
    {
        "id": "1",
        "participants": [
            {
                "name": {
                    "firstName": "Alice",
                    "lastName": "Cooper"
                },
                "age": 15,
                "city": "London"
            },
            {
                "name": {
                    "firstName": "Bob",
                    "lastName": "Dylan"
                },
                "age": 45,
                "city": "Dubai"
            }
        ]
    },
    {
        "id": "2",
        "participants": [
            {
                "name": {
                    "firstName": "Charlie",
                    "lastName": "Daniels"
                },
                "age": 20,
                "city": "Moscow"
            },
            {
                "name": {
                    "firstName": "Charlie",
                    "lastName": "Chaplin"
                },
                "age": 40,
                "city": "Milan"
            }
        ]
    }
]
```

You get dataframe with this schema

```text
id: String
participants: *
    name:
        firstName: String
        lastName: String
    age: Int
    city: String
```

- `participants` is `FrameColumn`
- `name` is `ColumnGroup`

Here's the data schema that matches it:

```kotlin
@DataSchema
data class Group(
    val id: String, 
    val participants: List<Person>
)

@DataSchema
data class Person(
    val name: Name,
    val age: Int, 
    val city: String?
)

@DataSchema
data class Name(
    val firstName: String,
    val lastName: String,
)
```

```kotlin
val url = "https://raw.githubusercontent.com/Kotlin/dataframe/refs/heads/master/data/participants.json"
val df = DataFrame.readJson(url).cast<Group>()
```
