[//]: # (title: ungroup)

Replaces `ColumnGroup` with its nested columns. Reverse operation to [group](#group)
```kotlin
// fullName.firstName -> firstName
// fullName.lastName -> lastName
df.ungroup { fullName }
``` 
