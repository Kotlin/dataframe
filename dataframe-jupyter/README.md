## :dataframe-jupyter

This module, published as `dataframe-jupyter`, contains all logic and tests for DataFrame to be able to work
in [Jupyter notebooks](https://kotlin.github.io/dataframe/gettingstartedjupyternotebook.html)
and the [Kotlin Notebook IntelliJ Plugin](https://kotlin.github.io/dataframe/usage-with-kotlin-notebook-plugin.html).

The main integration point is at [Integration.kt](src/main/kotlin/org/jetbrains/kotlinx/dataframe/jupyter/Integration.kt).
This is what will be called when people write `%use dataframe` thanks to our
[Kotlin Notebook library descriptor](https://github.com/Kotlin/kotlin-jupyter-libraries/blob/master/dataframe.json).

This module is a friend module of [`:core`](../core) to be able to access internal APIs.

See [Get started with Kotlin DataFrame on Jupyter Notebook](https://kotlin.github.io/dataframe/gettingstartedjupyternotebook.html),
and [Usage with Kotlin Notebook Plugin](https://kotlin.github.io/dataframe/usage-with-kotlin-notebook-plugin.html).

This module targets java 11 because of the restriction from `org.jetbrains.kotlin.jupyter`.
