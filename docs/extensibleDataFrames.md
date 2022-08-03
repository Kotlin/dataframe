# Goal

Long story short, we aim to make this code compile

```kotlin
@DataSchema
interface Schema {
    val year: Int
}

fun consumeInt(i: Int) = i * 2

val df = DataFrame.readJson("...").cast<Schema>()
val df1 = df.add("age") { 2022 - year }
df1.age
consumeInt(df1[0].age)
```

Arguments of operations such as add, convert, explode, join, insert, rename, ... are often known at compile time, and we can access them.
We also need:
1. a way to evaluate result of a given function call with arguments known at compile time
2. a way to generate some type safe accessors from this result after dataframe's function calls

# General mechanism

## Codegen

With new K2 compiler API it's possible to inject implicit receivers after function calls.
It could be roughly translated to the following code:

```kotlin
val df1 = df.add("age") { 2022 - year }/*.cast<Cars1>()*/
// inject implicit receiver
with(object {
    val ColumnsContainer<Cars>.age: DataColumn<Int> get() = this["age"] as DataColumn<Int>
    val DataRow<Cars>.age: Int get() = this["age"] as Int
}) {
    df1.age
    consumeInt(df1[0].age)
}
```

Note that all that is transparent, and you'll only see 

```kotlin
val df1 = df.add("age") { 2022 - year }
df1.age
consumeInt(df1[0].age)
```

So with this API it's possible to generate typed API.

## Evaluation

One possible way to 
