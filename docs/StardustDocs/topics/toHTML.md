[//]: # (title: toHtml)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Render-->

`DataFrame` instances can be rendered to HTML.
Rendering of hierarchical tables in HTML is supported by JS and CSS definitions
that can be found in project resources.

Dataframes can also be formatted before being converted to HTML.
See [](format.md) for how to do this.

Depending on your environment, there can be different ways to use the result of `toHtml` functions.

## IntelliJ IDEA

### Working with the result

The following function produces HTML that includes JS and CSS definitions.
It can be displayed in the browser and has parameters for customization.

<!---FUN useRenderingResult-->

```kotlin
df.toStandaloneHtml(DisplayConfiguration(rowsLimit = null)).openInBrowser()
df.toStandaloneHtml(DisplayConfiguration(rowsLimit = null)).writeHtml(File("/path/to/file"))
df.toStandaloneHtml(DisplayConfiguration(rowsLimit = null)).writeHtml(Path("/path/to/file"))
```

<!---END-->

### Composing multiple tables

`toHtml` and `toStandaloneHtml` return composable `DataFrameHtmlData`,
which you can use to include additional scripts, elements,
or styles at the end of the page or just to merge multiple tables into one HTML snippet.

<!---FUN composeTables-->

```kotlin
val df1 = df.reorderColumnsByName()
val df2 = df.sortBy { age }
val df3 = df.sortByDesc { age }

listOf(df1, df2, df3).fold(DataFrameHtmlData.tableDefinitions()) { acc, df -> acc + df.toHtml() }
```

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


