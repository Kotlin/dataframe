[//]: # (title: Setup Kotlin DataFrame in Jupyter)

## Jupyter Notebook

You can use the Kotlin DataFrame library in Jupyter Notebook and in Jupyter Lab.
To start,
install the latest version of [Kotlin kernel](https://github.com/Kotlin/kotlin-jupyter#installation)
and start your favorite Jupyter client from
the command line, for example:

```shell
jupyter notebook
```

In the notebook, you only have to write a single line to start using the Kotlin DataFrame library:

```text
%use dataframe
```

In this case, the version bundled with the kernel will be used.
If you want always to use the latest version, add another magic before `%use dataframe`:

```text
%useLatestDescriptors
%use dataframe
```

If you want to use a specific version of the Kotlin DataFrame library, you can specify it in brackets:

```text
%use dataframe(%dataFrameVersion%)
```

After loading, all essential types will be already imported, so you can start using the Kotlin DataFrame library. Enjoy!

```kotlin
val df = DataFrame.read("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv")
df // the last expression in the cell is displayed
```

When the previous cell with variable declaration is executed, `DataFrame` provides a data schema API based on data:
```kotlin
df.filter { stargazers_count > 50 }
```
