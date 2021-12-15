[//]: # (title: ungroup)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces `ColumnGroup` with its nested columns. 

```text
ungroup { columns }
```

**Reverse operation:** [`group`](group.md)

See [column selectors](ColumnSelectors.md)

<!---FUN ungroup-->

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.ungroup { name }
```

<!---END-->
