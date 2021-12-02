[//]: # (title: Access APIs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

By nature data frames are dynamic objects, column labels depend on input source and also new columns could be added or deleted while wrangling. Kotlin in contrast is a statically typed language and all types are defined and verified ahead of execution. That's why creating flexible, handy and at the same time, safe API to a data frame is a tricky thing.

In `Kotlin Dataframe` we provide four different ways to access data, and while they are essentially different, they look pretty similar in data wrangling DSL.

## List of Access APIs
Here's a list of all API's in the order of increasing their safeness.

* [**String API**](stringApi.md) <br/>
  Columns accessed by `string` representing their name. Type-checking is on runtime, name-checking is also on runtime.

* [**Column Accessors API**](columnAccessorsApi.md) <br />
  Every column has a descriptor, a variable that representing its name and type.

* [**`KProperty` Accessors API**](KPropertiesApi.md) <br />
  Columns accessed by [`KProperty`](https://kotlinlang.org/docs/reflection.html#property-references) of some class. The name and type of column should match the name and type of property

* [**Extension properties API**](extensionPropertiesApi.md)
  Extension access properties are generating based on dataframe schema. Name and type of properties infers from name and type of corresponding columns.

## Example
Here's an example of how the same operations can be performed via different access APIs

<note>
In the most of the code snippets in this documentation there's a tab selector that allows switching across access APIs
</note>

<tabs>
    <tab title = "Generated Properties">
        
<!---FUN extensionProperties1-->

```kotlin
val df = DataFrame.read("titanic.csv")
```

<!---END-->

<!---FUN extensionProperties2-->

```kotlin
df.add("lastName") { name.split(",").last() }
    .dropNulls { age }
    .filter { survived && home.endsWith("NY") && age in 10..20 }
```

<!---END-->

    </tab>
    <tab title="Strings">

<!---FUN strings-->

```kotlin
DataFrame.read("titanic.csv")
    .add("lastName") { "name"<String>().split(",").last() }
    .dropNulls("age")
    .filter { "survived"<Boolean>() && "home"<String>().endsWith("NY") && "age"<Int>() in 10..20 }
```

<!---END-->

    </tab>
    <tab title="Accessors">
        
<!---FUN accessors3-->

```kotlin
val survived by column<Boolean>()
val home by column<String>()
val age by column<Int?>()
val name by column<String>()
val lastName by column<String>()

DataFrame.read("titanic.csv")
    .add(lastName) { name().split(",").last() }
    .dropNulls { age }
    .filter { survived() && home().endsWith("NY") && age()!! in 10..20 }
```

<!---END-->

    </tab>
    <tab title = "KProperties">

<!---FUN kproperties1-->

```kotlin
data class Passenger(val survived: Boolean, val home: String, val age: Int, val lastName: String)

val passengers = DataFrame.read("titanic.csv")
    .add(Passenger::lastName) { "name"<String>().split(",").last() }
    .dropNulls(Passenger::age)
    .filter { it[Passenger::survived] && it[Passenger::home].endsWith("NY") && it[Passenger::age] in 10..20 }
    .toListOf<Passenger>()
```

<!---END-->

    </tab>
</tabs>

# Comparing of APIs
[String API](stringApi.md) is the simplest one and the most unsafe of all. The main advantage of it is that it can be used at any time, including accessing new columns in chain calls. So we can write something like:
```kotlin
df.add("weight") { ... } // adding a new column named `weight`, calculated by some expression
  .sortBy("weight") // sorting data frame rows by its value
```
So we don't need to interrupt a method chain and declare a column accessor or generate new properties.

In contrast, generated [extension properties](extensionPropertiesApi.md) are the most convenient and safe API. Using it you can be always sure that you work with correct data and types. But its bottleneck — the moment of generation. To get new extension properties you have to run a cell in a notebook, which could lead to unnecessary variable declarations. Currently, we are working on compiler a plugin that generates these properties on the fly while user typing.

[Column Accessors API](columnAccessorsApi.md) is a kind of trade-off between safeness and ahead of the execution type declaration. It was designed to write code in IDE without notebook experience. It provides type-safe access to columns but doesn't ensure that the columns really exist in a particular dataframe.

[`KProperty` based API](KPropertiesApi.md) is useful when you have already declared classed in application business logic with fields that correspond columns of dataframe.

<table>
    <tr>
        <td> API </td>
        <td> Type-checking </td>
        <td> Column names checking </td>
        <td> Column existence checking </td>
    </tr>
    <tr>
        <td> Strings </td>
        <td> Runtime </td>
        <td> Runtime </td>
        <td> Runtime </td>
    </tr>
    <tr>
        <td> Column Accessors </td>
        <td> Compile-time </td>
        <td> Compile-time </td>
        <td> Runtime </td>
    </tr>
    <tr>
        <td> `KProperty` Accessors </td>
        <td> Compile-time </td>
        <td> Compile-time </td>
        <td> Runtime </td>
    </tr>
    <tr>
        <td> Extension Properties Accessors </td>
        <td> Generation-time </td>
        <td> Generation-time </td>
        <td> Generation-time </td>
    </tr>
</table>
