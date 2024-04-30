[//]: # (title: Extension properties API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

When [`DataFrame`](DataFrame.md) is used within Jupyter/Kotlin Notebook or Datalore with the Kotlin Kernel,
something special happens:
After every cell execution, all new global variables of type DataFrame are analyzed and replaced
with a typed [`DataFrame`](DataFrame.md) wrapper along with auto-generated extension properties for data access.
For instance, say we run:

<!---FUN extensionProperties1-->

```kotlin
val df /* : AnyFrame */ = DataFrame.read("titanic.csv")
```

<!---END-->

TODO make df <dataFrame>


In normal Kotlin code, we would now have a variable of type [`AnyFrame` (=`DataFrame<*>`)](DataFrame.md)  that doesn't
have any
extension properties to access its columns. We would either have to define them manually or use the
[`@DataSchema`](schemas.md) annotation to [generate them](schemasGradle.md#configuration).

By contrast, after this cell is run in a notebook, the columns of the dataframe are used as a basis
to generate a hidden `@DataSchema interface TypeX`,
along with extension properties like `val DataFrame<TypeX>.age` etc.
Next, the `df` variable is shadowed by a new version cast to `DataFrame<TypeX>`.

As a result, now columns can be accessed directly on `df`!

<!---FUN extensionProperties2-->

```kotlin
df.add("lastName") { name.split(",").last() }
    .dropNulls { age }
    .filter { survived && home.endsWith("NY") && age in 10..20 }
```

<!---END-->

The `titanic.csv` file could be found [here](https://github.com/Kotlin/dataframe/blob/master/data/titanic.csv).

Extension properties can be generated in IntelliJ IDEA using
the [Kotlin Dataframe Gradle plugin](schemasGradle.md#configuration).

<warning>
In notebooks generated properties won't appear and be updated until the cell has been executed.
It often means that you have to introduce new variable frequently to sync extension properties with actual schema.
</warning>
