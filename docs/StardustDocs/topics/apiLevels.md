[//]: # (title: Access APIs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

By nature data frames are dynamic objects, column labels depend on the input source and also new columns could be added
or deleted while wrangling. Kotlin, in contrast, is a statically typed language and all types are defined and verified
ahead of execution. That's why creating a flexible, handy, and, at the same time, safe API to a data frame is tricky.

In the Kotlin DataFrame library we provide four different ways to access columns, and, while they are essentially different, they
look pretty similar in the data wrangling DSL.

## List of Access APIs

Here's a list of all APIs in order of increasing safety.

* [**String API**](stringApi.md) <br/>
  Columns are accessed by `string` representing their name. Type-checking is done at runtime, name-checking too.

* [**Column Accessors API**](columnAccessorsApi.md) <br/>
  Every column has a descriptor; a variable that represents its name and type.

* [**KProperties API**](KPropertiesApi.md) <br/>
  Columns accessed by the [`KProperty`](https://kotlinlang.org/docs/reflection.html#property-references) of some class.
  The name and type of column should match the name and type of property, respectively.

* [**Extension Properties API**](extensionPropertiesApi.md)
  Extension access properties are generated based on the dataframe schema. The name and type of properties are inferred
  from the name and type of the corresponding columns.

## Example

Here's an example of how the same operations can be performed via different Access APIs:

<note>
In the most of the code snippets in this documentation there's a tab selector that allows switching across Access APIs.
</note>

<tabs>

<tab title="String API">

<!---FUN strings-->

```kotlin
DataFrame.read("titanic.csv")
    .add("lastName") { "name"<String>().split(",").last() }
    .dropNulls("age")
    .filter {
        "survived"<Boolean>() &&
            "home"<String>().endsWith("NY") &&
            "age"<Int>() in 10..20
    }
```

<!---END-->

</tab>

<tab title="Column Accessors API">

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

<tab title = "KProperties API">

<!---FUN kproperties1-->

```kotlin
data class Passenger(
    val survived: Boolean,
    val home: String,
    val age: Int,
    val lastName: String
)

val passengers = DataFrame.read("titanic.csv")
    .add(Passenger::lastName) { "name"<String>().split(",").last() }
    .dropNulls(Passenger::age)
    .filter {
        it[Passenger::survived] &&
            it[Passenger::home].endsWith("NY") &&
            it[Passenger::age] in 10..20
    }
    .toListOf<Passenger>()
```

<!---END-->

</tab>

<tab title = "Extension Properties API">

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

</tabs>

# Comparing the APIs

The [String API](stringApi.md) is the simplest and unsafest of them all. The main advantage of it is that it can be
used at any time, including when accessing new columns in chain calls. So we can write something like:

```kotlin
df.add("weight") { ... } // add a new column `weight`, calculated by some expression
    .sortBy("weight") // sorting dataframe rows by its value
```

We don't need to interrupt a function call chain and declare a column accessor or generate new properties.

In contrast, generated [extension properties](extensionPropertiesApi.md) are the most convenient and the safest API. 
Using it, you can always be sure that you work with correct data and types. 
But its bottleneck is the moment of generation. 
To get new extension properties you have to run a cell in a notebook, 
which could lead to unnecessary variable declarations.
Currently, we are working on compiler a plugin that generates these properties on the fly while typing!

The [Column Accessors API](columnAccessorsApi.md) is a kind of trade-off between safety and needs to be written ahead of
the execution type declaration. It was designed to better be able to write code in an IDE without a notebook experience. 
It provides type-safe access to columns but doesn't ensure that the columns really exist in a particular data frame.

The [KProperties API](KPropertiesApi.md) is useful when you already have declared classed in your application business
logic with fields that correspond columns of a data frame.

<table>
    <tr>
        <td> API </td>
        <td> Type-checking </td>
        <td> Column names checking </td>
        <td> Column existence checking </td>
    </tr>
    <tr>
        <td> String API </td>
        <td> Runtime </td>
        <td> Runtime </td>
        <td> Runtime </td>
    </tr>
    <tr>
        <td> Column Accessors API </td>
        <td> Compile-time </td>
        <td> Compile-time </td>
        <td> Runtime </td>
    </tr>
    <tr>
        <td> KProperties API </td>
        <td> Compile-time </td>
        <td> Compile-time </td>
        <td> Runtime </td>
    </tr>
    <tr>
        <td> Extension Properties API </td>
        <td> Generation-time </td>
        <td> Generation-time </td>
        <td> Generation-time </td>
    </tr>
</table>
