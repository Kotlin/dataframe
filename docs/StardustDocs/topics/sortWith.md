[//]: # (title: sortWith)

Sorts `DataFrame` with comparator
```kotlin
val comparator = Comparator { row1, rpw2 -> row1.age.compareTo(row2.age) }
df.sortWith(comparator)

df.sortWith { row1, row2 -> when {
       row1.age < row2.age -> -1
       row1.age > row2.age -> 1
       else -> row1.name.compareTo(row2.name)
    } 
}
```
