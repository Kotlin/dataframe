[//]: # (title: sortBy)

Sorts `DataFrame` by one or several columns.
Several sort columns can be combined by `and` operator.
By default, columns are sorted in ascending order with null values going first.
To change column sort order to descending use `.desc` modifier.
To get `null` values in the end of the order use `.nullsLast` modifier

String API:
```kotlin
df.sortBy("age", "name")
```
Column accessors API:
```kotlin
val age by column<Int>()
val name by column<String>()
df.sortBy { age and name.desc }
df.sortBy { name.nullsLast and age.desc }
```
Extension properties API:
```kotlin
df.sortBy { age }
df.sortBy { age and name.desc }
df.sortBy { name.nullsLast and age.desc }
```
To apply descending order to all columns use `sortByDesc` function
```kotlin
df.sortByDesc { name and age}
```
To sort by a continuous range of columns use `cols` function
```kotlin
df.sortBy { cols(0..2) }
```

## sortByDesc

Similar to [sortBy](sortBy.md), but uses reversed sort direction

## sortWith

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
