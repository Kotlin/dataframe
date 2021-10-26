[//]: # (title: Indexing)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

<!---FUN getCell-->
<tabs>
<tab title="Properties">

```kotlin
df.age[1]
df[1].age
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<String>()

df[age][1]
df[1][age]
```

</tab>
<tab title="Strings">

```kotlin
df["age"][1]
df[1]["age"]
```

</tab></tabs>
<!---END-->
