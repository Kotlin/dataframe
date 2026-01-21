[//]: # (title: Access APIs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

By nature, dataframes are dynamic objects;
column labels depend on the input source, and new columns can be added
or deleted while wrangling.
Kotlin, in contrast, is a statically typed language where all types are defined and verified
ahead of execution.

That's why creating a flexible, handy, and, at the same time, safe API to dataframe
columns access is tricky.

In the Kotlin DataFrame library, we provide two different ways to access columns — 
the [](StringApi.md) and the [](extensionPropertiesApi.md).

## String API

In the [**String API**](StringApi.md), columns are accessed by `string` representing their name.
Type-checking is done at runtime, name-checking too.

The most basic String API usage is quite intuitive and looks very similar to any other library working with dataframe:

```kotlin
// Get "name" column
df["name"]
// Rename "name" column into "firstName"
df.rename("name").into("fullName")
```

Also, String API methods could be used inside the [Columns Selection DSL](ColumnSelectors.md):

```kotlin
// Select "firstName" and "lastName" columns from "name" column group
df.select { colGroup("name").select { 
    col<String>("firstName") and col<String>("lastName") } 
}
// Takes only rows where "age" column (the subcolumn of "info" column group) 
// is greater or equal to 18
df.filter { "info"["age"]<Int>() >= 18 }
```

Though the String API is the simple and doesn't require any additional setup, 
it lack name- and type-safety; if column names or casted type are incorrect,
the runtime error will be thrown.

## Extension Properties API

The [**Extension Properties API**](extensionPropertiesApi.md) solves the 
main problem of the String API - name- and type-safety;

This is achieved by generating extension properties for **`DataFrame<T>`** 
(as well as for other related interfaces such as **`DataRow`** and others) 
based on its [data schema](schemas.md), which is represented by the type parameter **`T`**.  
This requires the [*Kotlin DataFrame Compiler Plugin*](Compiler-Plugin.md), 
or alternatively, usage within the [*Kotlin Notebook*](SetupKotlinNotebook.md).

> Extension Properties behavior differs in the Kotlin Notebook 
> and in the Kotlin DataFrame Compiler Plugin. [Read about it here](extensionPropertiesApi.md).

The same operations as in the String API could be performed via extension properties briefly
and completely name- and typesafe:

```kotlin
// Get "name" column
df.name
// Rename "name" column into "firstName"
df.rename { name }.into("fullName")
// Select "firstName" and "lastName" columns from "name" column group
df.select { name.firstName and name.lastName }
// Takes only rows where "age" column (the subcolumn of "info" column group) 
// is greater or equal to 18; 
// for DataRow API extension properties has direct value types
df.filter { info.age >= 18 }
```

## Comparing APIs

To better understand the distinction between the two Access APIs, 
look at concise example of the DataFrame operations chain, 
presented using both APIs.

<note>
In the most of the code snippets in this documentation there's a tab selector that allows switching across Access APIs.
</note>

<tabs>

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

The Extension Properties API provides column names and types at compile-time, 
while the String API can be used with incorrect column names or types and breaks in runtime.

Also, when using in the [IntelliJ IDEA](https://www.jetbrains.com/idea/) in the 
[Gradle](SetupGradle.md#kotlin-dataframe-compiler-plugin) 
or [Maven](SetupMaven.md#kotlin-dataframe-compiler-plugin) projects 
with the [Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) enabled, or
in the [Kotlin Notebook](SetupKotlinNotebook.md), extensions properties are completely supported
in the code сompeltion!

![Code Completion](codeCompletion.png)

However, note that after operations in which resulting columns cannot be inferred 
by Compiler Plugin (for example, [`pivot`](pivot.md)),
extension properties can't be inferred automatically either. For such operations, 
you can use [`cast`](cast.md) to define a new data schema or use the String API. 

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
