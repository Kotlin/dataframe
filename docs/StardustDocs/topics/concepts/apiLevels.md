[//]: # (title: Access APIs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.concepts.AccessApis-->

By nature, dataframes are dynamic objects;
column labels depend on the input source, and new columns can be added
or deleted while wrangling.
Kotlin, in contrast, is a statically typed language where all types are defined and verified
ahead of execution.

That's why creating a flexible, handy, and, at the same time, 
safe API to access dataframe columns is tricky.

In the Kotlin DataFrame library, we provide two different ways to access columns — 
the [](StringApi.md) and the [](extensionPropertiesApi.md).

## String API

In the [**String API**](StringApi.md), columns are accessed by a `String` representing their name.
Type-checking is done at runtime, name-checking too.

The most basic String API usage is quite intuitive and looks very similar 
to any other library working with dataframes:

<!---FUN stringApiExample1-->

```kotlin
// Get the "fullName" column
df["fullName"]
// Rename the "fullName" column into "name"
df.rename("fullName").into("name")
```

<!---END-->

Also, you can create [*String Column Accessors*](StringApi.md#string-column-accessors)
that can be used inside the [Columns Selection DSL](ColumnSelectors.md)
and [row expressions](DataRow.md#row-expressions) using special methods:

<!---FUN stringApiExample2-->

```kotlin
// Select the "firstName" column from the "fullName" column group
// and the "age" column
df.select { "fullName"["firstName"]<String>() and "age"<Int>() }
// Takes only rows where the
// "fullName"->"firstName" column value is equal to "Alice"
// and "age" column value is greater or equal to 18
df.filter {
    "fullName"["firstName"]<String>() == "Alice" && "age"<Int>() >= 18
}
```

<!---END-->

Though the String API is the simplest of the two and doesn't require any additional setup, 
it lacks name- and type-safety; if column names or cast types are incorrect,
a runtime exception will be thrown.

## Extension Properties API

The [**Extension Properties API**](extensionPropertiesApi.md) solves the 
main problems of the String API - name- and type-safety;

This is achieved by generating extension properties for **`DataFrame<T>`**
(as well as for other related interfaces such as **`DataRow`** and others) 
based on its [data schema](schemas.md), which is represented by the type parameter **`T`**.  
This requires the [*Kotlin DataFrame Compiler Plugin*](Compiler-Plugin.md),
or alternatively, usage within the [*Kotlin Notebook*](SetupKotlinNotebook.md).

> Extension Properties behavior differs in Kotlin Notebook 
> and in the Kotlin DataFrame Compiler Plugin. [Read about it here](extensionPropertiesApi.md).

The same operations as in the String API can be performed via extension properties concisely
and completely name- and typesafe:

<!---FUN extensionPropertiesApiExample-->

```kotlin
// Get "fullName" column
df.fullName
// Rename "fullName" column into "name"
df.rename { fullName }.into("name")
// Select the "firstName" column from the "fullName" column group
// and the "age" column
df.select { fullName.firstName and age }
// Takes only rows where the
// "fullName"->"firstName" column value is equal to "Alice"
// and "age" column value is greater or equal to 18
df.filter {
    fullName.firstName == "Alice" && age >= 18
}
```

<!---END-->

## Comparing APIs

To better understand the distinction between the two Access APIs, 
let's look at a concise example of the DataFrame operations chain, 
presented using both APIs.

<note>
For most of the code snippets in this documentation,
there's a tab selector that allows switching between Access APIs.
</note>

<tabs>

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

</tabs>

> The `titanic.csv` file can be found [here](https://github.com/Kotlin/dataframe/blob/master/data/titanic.csv).

The Extension Properties API provides column names and -types at compile-time, 
while the String API could be used with incorrect column names or types and break in runtime.

Additionally, when using [IntelliJ IDEA](https://www.jetbrains.com/idea/) with
[Gradle](SetupGradle.md#kotlin-dataframe-compiler-plugin) or
[Maven](SetupMaven.md#kotlin-dataframe-compiler-plugin) projects that have the 
[Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) enabled, as well as in
[Kotlin Notebook](SetupKotlinNotebook.md),
code completion fully supports extension properties.

![Code Completion](codeCompletion.png)

However, note that after operations where the resulting columns cannot be inferred 
by the Compiler Plugin (for example, [`pivot`](pivot.md)), extension properties
cannot be inferred automatically either. In such cases, you can use [`cast`](cast.md)
to define a new data schema or switch to the String API.

<table>
    <tr>
        <td> API </td>
        <td> Type-checking </td>
        <td> Column names checking </td>
        <td> Column existence checking </td>
        <td> Code completion support </td>
    </tr>
    <tr>
        <td> String API </td>
        <td> Runtime </td>
        <td> Runtime </td>
        <td> Runtime </td>
        <td> No </td>
    </tr>
    <tr>
        <td> Extension Properties API </td>
        <td> Compile-time </td>
        <td> Compile-time </td>
        <td> Compile-time </td>
        <td> Yes </td>
    </tr>
</table>
