[//]: # (title: Iterating)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Iterate over rows:

<!---FUN forRows-->
<tabs>
<tab title="Properties">

```kotlin
for (row in df) {
    println(row.age)
}

df.forEach {
    println(it.age)
}

df.rows().forEach {
    println(it.age)
}
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()

for (row in df) {
    println(row[age])
}

df.forEach {
    println(it[age])
}

df.rows().forEach {
    println(it[age])
}
```

</tab>
<tab title="Strings">

```kotlin
for (row in df) {
    println(row["age"])
}

df.forEach {
    println(it["age"])
}

df.rows().forEach {
    println(it["age"])
}
```

</tab></tabs>
<!---END-->

Iterate over columns:

<!---FUN forColumn-->

```kotlin
df.columns().forEach {
    println(it.name())
}
```

<!---END-->

Iterate over cells:

<!---FUN forCells-->

```kotlin
// from top to bottom, then from left to right
df.values().forEach {
    println(it)
}

// from left to right, then from top to bottom
df.values(byRows = true).forEach {
    println(it)
}
```

<!---END-->
