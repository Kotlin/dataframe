[//]: # (title: String API)

String column names are the easiest way to access data in DataFrame:

```kotlin
val df = DataFrame.read("titanic.csv")

df.filter { it["survived"] as Boolean }.groupBy("city").max("age")
```

or using invoke operator:

```kotlin
df.filter { "survived"<Boolean>() }.groupBy("city").max("age")
```
<warning>
Note that if data frame doesn’t contain column with the string provided, or you try to cast to the wrong type it will lead to runtime exception.
</warning>
