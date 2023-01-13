[//]: # (title: Rendering)

// TODO

## Jupyter Notebooks

Rendering in Jupyter Notebooks can be configured using `dataFrameConfig.display` value.

### Content limit length

Content in each cell gets truncated to 40 characters by default. 
This can be changed by setting `cellContentLimit` to a different value on the display configuration.

```kotlin
dataFrameConfig.display.cellContentLimit = 100
```
