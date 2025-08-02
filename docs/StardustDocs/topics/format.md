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

#### Grammar

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.api.FormatDocs.Grammar.ForHtml.html" width="100%"/>

#### Examples

<!---FUN formatExample-->
<tabs>
<tab title="Properties">

```kotlin
df
    .format().with { bold and textColor(black) }
    .format { isHappy }.with { background(if (it) green else red) }
    .format { weight }.notNull().linearBg(50 to FormattingDsl.blue, 90 to FormattingDsl.red)
    .format { age }.perRowCol { row, col ->
        textColor(
            linear(value = col[row], from = col.min() to blue, to = col.max() to green)
        )
    }
    .toStandaloneHtml()
```

</tab>
<tab title="Strings">

```kotlin
df
    .format().with { bold and textColor(black) }
    .format("isHappy").with { background(if (it as Boolean) green else red) }
    .format("weight").notNull().with { linearBg(it as Int, 50 to blue, 90 to red) }
    .format("age").perRowCol { row, col ->
        col as DataColumn<Int>
        textColor(
            linear(value = col[row], from = col.min() to blue, to = col.max() to green)
        )
    }
    .toStandaloneHtml()
```

</tab></tabs>
<!---END-->

<inline-frame src="resources/formatExample.html" width="100%"/>

