[//]: # (title: Column selectors)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

`DataFrame` provides DSL for selecting arbitrary set of columns.

Column selectors are used in many operations:

<!---FUN columnSelectorsUsages-->

```kotlin
df.select { age and name }
df.fillNaNs { dfsOf<Double>() }.withZero()
df.remove { cols { it.hasNulls() } }
df.update { city }.notNull { it.lowercase() }
df.gather { numberCols() }.into("key", "value")
df.move { name.firstName and name.lastName }.after { city }
```

<!---END-->

**Select columns by name:**

<!---FUN columnSelectors-->
<tabs>
<tab title="Properties">

```kotlin
// by column name
df.select { it.name }
df.select { name }

// by column path
df.select { name.firstName }

// with a new name
df.select { name named "Full Name" }

// converted
df.select { name.firstName.map { it.lowercase() } }

// column arithmetics
df.select { 2021 - age }

// two columns
df.select { name and age }

// range of columns
df.select { name..age }

// all children of ColumnGroup
df.select { name.all() }

// dfs traversal of all children columns
df.select { name.allDfs() }
```

</tab>
<tab title="Accessors">

```kotlin
// by column name
val name by columnGroup()
df.select { it[name] }
df.select { name }

// by column path
val firstName by name.column<String>()
df.select { firstName }

// with a new name
df.select { name named "Full Name" }

// converted
df.select { firstName.map { it.lowercase() } }

// column arithmetics
val age by column<Int>()
df.select { 2021 - age }

// two columns
df.select { name and age }

// range of columns
df.select { name..age }

// all children of ColumnGroup
df.select { name.all() }

// dfs traversal of all children columns
df.select { name.allDfs() }
```

</tab>
<tab title="Strings">

```kotlin
// by column name
df.select { it["name"] }

// by column path
df.select { it["name"]["firstName"] }
df.select { "name"["firstName"] }

// with a new name
df.select { "name" named "Full Name" }

// converted
df.select { "name"["firstName"]<String>().map { it.uppercase() } }

// column arithmetics
df.select { 2021 - "age"<Int>() }

// two columns
df.select { "name" and "age" }

// by range of names
df.select { "name".."age" }

// all children of ColumnGroup
df.select { "name".all() }

// dfs traversal of all children columns
df.select { "name".allDfs() }
```

</tab></tabs>
<!---END-->

**Select columns by column index:**

<!---FUN columnsSelectorByIndices-->

```kotlin
// by index
df.select { col(2) }

// by several indices
df.select { cols(0, 1, 3) }

// by range of indices
df.select { cols(1..4) }
```

<!---END-->

**Other column selectors:**

<!---FUN columnSelectorsMisc-->

```kotlin
// by condition
df.select { cols { it.name.startsWith("year") } }

// by type
df.select { colsOf<String>() }
df.select { stringCols() }

// by type with condition
df.select { colsOf<String> { !it.hasNulls() } }
df.select { stringCols { !it.hasNulls() } }

// all top-level columns
df.select { all() }

// first/last n columns
df.select { take(2) }
df.select { takeLast(2) }

// all except first/last n columns
df.select { drop(2) }
df.select { dropLast(2) }

// dfs traversal of all columns, excluding ColumnGroups from result
df.select { allDfs() }

// dfs traversal of all columns, including ColumnGroups in result
df.select { allDfs(includeGroups = true) }

// dfs traversal with condition
df.select { dfs { it.name.contains(":") } }

// dfs traversal of columns of given type
df.select { dfsOf<String>() }

// all columns except given column set
df.select { except { colsOf<String>() } }

// union of column sets
df.select { take(2) and col(3) }
```

<!---END-->

**Modify the set of selected columns:**

<!---FUN columnSelectorsModifySet-->

```kotlin
// first/last n columns in column set
df.select { allDfs().take(3) }
df.select { allDfs().takeLast(3) }

// all except first/last n columns in column set
df.select { allDfs().drop(3) }
df.select { allDfs().dropLast(3) }

// filter column set by condition
df.select { allDfs().filter { it.name.startsWith("year") } }

// exclude columns from column set
df.select { allDfs().except { age } }
```

<!---END-->
