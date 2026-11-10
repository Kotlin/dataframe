# Examples of Kotlin DataFrame

## IntelliJ IDEA project examples

You don't need to clone the repository to build and explore these projects.
Download an individual project using the links below or the **Download this Example** link in its README.

#### [Gradle plugin example](projects/kotlin-dataframe-plugin-gradle-example)

An IntelliJ IDEA project showing how to use the
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html) with Gradle.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-dataframe-plugin-gradle-example.zip)

#### [Maven plugin example](projects/kotlin-dataframe-plugin-maven-example)

An IntelliJ IDEA project showing how to use the
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html) with Maven.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-dataframe-plugin-maven-example.zip)

#### [Kotlin Toolchain example](projects/kotlin-dataframe-plugin-kotlin-toolchain-example)

An IntelliJ IDEA project showing how to use the
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html) with Kotlin Toolchain.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-dataframe-plugin-kotlin-toolchain-example.zip)

#### [Android example](projects/android-example)

A minimal Android project showcasing Kotlin DataFrame integration, including the
[Kotlin DataFrame Compiler Plugin](https://kotlin.github.io/dataframe/compiler-plugin.html).

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/android-example.zip)

#### [Movies](projects/movies)

Explore basic DataFrame operations and type-safe column access using data from [MovieLens](https://movielens.org/).

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/movies.zip)

#### [Titanic](projects/titanic)

Combine Kotlin DataFrame with [KotlinDL](https://github.com/Kotlin/kotlindl)
to explore deep-learning techniques using the Titanic dataset.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/titanic.zip)

#### [YouTube](projects/youtube)

Work with YouTube data using the [YouTube Data API](https://developers.google.com/youtube/v3) and Kotlin DataFrame.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/youtube.zip)

#### [JSON and OpenAPI](projects/json-openapi)

Generate data schemas for JSON data from [APIs.guru](https://apis.guru/) and read JSON
using types defined in an OpenAPI specification.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/json-openapi.zip)

#### [Spark and Parquet](projects/spark-parquet-dataframe)

Read Parquet files to import data and ML models exported from Apache Spark.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/spark-parquet-dataframe.zip)

#### [IMDb SQL database](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples)

Convert SQL tables and query results into Kotlin DataFrames.

[Download this example](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples/archive/refs/heads/master.zip)

### Examples of manual integrations with other libraries
DataFrame provides modules that wrap other libraries, for example, to support reading Excel files, or JDBC ResultSets, etc. But capacity of working with other libraries is not limited only by that.

Following examples showcase of how to use DataFrame with (momentarily) unsupported data libraries such as
  [Spark](https://spark.apache.org/) and [Exposed](https://github.com/JetBrains/Exposed).
  They show how to convert to and from Kotlin DataFrame and their respective tables.

#### [JetBrains Exposed](projects/exposed)

Convert between Kotlin DataFrame and [Exposed](https://github.com/JetBrains/Exposed) tables.

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/exposed.zip)

#### [Hibernate](projects/hibernate)

Use Kotlin DataFrame with [Hibernate](https://hibernate.org/orm/).

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/hibernate.zip)

#### [Apache Spark](projects/kotlin-spark)

Convert between Kotlin DataFrame and [Apache Spark](https://spark.apache.org/) DataFrames,
including examples using the [Kotlin Spark API](https://github.com/JetBrains/kotlin-spark-api).

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/kotlin-spark.zip)

#### [Multik](projects/multik)

Use Kotlin DataFrame with [Multik](https://github.com/Kotlin/multik).

[Download this example](https://github.com/Kotlin/dataframe/raw/example-projects-archives/multik.zip)

### Building and maintaining the examples

The example projects in [examples/projects](projects) always target the latest stable version of the library.
Projects compatible with the latest dev/master version are located in the [examples/projects/dev](projects/dev) folder.

All examples are tested by the main project in debug mode when `gradlew test` is run
(or manually by `gradlew runBuildAllExampleFolders -Pkotlin.dataframe.debug=true`).
Android example projects require the `android.sdk.dir=` property to be set.

Versions inside the example projects are overwritten automatically upon `gradlew assemble`
(or manually by `gradlew syncAllExampleFolders`).

See the [convention plugins README](../build-logic/README.md) for more information.

## Notebook examples

* puzzles [notebook](notebooks/puzzles/40%20puzzles.ipynb) &ndash;
Inspired [by 100 pandas puzzles](https://github.com/ajcr/100-pandas-puzzles). You will go from the simplest tasks to 
complex problems where you need to think. This notebook will show you how to solve these tasks with Kotlin 
DataFrame in a laconic, beautiful style.
___
* movies [notebook](notebooks/movies/movies.ipynb) &ndash;
In this notebook you can see the basic operations of the Kotlin DataFrame on data from [movielens](https://movielens.org/).
You can take the data from the [link](https://grouplens.org/datasets/movielens/latest/).
___
* netflix [notebook](notebooks/netflix/netflix.ipynb) &ndash;
Explore TV shows and movies from Netflix with the powerful Kotlin DataFrame API and beautiful
visualizations from [lets-plot](https://github.com/JetBrains/lets-plot-kotlin).
___
* github [notebook](notebooks/github/github.ipynb) &ndash;
This notebook shows what hierarchical dataframes look like and how to work with them.
___
* titanic [notebook](notebooks/titanic/Titanic.ipynb) &ndash;
Let's see how the new library will show itself on the famous Titanic dataset.
___
* Financial Analysis of the top-12 German companies [notebook](notebooks/top_12_german_companies) &ndash;
  Analyze key financial metrics for several major German companies.
___
* wine [notebook](notebooks/wine/WineNetWIthKotlinDL.ipynb) &ndash;
  Wine. Kotlin DataFrame. KotlinDL. What came out of this can be seen in this notebook.
___
* youtube [notebook](notebooks/youtube/Youtube.ipynb) &ndash;
Explore YouTube videos with YouTube REST API and Kotlin DataFrame 

___
* imdb sql database ([notebook](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples/blob/master/notebooks/imdb.ipynb)) &ndash; In this notebook, we use Kotlin DataFrame and Kandy library to analyze data from [IMDB](https://datasets.imdbws.com/) (SQL dump for the MariaDB database with the name "imdb" could be downloaded by this [link](https://drive.google.com/file/d/10HnOu0Yem2Tkz_34SfvDoHTVqF_8b4N7/view?usp=sharing)).

---
* Feature Overviews [notebook folder](notebooks/feature_overviews)
  Overview of new features available a given version

The example notebooks always target the latest stable version of the library.
Notebooks compatible with the latest dev/master version are located in the [dev](notebooks/dev) folder.

These [dev versions](notebooks/dev) are tested by the 
[:dataframe-jupyter module](../dataframe-jupyter/src/test/kotlin/org/jetbrains/kotlinx/dataframe/jupyter).
