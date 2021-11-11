[//]: # (title: ungroup)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces `ColumnGroup` with its nested columns. 

Reverse operation to [group](group.md)

```kotlin
ungroup { columns }
```

See [column selectors](ColumnSelectors.md)

<!---FUN ungroup-->

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.ungroup { name }
```

<!---END-->
