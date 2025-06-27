[//]: # (title: Column selectors)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

[`DataFrame`](DataFrame.md) provides a DSL for selecting an arbitrary set of columns: the Columns Selection DSL.

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

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsUsages.html" width="100%"/>
<!---END-->

#### Full DSL Grammar {collapsible="true"}

**Definitions**

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.DefinitionsPartOfGrammar.html" width="100%"/>

<tabs>
    <tab title="Directly in the DSL">
        <inline-frame src="resources/org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.PlainDslPartOfGrammar.html" width="100%"/>
</tab>
    <tab title="On a Column Set">
        <inline-frame src="resources/org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.ColumnSetPartOfGrammar.ForHtml.html" width="100%"/>
</tab>
    <tab title="On a Column Group">
        <inline-frame src="resources/org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar.ColumnGroupPartOfGrammar.ForHtml.html" width="100%"/>
</tab>
</tabs>

#### Functions Overview {collapsible="true"}

##### First (Col), Last (Col), Single (Col) {collapsible="true"}
`first {}`, `firstCol()`, `last {}`, `lastCol()`, `single {}`, `singleCol()`

Returns the first, last, or single column from the top-level, specified [column group](DataColumn.md#columngroup), 
or [`ColumnSet`](#column-resolvers) that adheres to the optional given condition. If no column adheres to the given condition,
`NoSuchElementException` is thrown.

##### Col {collapsible="true"}
`col(name)`, `col(5)`

Creates a [`ColumnAccessor`](#column-resolvers) (or [`SingleColumn`](#column-resolvers)) for a column with the given 
argument from the top-level or specified [column group](DataColumn.md#columngroup). The argument can be either an 
index (`Int`) or a reference to a column (`String`, [`ColumnPath`](#column-resolvers), or 
[`ColumnAccessor`](#column-resolvers);
any [AccessApi](apiLevels.md)).

##### Value Col, Frame Col, Col Group {collapsible="true"}
`valueCol(name)`, `valueCol(5)`, `frameCol(name)`, `frameCol(5)`, `colGroup(name)`, `colGroup(5)`

Creates a [`ColumnAccessor`](DataColumn.md) (or `SingleColumn`) for a 
[value column](DataColumn.md#valuecolumn) / [frame column](DataColumn.md#framecolumn) / 
[column group](DataColumn.md#columngroup) with the given argument from the top-level or
specified [column group](DataColumn.md#columngroup). The argument can be either an index (`Int`) or a reference
to a column (`String`, [`ColumnPath`](#column-resolvers), or [`ColumnAccessor`](#column-resolvers); any [AccessApi](apiLevels.md)).
The functions can be both typed and untyped (in case you're supplying a column name, path, or index).
These functions throw an `IllegalArgumentException` if the column found is not the right kind.

##### Cols {collapsible="true"}
`cols {}`, `cols()`, `cols(colA, colB)`, `cols(1, 5)`, `cols(1..5)`, `[{}]`, `colSet[1, 3]`

Creates a subset of columns ([`ColumnSet`](#column-resolvers)) from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers).
You can use either a `ColumnFilter`, or any of the `vararg` overloads for any [AccessApi](apiLevels.md).
The function can be both typed and untyped (in case you're supplying a column name, -path, or index (range)).

Note that you can also use the `[]` operator for most overloads of `cols` to achieve the same result.

##### Range of Columns {collapsible="true"}
`colA.."colB"`

Creates a [`ColumnSet`](#column-resolvers) containing all columns from `colA` to `colB` (inclusive) from the top-level.
Columns inside [column groups](DataColumn.md#columngroup) are also supported
(as long as they share the same direct parent), as well as any combination of [AccessApi](apiLevels.md).

##### Value Columns, Frame Columns, Column Groups {collapsible="true"}
`valueCols {}`, `valueCols()`, `frameCols {}`, `frameCols()`, `colGroups {}`, `colGroups()`

Creates a subset of columns ([`ColumnSet`](#column-resolvers)) from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers) containing only [value columns](DataColumn.md#valuecolumn) / [frame columns](DataColumn.md#framecolumn) / 
[column groups](DataColumn.md#columngroup) that adhere to the optional condition.

##### Cols of Kind {collapsible="true"}
`colsOfKind(Value, Frame) {}`, `colsOfKind(Group, Frame)`

Creates a subset of columns ([`ColumnSet`](#column-resolvers)) from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers) containing only columns of the specified kind(s) that adhere to the optional condition.

##### All (Cols) {collapsible="true"}
`all()`, `allCols()`

Creates a [`ColumnSet`](#column-resolvers) containing all columns from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers). This is the opposite of [`none()`](ColumnSelectors.md#none) and equivalent to
[`cols()`](ColumnSelectors.md#cols) without filter.
Note, on [column groups](DataColumn.md#columngroup), `all` is named `allCols` instead to avoid confusion.

##### All (Cols) After, -Before, -From, -Up To {collapsible="true"}
`allAfter(colA)`, `allBefore(colA)`, `allColsFrom(colA)`, `allColsUpTo(colA)`

Creates a [`ColumnSet`](#column-resolvers) containing a subset of columns from the top-level, 
specified [column group](DataColumn.md#columngroup), or [`ColumnSet`](#column-resolvers).
The subset includes:
- `all(Cols)Before(colA)`: All columns before the specified column, excluding that column.
- `all(Cols)After(colA)`: All columns after the specified column, excluding that column.
- `all(Cols)From(colA)`: All columns from the specified column, including that column.
- `all(Cols)UpTo(colA)`: All columns up to the specified column, including that column.

NOTE: The `{}` overloads of these functions in the Plain DSL and on [column groups](DataColumn.md#columngroup) 
are a `ColumnSelector` (relative to the receiver).
On `ColumnSets` they are a `ColumnFilter` instead.

##### Cols at any Depth {collapsible="true"}
`colsAtAnyDepth {}`, `colsAtAnyDepth()`

Creates a [`ColumnSet`](#column-resolvers) containing all columns from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers) at any depth if they satisfy the optional given predicate. This means that columns (of all three kinds!)
nested inside [column groups](DataColumn.md#columngroup) are also included.
This function can also be followed by another [`ColumnSet`](#column-resolvers) filter-function like `colsOf<>()`, `single()`,
or `valueCols()`.

**For example:**

Depth-first search to a column containing the value "Alice":

`df.select { colsAtAnyDepth().first { "Alice" in it.values() } }`

The columns at any depth excluding the top-level:

`df.select { colGroups().colsAtAnyDepth() }`

All [value-](DataColumn.md#valuecolumn) and [frame columns](DataColumn.md#framecolumn) at any depth:

`df.select { colsAtAnyDepth { !it.isColumnGroup } }`

All value columns at any depth nested under a column group named "myColGroup":

`df.select { myColGroup.colsAtAnyDepth().valueCols() }`


**Converting from deprecated syntax:**

`dfs { condition }` -> `colsAtAnyDepth { condition }`

`allDfs(includeGroups = false)` -> `colsAtAnyDepth { includeGroups || !it.isColumnGroup() }`

`dfsOf<Type> { condition }` -> `colsAtAnyDepth().colsOf<Type> { condition }`

`cols { condition }.recursively()` -> `colsAtAnyDepth { condition }`

`first { condition }.rec()` -> `colsAtAnyDepth { condition }.first()`

`all().recursively()` -> `colsAtAnyDepth()`

##### Cols in Groups {collapsible="true"}
`colsInGroups {}`, `colsInGroups()`

Creates a [`ColumnSet`](#column-resolvers) containing all columns that are nested in the [column groups](DataColumn.md#columngroup) at 
the top-level, specified [column group](DataColumn.md#columngroup), or [`ColumnSet`](#column-resolvers) adhering to an optional predicate.
This is useful if you want to select all columns that are "one level down".

This function used to be called `children()` in the past.

**For example:**

To get the columns inside all [column groups](DataColumn.md#columngroup) in a [dataframe](DataFrame.md),
instead of having to write:

`df.select { colGroupA.cols() and colGroupB.cols() ... }`

you can use:

`df.select { colsInGroups() }`

or with filter:

`df.select { colsInGroups { "user" in it.name } }`

Similarly, you can take the columns inside all [column groups](DataColumn.md#columngroup) in a [`ColumnSet`](#column-resolvers):

`df.select { colGroups { "my" in it.name }.colsInGroups() }`

##### Take (Last) (Cols) (While) {collapsible="true"}
`take(5)`, `takeLastCols(2)`, `takeLastWhile {}`, `takeColsWhile {}`,

Creates a [`ColumnSet`](#column-resolvers) containing the first / last `n` columns from the top-level, 
specified [column group](DataColumn.md#columngroup), or [`ColumnSet`](#column-resolvers) or those that adhere to the given condition.
Note, to avoid ambiguity, `take` is called `takeCols` when called on a [column group](DataColumn.md#columngroup).

##### Drop (Last) (Cols) (While) {collapsible="true"}
`drop(5)`, `dropLastCols(2)`, `dropLastWhile {}`, `dropColsWhile {}`

Creates a [`ColumnSet`](#column-resolvers) without the first / last `n` columns from the top-level,
specified [column group](DataColumn.md#columngroup), or [`ColumnSet`](#column-resolvers) or those that adhere to the given condition.
Note, to avoid ambiguity, `drop` is called `dropCols` when called on a [column group](DataColumn.md#columngroup).

##### Select from [Column Group](DataColumn.md#columngroup) {collapsible="true"}
`colGroupA.select {}`, `"colGroupA" {}`

Creates a [`ColumnSet`](#column-resolvers) containing the columns selected by a `ColumnsSelector` relative to the specified
[column group](DataColumn.md#columngroup). In practice, this means you're opening a new selection DSL scope inside a 
[column group](DataColumn.md#columngroup) and selecting columns from there.
The selected columns are referenced individually and "unpacked" from their parent
[column group](DataColumn.md#columngroup).

**For example:**

Select `myColGroup.someCol` and all `String` columns from `myColGroup`:

`df.select { myColGroup.select { someCol and colsOf<String>() } }`

`df.select { "myGroupCol" { "colA" and expr("newCol") { colB + 1 } } }`

`df.select { "pathTo"["myGroupCol"].select { "colA" and "colB" } }`

`df.select { it["myGroupCol"].asColumnGroup()() { "colA" and "colB" } }`

> Did you know? Because the Columns Selection DSL uses
> [`@DslMarker`](https://kotlinlang.org/docs/type-safe-builders.html#scope-control-dslmarker), outer scope leaking is
> prohibited! This means that you can't reference columns from the outer scope inside the `select {}` block. This
> ensures safety and prevents issues for when multiple columns exist with the same name.
>
> `userData.select { age and address.select { `~~`age`~~` } }`


##### (All) (Cols) Except {collapsible="true"}
`colSet.except()`, `allExcept {}`, `colGroupA.allColsExcept {}`, `colGroupA.except {}`

Exclude a selection of columns from the current selection using a relative `ColumnsSelector`.

This function is best explained in parts:

**On Column Sets:** `except {}`

This function can be explained the easiest with a [`ColumnSet`](#column-resolvers).
Let's say we want all `Int` columns apart from `age` and `height`.

We can do:

`df.select { colsOf<Int>() except (age and height) }`

which will 'subtract' the [`ColumnSet`](#column-resolvers) created by `age and height` from the [`ColumnSet`](#column-resolvers) created by
[`colsOf<Int>()`](ColumnSelectors.md#cols-of).

This operation can also be used to exclude columns that are originally in [column groups](DataColumn.md#columngroup).

For instance, excluding `userData.age`:

`df.select { colsAtAnyDepth { "a" in it.name() } except userData.age }`

Note that the selection of columns to exclude from column sets is always done relative to the outer scope.
Use the [Extension Properties API](extensionPropertiesApi.md) to prevent scoping issues if possible.

> Special case: If a column that needs to be removed appears multiple times in the [`ColumnSet`](#column-resolvers),
> it is excepted each time it is encountered (including inside [Column Groups](DataColumn.md#columngroup)).
> You could say the receiver `ColumnSet` is [simplified](ColumnSelectors.md#simplify) before the operation is performed:
>
> `cols(a, a, a. b, a. b).except(a. b) == cols(a).except(a. b)`
 

**Directly in the DSL:** `allExcept {}`

Instead of having to write `all() except { ... }` in the DSL, you can use `allExcept { ... }` to achieve the same result.

This does the same but is a handy shorthand.

For example:

`df.select { allExcept { userData.age and height } }`

**On [Column Groups](DataColumn.md#columngroup):** `allColsExcept {}`

The variant of this function on [Column Groups](DataColumn.md#columngroup) is a bit different, as it changes the scope
to being relative to the [Column Groups](DataColumn.md#columngroup).
This is similar to the [`select`](ColumnSelectors.md#select-from-column-group) function.

In other words:

`df.select { myColGroup.allColsExcept { colA and colB } }`

is shorthand for

`df.select { myColGroup.select { allExcept { colA and colB } } }`

or

`df.select { myColGroup.allCols() except { myColGroup.colA and myColGroup.colB } }`

Note the name change, similar to [`allCols`](ColumnSelectors.md#cols), this makes it clearer that you're selecting
columns inside the group, 'lifting' them out.

**On [Column Groups](DataColumn.md#columngroup):** `except {}`

This variant can be used to exclude some nested columns from a [Column Group](DataColumn.md#columngroup) in the selection.
In contrast to `allColsExcept`, this function does not 'lift' the columns out of the group, preserving the structure.

So:

`df.select { colGroup.except { col } }`

is shorthand for:

`df.select { cols(colGroup) except colGroup.col }`

or:

`df.remove { colGroup.col }.select { colGroup }`

##### Column Name Filters {collapsible="true"}
`nameContains()`, `colsNameContains()`, `nameStartsWith()`, `colsNameEndsWith()`

Creates a [`ColumnSet`](#column-resolvers) containing columns from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers) that have names that satisfy the given function. These functions accept a `String` as argument, as
well as an optional `ignoreCase` parameter. For the `nameContains` variant, you can also pass a `Regex` as an argument.
Note, on [column groups](DataColumn.md#columngroup), the functions have names starting with `cols` to avoid
ambiguity.

##### (Cols) Without Nulls {collapsible="true"}
`withoutNulls()`, `colsWithoutNulls()`

Creates a [`ColumnSet`](#column-resolvers) containing columns from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers) that have no `null` values. This is a shorthand for `cols { !it.hasNulls() }`.
Note, to avoid ambiguity, `withoutNulls` is called `colsWithoutNulls` when called on a
[column group](DataColumn.md#columngroup).

##### Distinct {collapsible="true"}
`colSet.distinct()`

Returns a new [`ColumnSet`](#column-resolvers) from the specified [`ColumnSet`](#column-resolvers) containing only distinct columns (by path).
This is useful when you've selected the same column multiple times but only want it once.

This does not cover the case where a column is selected individually and through its enclosing
[column group](DataColumn.md#columngroup). See [`simplify`](ColumnSelectors.md#simplify) for that.

NOTE: This doesn't solve the `DuplicateColumnNamesException` if you've selected two columns with the same name.
For this, you'll need to [rename](ColumnSelectors.md#rename) one of the columns.

##### None {collapsible="true"}
`none()`

Creates an empty [`ColumnSet`](#column-resolvers), essentially selecting no columns at all.
This is the opposite of [`all()`](ColumnSelectors.md#all-cols).

This function mostly exists for completeness, but can be useful in some very specific cases.

##### Cols Of {collapsible="true"}
`colsOf<T>()`, `colsOf<T> {}`

Creates a [`ColumnSet`](#column-resolvers) containing columns from the top-level, specified [column group](DataColumn.md#columngroup),
or [`ColumnSet`](#column-resolvers) that are a subtype of the specified type `T` and adhere to the optional condition.

##### Simplify {collapsible="true"}
`colSet.simplify()`

Returns a new [`ColumnSet`](#column-resolvers) from the specified [`ColumnSet`](#column-resolvers) in 'simplified' form.
This function simplifies the structure of the [`ColumnSet`](#column-resolvers) by removing columns that are already present in
[column groups](DataColumn.md#columngroup), returning only these groups, 
plus columns not belonging in any of the groups.

In other words, this means that if a column in the [`ColumnSet`](#column-resolvers) is inside a [column group](DataColumn.md#columngroup) 
in the [`ColumnSet`](#column-resolvers), it will not be included in the result.

It's useful in combination with [`colsAtAnyDepth {}`](ColumnSelectors.md#cols-at-any-depth), as that function can
create a [`ColumnSet`](#column-resolvers) containing both a column and the [column group](DataColumn.md#columngroup) it's in.

In the past, was named `top()` and `roots()`, but these names have been deprecated.

**For example:**

`cols(a, a.b, d.c).simplify() == cols(a, d.c)`

##### Filter {collapsible="true"}
`colSet.filter {}`

Returns a new [`ColumnSet`](#column-resolvers) from the specified [`ColumnSet`](#column-resolvers) containing only columns that satisfy the given condition.
This function behaves the same as [`cols {}` and `[{}]`](ColumnSelectors.md#cols), but only exists on column sets.

##### And {collapsible="true"}
`colSet and colB`

Creates a [`ColumnSet`](#column-resolvers) containing the columns from both the left and right side of the function. This allows
you to combine selections or simply select multiple columns at once.

Any combination of [AccessApi](apiLevels.md) can be used on either side of the `and` operator.

Note, while you can write `col1 and col2 and col3...`, it may be more concise to use
[`cols(col1, col2, col3...)`](ColumnSelectors.md#cols) instead. The only downside is that you can't mix
[Access APIs](apiLevels.md) with that notation.

##### Rename {collapsible="true"}
`colA named "colB"`, `colA into namedColAccessor`

Renaming a column in the Columns Selection DSL is done by calling the infix functions 
`named` or `into`.
They behave exactly the same, so it's up to contextual preference which one to use.
Any combination of [Access API](apiLevels.md) can be used to specify the column to rename 
and which name should be used instead.

##### Expr (Column Expression) {collapsible="true"}
`expr {}`, `expr("newCol") {}`

Creates a temporary new column by defining an expression to fill up each row.
You may have come across this name before in the [Add DSL](add.md) or
[`toDataFrame {}` DSL](createDataFrame.md#todataframe).

It's extremely useful when you want to create a new column based on existing columns for operations like
[`sortBy`](sortBy.md), [`groupBy`](groupBy.md), etc.

#### Examples

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
df.select { name.colsAtAnyDepth().filter { !it.isColumnGroup() } }
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
df.select { "name".colsAtAnyDepth().filter { !it.isColumnGroup() } }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectors.html" width="100%"/>
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

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.columnsSelectorByIndices.html" width="100%"/>
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
df.select { colsAtAnyDepth().filter { !it.isColumnGroup() } }

// traversal of columns at any depth from here including ColumnGroups
df.select { colsAtAnyDepth() }

// traversal of columns at any depth with condition
df.select { colsAtAnyDepth().filter() { it.name().contains(":") } }

// traversal of columns at any depth to find columns of given type
df.select { colsAtAnyDepth().colsOf<String>() }

// all columns except given column set
df.select { allExcept { colsOf<String>() } }

// union of column sets
df.select { take(2) and col(3) }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsMisc.html" width="100%"/>
<!---END-->

**Modify the set of selected columns:**

<!---FUN columnSelectorsModifySet-->

```kotlin
// first/last n value- and frame columns in column set
df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.take(3) }
df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.takeLast(3) }

// all except first/last n value- and frame columns in column set
df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.drop(3) }
df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.dropLast(3) }

// filter column set by condition
df.select { colsAtAnyDepth().filter { !it.isColumnGroup() && it.name().startsWith("year") } }

// exclude columns from column set
df.select { colsAtAnyDepth().filter { !it.isColumnGroup() }.except { age } }

// keep only unique columns
df.select { (colsOf<Int>() and age).distinct() }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.columnSelectorsModifySet.html" width="100%"/>
<!---END-->

### Column Resolvers

`ColumnsResolver` is the base type used to resolve columns within the **Columns Selection DSL**,  
as well as the return type of columns selection expressions.

All functions described above for selecting columns in various ways return a `ColumnResolver` of a specific kind:

- **`SingleColumn`** — resolves to a single [`DataColumn`](DataColumn.md).
- **`ColumnAccessor`** — a specialized `SingleColumn` with a defined path and type argument.  
  It can also be renamed during selection.
  - **`ColumnPath`** — a wrapper for a [`DataColumn`](DataColumn.md) path
    in a [`DataFrame`](DataFrame.md) also can serve as a `ColumnAccessor`.
```kotlin
// Select all columns from the group by path "group2"/"info":
df.select { pathOf("group2", "info").allCols() }
// For each selected column, place it under its ancestor group
// from two levels up in the column path hierarchy:
df.group { colsAtAnyDepth().colsOf<String>() }
.into { it.path.dropLast(2) }
```
- **`ColumnSet`** — resolves to an ordered list of [`DataColumn`s](DataColumn.md).


