[//]: # (title: Access APIs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

By nature, data frames are dynamic objects;
column labels depend on the input source and new columns can be added
or deleted while wrangling.
Kotlin, in contrast, is a statically typed language where all types are defined and verified
ahead of execution.

That's why creating a flexible, handy, and, at the same time, safe API to a data frame is tricky.

In the Kotlin DataFrame library, we provide two different ways to access columns

## List of Access APIs

Here's a list of all APIs in order of increasing safety.

* [**String API**](stringApi.md) <br/>
  Columns are accessed by `string` representing their name. Type-checking is done at runtime, name-checking too.

* [**Extension Properties API**](extensionPropertiesApi.md) <br/>
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

<tab title = "Extension Properties API">

<!---FUN extensionProperties1-->

```kotlin
val df /* : AnyFrame */ = DataFrame.read("titanic.csv")
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

The `titanic.csv` file can be found [here](https://github.com/Kotlin/dataframe/blob/master/data/titanic.csv).

# Comparing APIs

The [String API](stringApi.md) is the simplest and unsafest of them all. The main advantage of it is that it can be
used at any time, including when accessing new columns in chain calls. So we can write something like:

```kotlin
df.add("weight") { ... } // add a new column `weight`, calculated by some expression
    .sortBy("weight") // sorting dataframe rows by its value
```

In contrast, generated [extension properties](extensionPropertiesApi.md) form the most convenient and the safest API. 
Using them, you can always be sure that you work with correct data and types.
However, there's a bottleneck at the moment of generation.
To get new extension properties, you have to run a cell in a notebook,
which could lead to unnecessary variable declarations.
Currently, we are working on a compiler plugin that generates these properties on the fly while typing!

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
        <td> Extension Properties API </td>
        <td> Generation-time </td>
        <td> Generation-time </td>
        <td> Generation-time </td>
    </tr>
</table>
