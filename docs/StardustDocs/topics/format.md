[//]: # (title: format)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

<web-summary>
DataFrame Format Operation: Apply CSS formatting for rendering a dataframe to HTML.
</web-summary>

<card-summary>
DataFrame Format Operation: Apply CSS formatting for rendering a dataframe to HTML.
</card-summary>

<link-summary>
DataFrame Format Operation: Apply CSS formatting for rendering a dataframe to HTML.
</link-summary>

Formats the specified columns or cells within the dataframe such that
they have specific CSS attributes applied to them when rendering the dataframe to HTML.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

The selection of columns and rows to apply formatting to follows the [`update` operation](update.md).
This means you can `format { }` some columns `where {}` some predicate holds true `at()` a certain range of rows
`with {}` some cell attributes, just to name an example.

`.perRowCol { row, col -> }` is also available as an alternative to `.with {}`, if you want to format cells based on
their relative context. See the example below for a use-case for this operation.

There are also a handful of shortcuts for common operations within `format`, such as `.linearBg(-20 to blue, 50 to red)`
which is a shortcut for `.with { background(linear(it, -20 to blue, 50 to red)) }`, and `.notNull {}` which is a
shortcut
for `.notNull().with {}`, filtering cells to only include non-null ones.

Finally, you can decide which attributes the selected cells get.
You can combine as many as you like by chaining
them with the `and` infix inside the Formatting DSL.
Some common examples include `background(white)`, which sets the background to `white` for a cell,
`italic`, which makes the cell text _italic_, `textColor(linear(it, 0 to green, 100 to rgb(255, 255, 0)))`, which
interpolates the text color between green and yellow based on where the value of the cell lies in between 0 and 100, and
finally `attr("text-align", "center")`, a custom attribute which centers the text inside the cell.
See [](#grammar) for everything that's available.

The `format` function can be repeated as many times as needed and, to view the result, you can call
[`toHtml()`/`toStandaloneHtml()`](toHTML.md).

#### Grammar {collapsible="true"}

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.ForHtml.html" width="100%"/>

#### Examples

The formatting DSL allows you to create all sorts of formatted tables.
The formatting can depend on the data; for instance, to highlight how the value of
a column corresponds to values of other columns:

<!---FUN formatExample-->
<tabs>
<tab title="Properties">

```kotlin
val ageMin = df.age.min()
val ageMax = df.age.max()

df
    .format().with { bold and textColor(black) and background(white) }
    .format { isHappy }.with { background(if (it) green else red) }
    .format { weight }.notNull().linearBg(50 to FormattingDsl.blue, 90 to FormattingDsl.red)
    .format { age }.perRowCol { row, col ->
        textColor(
            linear(value = col[row], from = ageMin to blue, to = ageMax to green),
        )
    }
```

</tab>
<tab title="Strings">

```kotlin
val ageMin = df.min { "age"<Int>() }
val ageMax = df.max { "age"<Int>() }

df
    .format().with { bold and textColor(black) and background(white) }
    .format("isHappy").with {
        background(if (it as Boolean) green else red)
    }
    .format("weight").notNull().with { linearBg(it as Int, 50 to blue, 90 to red) }
    .format("age").perRowCol { row, col ->
        col as DataColumn<Int>
        textColor(
            linear(value = col[row], from = ageMin to blue, to = ageMax to green),
        )
    }
```

</tab></tabs>
<!---END-->

<inline-frame src="resources/formatExample_properties.html" width="100%"/>

Alternatively, you could also customize the dataframe in a data-independent manner:

<!---FUN formatExampleNumbers-->

```kotlin
df2.format().perRowCol { row, col ->
    val rowIndex = row.index()
    val colIndex = row.df().getColumnIndex(col)
    if ((rowIndex - colIndex) % 3 == 0) {
        background(darkGray) and textColor(white)
    } else {
        background(white) and textColor(black)
    }
}
```

<!---END-->
<inline-frame src="resources/formatExampleNumbers.html" width="100%"/>
