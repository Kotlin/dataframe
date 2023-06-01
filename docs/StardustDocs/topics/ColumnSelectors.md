[//]: # (title: Column selectors)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

[`DataFrame`](DataFrame.md) provides DSL for selecting arbitrary set of columns.

Column selectors are used in many operations:

<!---FUN columnSelectorsUsages-->

```kotlin
df.select { age and name }
df.fillNaNs { colsOf<Double>().recursively() }.withZero()
df.remove { cols { it.hasNulls() } }
df.group { cols { it.data != name } }.into { "nameless" }
df.update { city }.notNull { it.lowercase() }
df.gather { colsOf<Number>() }.into("key", "value")
df.move { name.firstName and name.lastName }.after { city }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsUsages.html"/>
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

// recursive traversal of all children columns excluding ColumnGroups
df.select { name.cols { !it.isColumnGroup() }.recursively() }
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

// recursive traversal of all children columns excluding ColumnGroups
df.select { name.cols { !it.isColumnGroup() }.recursively() }
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

// recursive traversal of all children columns excluding groups
df.select { "name".cols { !it.isColumnGroup() }.recursively() }
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectors.html"/>
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

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnsSelectorByIndices.html"/>
<!---END-->

**Other column selectors:**

<!---FUN columnSelectorsMisc-->

```kotlin
// by condition
df.select { cols { it.name().startsWith("year") } }
df.select { startsWith("year") }

// by type
df.select { colsOf<String>() }

// by type with condition
df.select { colsOf<String?> { it.countDistinct() > 5 } }

// all top-level columns
df.select { all() }

// first/last n columns
df.select { take(2) }
df.select { takeLast(2) }

// all except first/last n columns
df.select { drop(2) }
df.select { dropLast(2) }

// find the first column satisfying the condition
df.select { first { it.name.startsWith("year") } }

// find the last column inside a column group satisfying the condition
df.select {
    colGroup("name").last { it.name().endsWith("Name") }
}

// find the single column inside a column group satisfying the condition
df.select {
    Person::name.single { it.name().startsWith("first") }
}

// recursive traversal of all columns, excluding ColumnGroups from result
df.select { cols { !it.isColumnGroup() }.recursively() }

// depth-first-search traversal of all columns, including ColumnGroups in result
df.select { all().recursively() }

// recursive traversal with condition
df.select { cols { it.name().contains(":") }.recursively() }

// recursive traversal of columns of given type
df.select { colsOf<String>().rec() }

// all columns except given column set
df.select { except { colsOf<String>() } }

// union of column sets
df.select { take(2) and col(3) }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsMisc.html"/>
<!---END-->

**Modify the set of selected columns:**

<!---FUN columnSelectorsModifySet-->

```kotlin
// first/last n value- and frame columns in column set
df.select { cols { !it.isColumnGroup() }.recursively().take(3) }
df.select { cols { !it.isColumnGroup() }.recursively().takeLast(3) }

// all except first/last n value- and frame columns in column set
df.select { cols { !it.isColumnGroup() }.recursively().drop(3) }
df.select { cols { !it.isColumnGroup() }.recursively().dropLast(3) }

// filter column set by condition
df.select { cols { !it.isColumnGroup() }.rec().filter { it.name().startsWith("year") } }

// exclude columns from column set
df.select { cols { !it.isColumnGroup() }.rec() except { age } }

// keep only unique columns
df.select { (colsOf<Int>() and age).distinct() }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsModifySet.html"/>
<!---END-->
