# Examples of Kotlin Dataframe

### Idea examples
* [movies](idea-examples/movies) Using extension properties [Access API](https://kotlin.github.io/dataframe/apilevels.html) to perform a data cleaning task
* [titanic](idea-examples/titanic)
* [youtube](idea-examples/youtube)
* [json](idea-examples/json) Using OpenAPI support in DataFrame's Gradle and KSP plugins to access data from [API guru](https://apis.guru/) in a type-safe manner
* [imdb sql database](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples) This project prominently showcases how to convert data from an SQL table to a Kotlin DataFrame 
and how to transform the result of an SQL query into a DataFrame.
* [unsupported-data-sources](idea-examples/unsupported-data-sources) Showcases of how to use DataFrame with
  (momentarily) unsupported data libraries such as [Spark](https://spark.apache.org/) and [Exposed](https://github.com/JetBrains/Exposed).
They show how to convert to and from Kotlin Dataframe and their respective tables.
  * **JetBrains Exposed**: See the [exposed folder](./idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/exposed)
    for an example of using Kotlin Dataframe with [Exposed](https://github.com/JetBrains/Exposed).
  * **Apache Spark**: See the [spark folder](./idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/spark)
    for an example of using Kotlin Dataframe with [Spark](https://spark.apache.org/).
  * **Spark (with Kotlin Spark API)**: See the [kotlinSpark folder](./idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/kotlinSpark)
    for an example of using Kotlin DataFrame with the [Kotlin Spark API](https://github.com/JetBrains/kotlin-spark-api).
  * **Multik**: See the [multik folder](./idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/multik)
    for an example of using Kotlin Dataframe with [Multik](https://github.com/Kotlin/multik).


### Notebook examples

* people ([Datalore](https://datalore.jetbrains.com/view/notebook/aOTioEClQQrsZZBKeUPAQj)) &ndash;
Small artificial dataset used in [DataFrame API examples](https://kotlin.github.io/dataframe/operations.html) 
___
* puzzles ([notebook](notebooks/puzzles/40%20puzzles.ipynb)/[Datalore](https://datalore.jetbrains.com/view/notebook/CVp3br3CDXjUGaxxqfJjFF)) &ndash;
Inspired [by 100 pandas puzzles](https://github.com/ajcr/100-pandas-puzzles). You will go from the simplest tasks to 
complex problems where need to think. This notebook will show you how to solve these tasks with the Kotlin 
Dataframe in a laconic, beautiful style.
___
* movies ([notebook](notebooks/movies/movies.ipynb)/[Datalore](https://datalore.jetbrains.com/view/notebook/89IMYb1zbHZxHfwAta6eKP)) &ndash;
In this notebook you can see the basic operations of the Kotlin Dataframe on data from [movielens](https://movielens.org/).
You can take the data from the [link](https://grouplens.org/datasets/movielens/latest/).
___
* netflix ([notebook](notebooks/netflix/netflix.ipynb)/[Datalore](https://datalore.jetbrains.com/view/notebook/xSJ4rx49hcH71pPnFgZBCq)) &ndash;
Explore TV shows and movies from Netflix with the powerful Kotlin Dataframe API and beautiful
visualizations from [lets-plot](https://github.com/JetBrains/lets-plot-kotlin).
___
* github ([notebook](notebooks/github/github.ipynb)/[Datalore](https://datalore.jetbrains.com/view/notebook/P9n6jYL4mmY1gx3phz5TsX)) &ndash;
This notebook shows the hierarchical dataframes look like and how to work with them.
___
* titanic ([notebook](notebooks/titanic/Titanic.ipynb)/[Datalore](https://datalore.jetbrains.com/view/notebook/B5YeMMONSAR78FgKQ9yJyW)) &ndash;
Let's see how the new library will show itself on the famous Titanic dataset.
___
* Financial Analyze of the top-12 German companies ([notebook](notebooks/top_12_german_companies)/[Datalore](https://datalore.jetbrains.com/report/static/KQKedA4jDrKu63O53gEN0z/MDg5pHcGvRdDVQnPLmwjuc)) &ndash;
  Analyze key financial metrics for several major German companies.
___
* wine ([notebook](notebooks/wine/WineNetWIthKotlinDL.ipynb)/[Datalore](https://datalore.jetbrains.com/view/notebook/aK9vYHH8pCA8H1KbKB5WsI)) &ndash;
  Wine. Kotlin Dataframe. KotlinDL. What came out of this can be seen in this notebook.
___
* youtube ([notebook](notebooks/youtube/Youtube.ipynb)/[Datalore](https://datalore.jetbrains.com/view/notebook/uXH0VfIM6qrrmwPJnLBi0j)) &ndash;
Explore YouTube videos with YouTube REST API and Kotlin Dataframe 

___
* imdb sql database ([notebook](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples/blob/master/notebooks/imdb.ipynb)) &ndash; In this notebook, we use Kotlin DataFrame and Kandy library to analyze data from [IMDB](https://datasets.imdbws.com/) (SQL dump for the MariaDB database with the name "imdb" could be downloaded by this [link](https://drive.google.com/file/d/10HnOu0Yem2Tkz_34SfvDoHTVqF_8b4N7/view?usp=sharing)).

---
* Feature Overviews [notebook folder](notebooks/feature_overviews)
  Overview of new features available a given version

The example notebooks always target the latest stable version of the library.
Notebooks compatible with the latest dev/master version are located in the [dev](notebooks/dev) folder.

These [dev versions](notebooks/dev) are tested by the 
[:dataframe-jupyter module](../dataframe-jupyter/src/test/kotlin/org/jetbrains/kotlinx/dataframe/jupyter).
