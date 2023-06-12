[//]: # (title: toHTML)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Render-->

DataFrame can be rendered to HTML.
Rendering of hierarchical tables in HTML is supported by JS and CSS definitions
that can be found in project resources.

Depending on your environment there can be different ways to use result of `toHTML` functions

## IntelliJ IDEA

### Working with result

The following function produces HTML that includes JS and CSS definitions. It can be displayed in the browser and has parameters for customization.

<!---FUN useRenderingResult-->

```kotlin
df.toStandaloneHTML(DisplayConfiguration(rowsLimit = null)).openInBrowser()
df.toStandaloneHTML(DisplayConfiguration(rowsLimit = null)).writeHTML(File("/path/to/file"))
df.toStandaloneHTML(DisplayConfiguration(rowsLimit = null)).writeHTML(Path("/path/to/file"))
```

<!---END-->

### Composing multiple tables

`toHTML` and `toStandaloneHTML` return composable `DataFrameHtmlData`. You can use it to include additional scripts, elements, styles on final page or just merge together multiple tables.

<!---FUN composeTables-->

```kotlin
val df1 = df.reorderColumnsByName()
val df2 = df.sortBy { age }
val df3 = df.sortByDesc { age }

listOf(df1, df2, df3).fold(DataFrameHtmlData.tableDefinitions()) { acc, df -> acc + df.toHTML() }
```

<!---END-->

## Jupyter Notebooks

### Configuring display for individual output

`toHTML` is useful if you want to configure how a single cell is displayed. To configure the display for the entire notebook, please refer to [Jupyter Notebooks](jupyterRendering.md) section.

<!---FUN configureCellOutput-->

```kotlin
df.toHTML(DisplayConfiguration(cellContentLimit = -1))
```

<!---END-->


