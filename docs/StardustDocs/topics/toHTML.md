[//]: # (title: toHtml)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Render-->

## HTML rendering

`DataFrame` instances can be rendered to HTML.
Rendering of hierarchical tables in HTML is supported by JS and CSS definitions
that can be found in project resources.

Dataframes can also be formatted before being converted to HTML.
See [](format.md) for how to do this.

Besides that, DataFrame provides multiple APIs to customize HTML output.

### Display images
Values of an `org.jetbrains.kotlinx.dataframe.datatypes.IMG` class are rendered as `<img>` tag

<!---FUN displayImg-->

```kotlin
val htmlData = dataFrameOf(
    "kotlinLogo" to columnOf(
        IMG("https://kotlin.github.io/dataframe/images/kotlin-logo.svg"),
    ),
).toStandaloneHtml()
```

<!---END-->

### Embed pages

Values of an `org.jetbrains.kotlinx.dataframe.datatypes.IFRAME` class are rendered as `<iframe>` tag

<!---FUN displayIFrame-->

```kotlin
val htmlData = dataFrameOf(
    "documentationPages" to columnOf(
        IFRAME(
            src = "https://kotlin.github.io/dataframe/tohtml.html",
            width = 850,
            height = 500,
        ),
    ),
).toStandaloneHtml()
```

<!---END-->

### Render clickable links

Values of `java.net.URL` are rendered as `<a>` tag

<!---FUN displayURL-->

```kotlin
val htmlData = dataFrameOf(
    "documentationPages" to columnOf(
        URI("https://kotlin.github.io/dataframe/format.html").toURL(),
        URI("https://kotlin.github.io/dataframe/tohtml.html").toURL(),
        URI("https://kotlin.github.io/dataframe/jupyterrendering.html").toURL(),
    ),
)
    .toStandaloneHtml()
```

<!---END-->

### Render any HTML inside a cell

Wrap cell values in a custom HTML using `RenderedContent.media`

<!---FUN displayMediaContent-->
<tabs>
<tab title="Properties">

```kotlin
val htmlData = dataFrameOf(
    "documentationPages" to columnOf(
        "https://kotlin.github.io/dataframe/format.html",
        "https://kotlin.github.io/dataframe/tohtml.html",
        "https://kotlin.github.io/dataframe/jupyterrendering.html",
    ),
)
    .convert { documentationPages }.with {
        val uri = URI(it)
        RenderedContent.media("""<a href='$uri'>${uri.path}</a>""")
    }
    .toStandaloneHtml()
```

</tab>
<tab title="Strings">

```kotlin
val htmlData = dataFrameOf(
    "documentationPages" to columnOf(
        "https://kotlin.github.io/dataframe/format.html",
        "https://kotlin.github.io/dataframe/tohtml.html",
        "https://kotlin.github.io/dataframe/jupyterrendering.html",
    ),
)
    .convert { "documentationPages"<String>() }.with {
        val uri = URI(it)
        RenderedContent.media("""<a href='$uri'>${uri.path}</a>""")
    }
    .toStandaloneHtml()
```

</tab></tabs>
<!---END-->

### Sample data 
This dataframe is used in the following examples

<!---FUN df-->

```kotlin
val df = dataFrameOf(
    "name" to columnOf(
        "firstName" to columnOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
        "lastName" to columnOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
    ),
    "age" to columnOf(15, 45, 20, 40, 30, 20, 30),
    "city" to columnOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
    "weight" to columnOf(54, 87, null, null, 68, 55, 90),
    "isHappy" to columnOf(true, true, false, true, true, false, true),
)
```

<!---END-->

### Reusable rendering logic

Generic approach to custom cell rendering

<!---FUN cellRenderer-->

```kotlin
class CustomArrayCellRenderer : ChainedCellRenderer(DefaultCellRenderer) {
    override fun maybeContent(value: Any?, configuration: DisplayConfiguration): RenderedContent? {
        if (value is Boolean) {
            return RenderedContent.text(if (value) "✓" else "✗")
        }
        // return null to delegate work to parent renderer: DefaultCellRenderer
        return null
    }

    override fun maybeTooltip(value: Any?, configuration: DisplayConfiguration): String? {
        // return null to delegate work to parent renderer: DefaultCellRenderer
        return null
    }
}

val htmlData = df.toStandaloneHtml(cellRenderer = CustomArrayCellRenderer())
```

<!---END-->

### Custom HTML outside the table

The result of `toHtml` can be composed with other HTML, CSS, JS definitions.
Let's build an alternative to displaying all rows in one table, custom pagination across multiple files

<!---FUN appendCustomHtml-->

```kotlin
val pages = df.duplicateRows(10).chunked(20)
val files = pages.indices.map { i -> File("page$i.html") }
val navLinks = files.mapIndexed { i, file ->
    """<a href="${file.name}">Page ${i + 1}</a>"""
}.joinToString(" | ")

pages.forEachIndexed { i, page ->
    val output = files[i]
    page.toStandaloneHtml().plus(DataFrameHtmlData(body = navLinks))
    // uncomment
    // .writeHtml(output)
}
```

<!---END-->

### Custom style and scripts

Let's add a hover effect and click listener for table cells.
See [init.js](https://github.com/Kotlin/dataframe/blob/704200cb86e7bdc07b800a7cfef48de408bd5fe8/core/src/main/resources/init.js) and [table.css](https://github.com/Kotlin/dataframe/blob/ead4f8666df5cf24e5bf45d245cda3200e150e93/core/src/main/resources/table.css) for reference.

<!---FUN interactiveJs-->

```kotlin
val selectCellInteraction = DataFrameHtmlData(
    style =
        """
        td:hover {
            background-color: rgba(0, 123, 255, 0.15);
            cursor: pointer;
        }
        """.trimIndent(),
    script =
        """
        (function() {
            let cells = document.querySelectorAll('td');
            cells.forEach(function(cell) {
                cell.addEventListener('click', function(e) {
                    let content = cell.textContent;
                    alert(content);
                });
            });
        })();
        """.trimIndent(),
)

// keep in mind JS script initialization order.
val htmlData = df.toStandaloneHtml().plus(selectCellInteraction)
```

<!---END-->

Depending on your environment, there can be different ways to use the result of `toHtml` functions.

## IntelliJ IDEA

### Working with the result

The following function produces HTML that includes JS and CSS definitions.
It can be displayed in the browser and has parameters for customization.

<!---FUN useRenderingResult-->

```kotlin
val configuration = DisplayConfiguration(rowsLimit = null)
df.toStandaloneHtml(configuration).openInBrowser()
df.toStandaloneHtml(configuration).writeHtml(File("/path/to/file"))
df.toStandaloneHtml(configuration).writeHtml(Path("/path/to/file"))
```

<!---END-->

### Composing multiple tables

`toHtml` and `toStandaloneHtml` return composable `DataFrameHtmlData`,
which you can use to include additional scripts, elements,
or styles at the end of the page or just to merge multiple tables into one HTML snippet.

<!---FUN composeTables-->
<tabs>
<tab title="Properties">

```kotlin
val df1 = df.reorderColumnsByName()
val df2 = df.sortBy { age }
val df3 = df.sortByDesc { age }

listOf(df1, df2, df3).fold(DataFrameHtmlData.tableDefinitions()) { acc, df -> acc + df.toHtml() }
```

</tab>
<tab title="Strings">

```kotlin
val df1 = df.reorderColumnsByName()
val df2 = df.sortBy("age")
val df3 = df.sortByDesc("age")

listOf(df1, df2, df3).fold(DataFrameHtmlData.tableDefinitions()) { acc, df ->
    acc + df.toHtml()
}
```

</tab></tabs>
<!---END-->

## Jupyter Notebooks

### Configuring display for individual output

`toHtml` is useful if you want to configure how a single cell is displayed.
To configure the display for the entire notebook, please refer to the [](jupyterRendering.md) section.

<!---FUN configureCellOutput-->

```kotlin
df.toHtml(DisplayConfiguration(cellContentLimit = -1))
```

<!---END-->


