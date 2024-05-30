[//]: # (title: Column selectors)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

[`DataFrame`](DataFrame.md) provides a DSL for selecting an arbitrary set of columns.

Column selectors are used in many operations:

<!---FUN columnSelectorsUsages-->

```kotlin
df.select { age and name }
df.fillNaNs { colsAtAnyDepth().colsOf<Double>() }.withZero()
df.remove { cols { it.hasNulls() } }
df.group { cols { it.data != name } }.into { "nameless" }
df.update { city }.notNull { it.lowercase() }
df.gather { colsOf<Number>() }.into("key", "value")
df.move { name.firstName and name.lastName }.after { city }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsUsages.html"/>
<!---END-->

#### Full DSL Grammar:

**Definitions**

<dataFrame src="org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.DefinitionsPartOfGrammar.html"/>

<tabs>
    <tab title="Directly in the DSL">
        <dataFrame src="org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.PlainDslPartOfGrammar.html"/>
</tab>
    <tab title="On a Column Set">
        <dataFrame src="org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.ColumnSetPartOfGrammar.ForHtml.html"/>
</tab>
    <tab title="On a Column Group">
        <dataFrame src="org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.ColumnGroupPartOfGrammar.ForHtml.html"/>
</tab>
</tabs>

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

// all columns of ColumnGroup
df.select { name.allCols() }

// traversal of columns at any depth from here excluding ColumnGroups
df.select { name.colsAtAnyDepth { !it.isColumnGroup() } }
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

// all columns of ColumnGroup
df.select { name.allCols() }

// traversal of columns at any depth from here excluding ColumnGroups
df.select { name.colsAtAnyDepth { !it.isColumnGroup() } }
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

// all columns of ColumnGroup
df.select { "name".allCols() }

// traversal of columns at any depth from here excluding ColumnGroups
df.select { "name".colsAtAnyDepth { !it.isColumnGroup() } }
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
df.select { nameStartsWith("year") }

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
    colGroup("name").lastCol { it.name().endsWith("Name") }
}

// find the single column inside a column group satisfying the condition
df.select {
    Person::name.singleCol { it.name().startsWith("first") }
}

// traversal of columns at any depth from here excluding ColumnGroups
df.select { colsAtAnyDepth { !it.isColumnGroup() } }

// traversal of columns at any depth from here including ColumnGroups
df.select { colsAtAnyDepth() }

// traversal of columns at any depth with condition
df.select { colsAtAnyDepth { it.name().contains(":") } }

// traversal of columns at any depth to find columns of given type
df.select { colsAtAnyDepth().colsOf<String>() }

// all columns except given column set
df.select { allExcept { colsOf<String>() } }

// union of column sets
df.select { take(2) and col(3) }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsMisc.html"/>
<!---END-->

**Modify the set of selected columns:**

<!---FUN columnSelectorsModifySet-->

```kotlin
// first/last n value- and frame columns in column set
df.select { colsAtAnyDepth { !it.isColumnGroup() }.take(3) }
df.select { colsAtAnyDepth { !it.isColumnGroup() }.takeLast(3) }

// all except first/last n value- and frame columns in column set
df.select { colsAtAnyDepth { !it.isColumnGroup() }.drop(3) }
df.select { colsAtAnyDepth { !it.isColumnGroup() }.dropLast(3) }

// filter column set by condition
df.select { colsAtAnyDepth { !it.isColumnGroup() }.filter { it.name().startsWith("year") } }

// exclude columns from column set
df.select { colsAtAnyDepth { !it.isColumnGroup() }.except { age } }

// keep only unique columns
df.select { (colsOf<Int>() and age).distinct() }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsModifySet.html"/>
<!---END-->
