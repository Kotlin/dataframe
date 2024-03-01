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

<h2><code>Columns Selection DSL</code> Grammar</h2><p>    </p><p><code>(What is this notation?)</code>
    </p><h3>Definitions:</h3><p><code>columnGroupReference: </code><code>String</code><code> | </code><code>KProperty</code><code>&lt;*&gt;</code></p><p>                                       
<code>| </code><code>ColumnPath</code></p><p><code>colSelector: </code><code>ColumnSelector</code></p><p><code>colsSelector: </code><code>ColumnsSelector</code></p><p><code>column: </code><code>ColumnAccessor</code><code> | </code><code>String</code></p><p>           
<code>| </code><code>KProperty</code><code>&lt;*&gt; | </code><code>ColumnPath</code></p><p><code>columnGroup: </code><code>SingleColumn</code><code>&lt;</code><code>DataRow</code><code>&lt;*&gt;&gt; | </code><code>String</code></p><p>                     
<code>| </code><code>KProperty</code><code>&lt;* | </code><code>DataRow</code><code>&lt;*&gt;&gt; | </code><code>ColumnPath</code></p><p><code>columnNoAccessor: </code><code>String</code><code> | </code><code>KProperty</code><code>&lt;*&gt; | </code><code>ColumnPath</code></p><p><code>columnOrSet: </code><code>column</code><code> | </code><code>columnSet</code></p><p><code>columnSet: </code><code>ColumnSet</code><code>&lt;*&gt;</code></p><p><code>columnsResolver: </code><code>ColumnsResolver</code></p><p><code>condition: </code><code>ColumnFilter</code></p><p><code>expression: </code><code>Column Expression</code></p><p><code>ignoreCase: </code><code>Boolean</code></p><p><code>index: </code><code>Int</code></p><p><code>indexRange: </code><code>IntRange</code></p><p><code>infer: </code><code>Infer</code></p><p><code>kind: </code><code>ColumnKind</code></p><p><code>kType: </code><code>KType</code></p><p><code>name: </code><code>String</code></p><p><code>number: </code><code>Int</code></p><p><code>regex: </code><code>Regex</code></p><p><code>singleColumn: </code><code>SingleColumn</code><code>&lt;</code><code>DataRow</code><code>&lt;*&gt;&gt;</code></p><p><code>T: Column type</code></p><p><code>text: </code><code>String</code></p><p>    </p><h3>What can be called directly in the <code>Columns Selection DSL</code>:</h3><p>    </p><p><code>column</code> <code><strong>..</strong></code> <code>column</code></p><p><code>|</code> <strong><code>this</code></strong><code>/</code><strong><code>it</code></strong><code><strong><code>[</code></strong></code><code>column</code><strong><code>,</code></strong><code>..</code><code><strong><code>]</code></strong></code></p><p><code>|</code> <strong><code>this</code></strong><code>/</code><strong><code>it</code></strong><code><strong><code>[</code></strong></code><strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong><code><strong><code>]</code></strong></code></p><p><code>|</code> <code><strong>all</strong></code><strong><code>()</code></strong></p><p><code>|</code> <strong><code>all</code></strong><code>(</code><code><strong>Before</strong></code><code>|</code><code><strong>After</strong></code><code>|</code><code><strong>From</strong></code><code>|</code><code><strong>UpTo</strong></code><code>)</code> <code>(</code> <strong><code>(</code></strong><code>column</code><strong><code>)</code></strong> <code>|</code> <strong><code>{</code></strong> <code>colSelector</code> <strong><code>}</code></strong> <code>)</code></p><p><code>|</code> <code><strong>allExcept</strong></code> <strong><code>{ </code></strong><code>colsSelector</code><strong><code> }</code></strong></p><p><code>|</code> <code><strong>allExcept</strong></code><strong><code>(</code></strong><code>column</code><strong><code>,</code></strong><code> ..</code><strong><code>)</code></strong></p><p><code>|</code> <code>columnOrSet</code> <code><strong>and</strong></code><code> [ </code><strong><code>{</code></strong><code> ] </code><code>columnOrSet</code><code> [ </code><strong><code>}</code></strong><code> ] </code></p><p><code>|</code> <code>columnOrSet</code>.<code><strong>and</strong></code> <strong><code>(</code></strong><code>|</code><strong><code>{ </code></strong><code>columnOrSet</code><strong><code> }</code></strong><code>|</code><strong><code>)</code></strong></p><p><code>|</code> <code>(</code>
 <code><strong>col</strong></code>
 <code>|</code> <code><strong>valueCol</strong></code>
 <code>|</code> <code><strong>frameCol</strong></code>
 <code>|</code> <code><strong>colGroup</strong></code>
 <code>)[</code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;</code></strong><code>]</code><strong><code>(</code></strong><code>column</code><code> | </code><code>index</code><strong><code>)</code></strong></p><p><code>|</code> <code>(</code>
 <code><strong>cols</strong></code>
 <code>|</code> <code><strong>valueCols</strong></code>
 <code>|</code> <code><strong>frameCols</strong></code>
 <code>|</code> <code><strong>colGroups</strong></code>
 <code>) [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p><code>|</code> <code><strong>cols</strong></code><code>[</code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;</code></strong><code>]</code><strong><code>(</code></strong><code>column</code><strong><code>,</code></strong><code>.. |</code><code>index</code><strong><code>,</code></strong><code>.. |</code><code>indexRange</code><strong><code>)</code></strong></p><p><code>|</code> <code><strong>colsAtAnyDepth</strong></code><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p><code>|</code> <code><strong>colsInGroups</strong></code><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p><code>|</code> <code><strong>colsOf</strong></code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;</code></strong><code> [</code> <strong><code>(</code></strong><code>kType</code><strong><code>)</code></strong> <code>] [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p><code>|</code> <code><strong>colsOfKind</strong></code><strong><code>(</code></strong><code>kind</code><strong><code>,</code></strong><code> ..</code><strong><code>)</code></strong><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p><code>|</code> <code><strong>drop</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><strong><code>(</code></strong><code>number</code><strong><code>)</code></strong></p><p><code>|</code> <code><strong>drop</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>While</strong></code><strong><code> { </code></strong><code>condition</code><strong><code> }</code></strong></p><p><code>|</code> <code><strong>expr</strong></code><strong><code>(</code></strong><code>[</code><code>name</code><strong><code>,</code></strong><code>][</code><code>infer</code><code>]</code><strong><code>)</code></strong> <strong><code>{ </code></strong><code>expression</code><strong><code> }</code></strong></p><p><code>|</code> <code>(</code>
 <code><strong>first</strong></code>
 <code>|</code> <code><strong>last</strong></code>
 <code>|</code> <code><strong>single</strong></code>
 <code>) [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p><code>|</code> <code><strong>nameContains</strong></code><strong><code>(</code></strong><code>text</code><code>[</code><strong><code>,</code></strong> <code>ignoreCase</code><code>] | </code><code>regex</code><strong><code>)</code></strong></p><p><code>|</code> 
<strong><code>name</code></strong><code>(</code><code><strong>Starts</strong></code><code>|</code><code><strong>Ends</strong></code><code>)</code><strong><code>With</code></strong> <strong><code>(</code></strong><code>text</code><code>[</code><strong><code>,</code></strong> <code>ignoreCase</code><code>]</code><strong><code>)</code></strong></p><p><code>|</code> <code>column</code> <code><strong>named</strong></code><code>/</code><code><strong>into</strong></code> <code>column</code></p><p><code>|</code> <code>column</code><code>(</code>.<code><strong>named</strong></code><code>|</code>.<code><strong>into</strong></code><code>)</code><strong><code>(</code></strong><code>column</code><strong><code>)</code></strong></p><p><code>|</code> <code><strong>none</strong></code><strong><code>()</code></strong></p><p><code>|</code> <code><strong>take</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><strong><code>(</code></strong><code>number</code><strong><code>)</code></strong></p><p><code>|</code> <code><strong>take</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>While</strong></code><strong><code> { </code></strong><code>condition</code><strong><code> }</code></strong></p><p><code>|</code> <code><strong>withoutNulls</strong></code><strong><code>()</code></strong></p><p>    </p><h3>What can be called on a <code>ColumnSet</code>:</h3><p>    </p><p><code>columnSet</code></p><p>    <code><strong><code>[</code></strong></code><code>index</code><code><strong><code>]</code></strong></code></p><p>    <code>|</code> <code><strong><code>[</code></strong></code><code>index</code><strong><code>,</code></strong><code>.. |</code><code>indexRange</code><code><strong><code>]</code></strong></code>`</p><p>    <code>|</code> <code><strong><code>[</code></strong></code><strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong><code><strong><code>]</code></strong></code></p><p>    <code>|</code> .<code><strong>all</strong></code><strong><code>()</code></strong></p><p>    <code>|</code> .<strong><code>all</code></strong><code>(</code><code><strong>Before</strong></code><code>|</code><code><strong>After</strong></code><code>|</code><code><strong>From</strong></code><code>|</code><code><strong>UpTo</strong></code><code>)</code> <code>(</code> <strong><code>(</code></strong><code>column</code><strong><code>)</code></strong> <code>|</code> <strong><code>{</code></strong> <code>condition</code> <strong><code>}</code></strong> <code>)</code></p><p>    <code>|</code> .<code><strong>and</strong></code> <strong><code>(</code></strong><code>|</code><strong><code>{ </code></strong><code>columnOrSet</code><strong><code> }</code></strong><code>|</code><strong><code>)</code></strong></p><p>    <code>|</code> <code>(</code>
 .<code><strong>col</strong></code>
 <code>|</code> .<code><strong>valueCol</strong></code>
 <code>|</code> .<code><strong>frameCol</strong></code>
 <code>|</code> .<code><strong>colGroup</strong></code>
 <code>)</code><strong><code>(</code></strong><code>index</code><strong><code>)</code></strong></p><p>    <code>|</code> <code>(</code>
 .<code><strong>cols</strong></code>
 <code>|</code> .<code><strong>valueCols</strong></code>
 <code>|</code> .<code><strong>frameCols</strong></code>
 <code>|</code> .<code><strong>colGroups</strong></code>
 <code>) [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>cols</strong></code><strong><code>(</code></strong><code>index</code><strong><code>,</code></strong><code>.. |</code><code>indexRange</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>colsAtAnyDepth</strong></code><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>colsInGroups</strong></code><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>colsOf</strong></code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;</code></strong><code> [</code> <strong><code>(</code></strong><code>kType</code><strong><code>)</code></strong> <code>] [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>colsOfKind</strong></code><strong><code>(</code></strong><code>kind</code><strong><code>,</code></strong><code> ..</code><strong><code>)</code></strong><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>distinct</strong></code><strong><code>()</code></strong></p><p>    <code>|</code> .<code><strong>drop</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><strong><code>(</code></strong><code>number</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>drop</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>While</strong></code><strong><code> { </code></strong><code>condition</code><strong><code> }</code></strong></p><p>    <code>|</code> <code><strong>except</strong></code> <code>[</code><strong><code> { </code></strong><code>]</code> <code>columnsResolver</code> <code>[</code><strong><code>}</code></strong><code>]</code></p><p>    <code>|</code> <code><strong>except</strong></code> <code>column</code></p><p>    <code>|</code> .<code><strong>except</strong></code><strong><code>(</code></strong><code>column</code><strong><code>,</code></strong><code> ..</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>filter</strong></code><strong><code> {</code></strong> <code>condition</code> <strong><code>}</code></strong></p><p>    <code>|</code> <code>(</code>
 .<code><strong>first</strong></code>
 <code>|</code> .<code><strong>last</strong></code>
 <code>|</code> .<code><strong>single</strong></code>
 <code>) [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<strong><code>name</code></strong><code>(</code><code><strong>Starts</strong></code><code>|</code><code><strong>Ends</strong></code><code>)</code><strong><code>With</code></strong> <strong><code>(</code></strong><code>text</code><code>[</code><strong><code>,</code></strong> <code>ignoreCase</code><code>]</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>nameContains</strong></code><strong><code>(</code></strong><code>text</code><code>[</code><strong><code>,</code></strong> <code>ignoreCase</code><code>] | </code><code>regex</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>simplify</strong></code><strong><code>()</code></strong></p><p>    <code>|</code> .<code><strong>take</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><strong><code>(</code></strong><code>number</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>take</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>While</strong></code><strong><code> { </code></strong><code>condition</code><strong><code> }</code></strong></p><p>    <code>|</code> .<code><strong>withoutNulls</strong></code><strong><code>()</code></strong></p><p>    </p><h3>What can be called on a <code>Column Group (reference)</code>:</h3><p>    </p><p><code>columnGroup</code></p><p>    <code>|</code> <code><strong><code>[</code></strong></code><code>column</code><strong><code>,</code></strong><code> ..</code><code><strong><code>]</code></strong></code></p><p>    <code>|</code> <code><strong><code>[</code></strong></code><strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong><code><strong><code>]</code></strong></code></p><p>    <code>|</code><code><strong><code> {</code></strong></code> <code>colsSelector</code> <code><strong><code>}</code></strong></code></p><p>    <code>|</code> .<code><strong>allCols</strong></code><strong><code>()</code></strong></p><p>    <code>|</code> .<strong><code>allCols</code></strong><code>(</code><code><strong>Before</strong></code><code>|</code><code><strong>After</strong></code><code>|</code><code><strong>From</strong></code><code>|</code><code><strong>UpTo</strong></code><code>)</code> <code>(</code> <strong><code>(</code></strong><code>column</code><strong><code>)</code></strong> <code>|</code> <strong><code>{</code></strong> <code>colSelector</code> <strong><code>}</code></strong> <code>)</code></p><p>    <code>|</code> .<code><strong>allColsExcept</strong></code> <strong><code> { </code></strong><code>colsSelector</code><strong><code>}</code></strong></p><p>    <code>|</code> .<code><strong>allColsExcept</strong></code><strong><code>(</code></strong><code>columnNoAccessor</code><strong><code>,</code></strong><code> ..</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>and</strong></code> <strong><code>(</code></strong><code>|</code><strong><code>{ </code></strong><code>columnOrSet</code><strong><code> }</code></strong><code>|</code><strong><code>)</code></strong></p><p>    <code>| (</code>
 .<code><strong>col</strong></code>
 <code>|</code> .<code><strong>valueCol</strong></code>
 <code>|</code> .<code><strong>frameCol</strong></code>
 <code>|</code> .<code><strong>colGroup</strong></code>
 <code>)[</code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;</code></strong><code>]</code><strong><code>(</code></strong><code>column</code><code> | </code><code>index</code><strong><code>)</code></strong></p><p>    <code>|</code> <code>(</code>
  .<code><strong>cols</strong></code>
  <code>|</code> .<code><strong>valueCols</strong></code>
  <code>|</code> .<code><strong>frameCols</strong></code>
  <code>|</code> .<code><strong>colGroups</strong></code>
  <code>) [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>cols</strong></code><code>[</code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;</code></strong><code>]</code><strong><code>(</code></strong><code>column</code><strong><code>,</code></strong><code>.. |</code><code>index</code><strong><code>,</code></strong><code>.. |</code><code>indexRange</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>colsAtAnyDepth</strong></code><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>colsInGroups</strong></code><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<strong><code>colsName</code></strong><code>(</code><code><strong>Starts</strong></code><code>|</code><code><strong>Ends</strong></code><code>)</code><strong><code>With</code></strong> <strong><code>(</code></strong><code>text</code><code>[</code><strong><code>,</code></strong> <code>ignoreCase</code><code>]</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>colsNameContains</strong></code><strong><code>(</code></strong><code>text</code><code>[</code><strong><code>,</code></strong> <code>ignoreCase</code><code>] | </code><code>regex</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>colsOfKind</strong></code><strong><code>(</code></strong><code>kind</code><strong><code>,</code></strong><code> ..</code><strong><code>)</code></strong><code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>colsWithoutNulls</strong></code><strong><code>()</code></strong></p><p>    <code>|</code> .<code><strong>drop</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>Cols</strong></code><strong><code>(</code></strong><code>number</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>drop</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>ColsWhile</strong></code><strong><code> { </code></strong><code>condition</code><strong><code> }</code></strong></p><p>    <code>|</code> <code><strong>exceptNew</strong></code> <strong><code> { </code></strong><code>colsSelector</code><strong><code> } EXPERIMENTAL!</code></strong></p><p>    <code>|</code> <code><strong>exceptNew</strong></code><strong><code>(</code></strong><code>columnNoAccessor</code><strong><code>,</code></strong><code> ..</code><strong><code>) EXPERIMENTAL!</code></strong></p><p>    <code>|</code> <code>(</code>
 .<code><strong>firstCol</strong></code>
 <code>|</code> .<code><strong>lastCol</strong></code>
 <code>|</code> .<code><strong>singleCol</strong></code>
 <code>) [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    <code>|</code> .<code><strong>select</strong></code><strong><code> {</code></strong> <code>colsSelector</code> <strong><code>}</code></strong></p><p>    <code>|</code> .<code><strong>take</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>Cols</strong></code><strong><code>(</code></strong><code>number</code><strong><code>)</code></strong></p><p>    <code>|</code> .<code><strong>take</strong></code><code>(</code><code><strong>Last</strong></code><code>)</code><code><strong>ColsWhile</strong></code><strong><code> { </code></strong><code>condition</code><strong><code> }</code></strong></p><p>    </p><p><code>singleColumn</code></p><p>    .<code><strong>colsOf</strong></code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;</code></strong><code> [</code> <strong><code>(</code></strong><code>kType</code><strong><code>)</code></strong> <code>] [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p><p>    </p><p><code>columnGroupReference</code></p><p>    .<code><strong>colsOf</strong></code><strong><code>&lt;</code></strong><code>T</code><strong><code>&gt;(</code></strong><code>kType</code><strong><code>)</code></strong> <code> [</code> <strong><code>{ </code></strong><code>condition</code><strong><code> }</code></strong> <code>]</code></p>

Hello `**test** also hi`

Hello **`test`**` also hi`
