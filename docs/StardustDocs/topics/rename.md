[//]: # (title: rename)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Renames one or several columns without changing its location in [`DataFrame`](DataFrame.md).

```kotlin
df.rename { columns }.into(name)
df.rename { columns }.into { nameExpression }

nameExpression = (DataColumn) -> String
```

<!---FUN rename-->
<tabs>
<tab title="Properties">

```kotlin
df.rename { name }.into("fullName")
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
df.rename { name }.into("fullName")
```

</tab>
<tab title="Strings">

```kotlin
df.rename("name").into("fullName")
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.rename.html"/>
<!---END-->


<!---FUN renameExpression-->
<tabs>
<tab title="Properties">

```kotlin

```

</tab>
<tab title="Accessors">

```kotlin

```

</tab>
<tab title="Strings">

```kotlin

```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.renameExpression.html"/>
<!---END-->
