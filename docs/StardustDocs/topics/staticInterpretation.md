# Static interpretation of DataFrame API

Plugin evaluates dataframe operations, given compile-time known arguments such as constant String, resolved types, property access calls.
It updates the return type of the function call to provide properties that match column names and types.
The goal is to reflect the result of operations you apply to dataframe in types and have convenient typed API

```kotlin
val weatherData = dataFrameOf(
    "time" to columnOf(0, 1, 2, 4, 5, 7, 8, 9),
    "temperature" to columnOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
    "humidity" to columnOf(0.5, 0.32, 0.11, 0.89, 0.68, 0.57, 0.56, 0.5)
)

weatherData.filter { temperature > 15.0 }.print()
```

## Schema info

The schema of DataFrame, as the compiler plugin sees it,
is displayed when you hover on an expression or variable:

![image.png](schema_info.png)

This is a way to tell what properties are available.
For expressions with several operations, you can see how DataFrame changes at each step.

## Visibility of the generated code

Generated code itself is very similar to @DataSchema declarations in nature.
Take this expression as an example:

```kotlin
fun main() {
    val df: /* DataFrame<DataFrameOf_39> */ = dataFrameOf("col" to columnOf(42))
}
```

It produces two additional local classes:

```kotlin
// Represents data schema
class DataFrameOf_39 {
    val a: Int
}

// Injected to implicit receiver scope of `main` function
class Scope {
    val DataRow<DataFrameOf_39>.a: Int
    val ColumnsScope<DataFrameOf_39>.a: DataColumn<Int>
}
```

You can read about the code transformation pipeline in [more detail](https://youtrack.jetbrains.com/issue/KT-65859).

The fact that generated classes are anonymous local types limits their scope to the private scope of the file.
It means you can do this:

```kotlin
private fun create(i: Int) = dataFrameOf("number" to columnOf(i))
    .first()

fun main() {
    val row = create(42)
    println(row.number)
}
```

But you cannot refer to these classes from your code, have them appear in the explicit type of the variable or as parameter of a function.

## Scope of compiler plugin

Compiler plugin aims to cover all functions where the result of the operation depends only on input schema and arguments that can be resolved at compile time.
In the library, such functions are annotated with `@Refine` or `@Interpretable`.

There are functions that are not supported:
`pivot`, `parse`, `read`, `ColumnSelectionDsl.filter`, etc. — operations where the resulting schema depends on data, so it's out of the scope 
`gather`, `split`, `implode`, some CS DSL functions — they will be supported in the future release

In Gradle projects it means that sometimes you'd need to provide [data schema](dataSchema.md) or fall back to String API.

In Kotlin Notebook, the compiler plugin complements the built-in code generator that updates types or variables after cell execution.

```kotlin
val df = DataFrame.read("...")
```

In the next cell you can add, convert, remove, aggregate columns and expect that schema will be updated accordingly, 
without having to split your pipeline into multiple steps and trigger notebook code generation.


