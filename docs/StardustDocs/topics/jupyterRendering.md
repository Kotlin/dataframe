[//]: # (title: Jupyter Notebooks)

Rendering in Jupyter Notebooks can be configured using `dataFrameConfig.display` value.
Have a look at [toHTML](toHTML.md#configuring-display-for-individual-output) function to configure output for single cell

**API reference**: [`toHTML`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.io/to-h-t-m-l.html)

### Content limit length

Content in each cell gets truncated to 40 characters by default.
This can be changed by setting `cellContentLimit` to a different value on the display configuration.

```kotlin
dataFrameConfig.display.cellContentLimit = 100
```
