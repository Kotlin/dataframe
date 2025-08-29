# Setup Kotlin DataFrame in Kotlin Notebook

<web-summary>
Use Kotlin DataFrame directly in Kotlin Notebook — write code, explore results, and refine your analysis step by step in a live environment.
</web-summary>

<card-summary>
Start analyzing data with Kotlin DataFrame in Kotlin Notebook — live code execution, 
rich tables, and zero configuration required.
</card-summary>

<link-summary>
Follow a step-by-step introduction to Kotlin DataFrame in Kotlin Notebook: setup, first DataFrame, and interactive output — all in one place.
</link-summary>


[**Kotlin Notebook**](https://kotlinlang.org/docs/kotlin-notebook-overview.html) is an interactive environment
integrated into [IntelliJ IDEA](https://www.jetbrains.com/idea/) (and can be easily added in
[Android Studio](https://developer.android.com/studio)), designed for fast, iterative,
and visual data exploration with Kotlin.

The Kotlin Notebook plugin transforms IntelliJ IDEA into a powerful data science workspace,
combining Kotlin’s strong language features with live code execution,
interactive data exploration, and rich visualizations.

It’s perfect for working with Kotlin DataFrame — letting you write code, view results instantly,
and refine your analysis step by step.


## Create a Kotlin notebook

<tip>
Before version 2025.1, Kotlin Notebook is unavailable in IntelliJ IDEA Community Edition
and not bundled in IntelliJ IDEA Ultimate Edition by default.
</tip>

* Make sure the [Kotlin Notebook plugin is enabled](https://kotlinlang.org/docs/kotlin-notebook-set-up-env.html).

* Open an IntelliJ IDEA project (either new or existing).

* In the project view, create a new Kotlin Notebook file:  
  Press <shortcut key="$NewFile"/> or right-click the project tree, then select
  <ui-path>Kotlin Notebook</ui-path>.

![Create Kotlin Notebook file](new_ktn_file.png){width="200"}

For more details, see  
[Get started with Kotlin Notebook](https://kotlinlang.org/docs/get-started-with-kotlin-notebooks.html)  
on the official [Kotlin Documentation](https://kotlinlang.org/docs/home.html) website.

## Integrate Kotlin DataFrame

In the new notebook file, execute the following cell to add the Kotlin DataFrame library:

```
%use dataframe
```

This will load all necessary dependencies, add required imports, and enable rich DataFrame rendering in the notebook.

### Specify a version

By default, if no version is specified, the version bundled with the notebook kernel is used.  
You can explicitly define the version you want:


```
%use dataframe(1.0.0)
```

Or use the latest stable version of Kotlin DataFrame
(specified in [Kotlin Jupyter descriptors](https://github.com/Kotlin/kotlin-jupyter-libraries)):


```
%useLatestDescriptors
%use dataframe
```

## Hello World

Let’s create your first [`DataFrame`](DataFrame.md) in the notebook — a simple "Hello, World!" style example:

```kotlin
val df = dataFrameOf(
  "name" to listOf("Alice", "Bob"),
  "age" to listOf(25, 30)
)
```

To display it, run a cell with the `DataFrame` variable in the last line (or simply a cell containing the variable):

```kotlin
df
```

You will see the content of this `DataFrame` rendered as an interactive table directly in the cell output:

![df_output](df_output.png) {height="168"}

## Next Steps

* Once you’ve successfully set up Kotlin DataFrame in Kotlin Notebook,
you can move on to the [](quickstart.md)
which walks you through the basics of working with Kotlin DataFrame inside a notebook.

* For more advanced use cases, explore our collection of
[detailed guides and real-world examples](Guides-And-Examples.md),
showcasing how Kotlin DataFrame can help with a variety of data tasks.

* Discover powerful [](Kotlin-DataFrame-Features-in-Kotlin-Notebook.md)that
make exploring and understanding your data easier and more effective.
