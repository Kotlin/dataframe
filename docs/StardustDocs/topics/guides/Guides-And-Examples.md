# Guides And Examples

<web-summary>
Browse a collection of guides and examples covering key features and real-world use cases of Kotlin DataFrame — from basics to advanced data analysis.
</web-summary>

<card-summary>
Explore Kotlin DataFrame with detailed user guides and real-world examples, 
showcasing practical use cases and data workflows.
</card-summary>

<link-summary>
A curated list of Kotlin DataFrame guides and examples that walk you through common operations and data analysis patterns step by step.
</link-summary>

<!--- TODO: add more guides (migration from pandas and others) and replace GH notebooks with topics --->

## Guides

Explore our structured, in-depth guides to steadily improve your Kotlin DataFrame skills — step by step.

* [](quickstart.md) — get started with Kotlin DataFrame in a few simple steps:
  load data, transform it, and visualize it.

<img src="quickstart_preview.png" border-effect="rounded" width="705"/>

* [](Guide-for-backend-SQL-developers.md) — migration guide for backend developers with SQL/ORM experience moving to Kotlin DataFrame

* [](extensionPropertiesApi.md) — learn about extension properties for [`DataFrame`](DataFrame.md) 
and make working with your data both convenient and type-safe.

* [Enhanced Column Selection DSL](https://blog.jetbrains.com/kotlin/2024/07/enhanced-column-selection-dsl-in-kotlin-dataframe/)
  — explore powerful DSL for typesafe and flexible column selection in Kotlin DataFrame.
* [](Kotlin-DataFrame-Features-in-Kotlin-Notebook.md)
  — discover interactive Kotlin DataFrame outputs in
  [Kotlin Notebook](https://kotlinlang.org/docs/kotlin-notebook-overview.html).

<img src="ktnb_features_preview.png" border-effect="rounded" width="705"/>

* [40 Puzzles](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/puzzles/40%20puzzles.ipynb)
  — inspired by [100 pandas puzzles](https://github.com/ajcr/100-pandas-puzzles).
  An interactive guide that takes you from simple tasks to complex challenges,
  teaching you how to solve them using Kotlin DataFrame in a concise and elegant style.
* [Reading from files: CSV, JSON, ApacheArrow](read.md)
  — read your data from various formats into `DataFrame`.
* [SQL Databases Interaction](readSqlDatabases.md)
  — set up SQL database access and read query results efficiently into `DataFrame`.
* [Custom SQL Database Support](readSqlFromCustomDatabase.md)
  — extend DataFrame library for custom SQL database support.
* [GeoDataFrame Guide](https://kotlin.github.io/kandy/geo-plotting-guide.html)
  — explore the GeoDataFrame module that brings a convenient Kotlin DataFrame API to geospatial workflows,
  enhanced with beautiful Kandy-Geo visualizations (*experimental*).


<img src="geoguide_preview.png" border-effect="rounded" width="705"/>


* [Using Unsupported Data Sources](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples):
  — A guide by examples. While these might one day become proper integrations of DataFrame, for now,
  we provide them as examples for how to make such integrations yourself.
    * [Apache Spark Interop (With and Without Kotlin Spark API)](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/spark)
    * [Multik Interop](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/multik)
    * [JetBrains Exposed Interop](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/exposed)
    * [Hibernate ORM](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/hibernate)
* [OpenAPI Guide](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/json/KeyValueAndOpenApi.ipynb)
  — learn how to parse and explore [OpenAPI](https://swagger.io) JSON structures using Kotlin DataFrame,
  enabling structured access and intuitive analysis of complex API schemas (*experimental*, supports OpenAPI 3.0.0).

## Examples

Explore our extensive collection of practical examples and real-world analytics workflows.

* [Kotlin DataFrame Compiler Plugin Example](https://github.com/Kotlin/dataframe/blob/master/examples/kotlin-dataframe-plugin-example)  
  — a simple project demonstrating the usage of the [compiler plugin](Compiler-Plugin.md),  
  showcasing DataFrame expressions with [extension properties](extensionPropertiesApi.md)  
  that are generated on-the-fly in the IDEA project.

* [Titanic Example](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/titanic/Titanic.ipynb)
  — discover the famous "Titanic"
  dataset with the Kotlin DataFrame analysis toolkit
  and [Kandy](https://kotlin.github.io/kandy/) visualizations.

* [Track and Analyze GitHub Star Growth](https://blog.jetbrains.com/kotlin/2024/08/track-and-analyze-github-star-growth-with-kandy-and-kotlin-dataframe/)
  — query GitHub’s API with the Kotlin Notebook Ktor client,
  then analyze and visualize the data using Kotlin DataFrame and [Kandy](https://kotlin.github.io/kandy/).

* [GitHub Example](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/github/github.ipynb)
  — a practical example of working with deeply nested, hierarchical DataFrames using GitHub data.

* [Netflix Example](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/netflix/netflix.ipynb)
  — explore TV shows and movies from Netflix with the powerful Kotlin DataFrame API and beautiful
  [Kandy](https://kotlin.github.io/kandy/) visualizations.

* [Top-12 German Companies Financial Analyze](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/top_12_german_companies)
  — analyze key financial metrics for several major German companies.

* [Movies Example](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/movies/movies.ipynb)
  — basic Kotlin DataFrame operations on data from [movielens](https://movielens.org/).

* [YouTube Example](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/youtube/Youtube.ipynb)
  — explore YouTube videos with YouTube REST API and Kotlin DataFrame.

* [IMDb SQL Database Example](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples/blob/master/notebooks/imdb.ipynb)
  — analyze IMDb data stored in MariaDB using Kotlin DataFrame
  and visualize with [Kandy](https://kotlin.github.io/kandy/).

* [Reading Parquet files from Apache Spark](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/spark-parquet-dataframe)
  — this project showcases how to export data and ML models from Apache Spark via reading from Parquet files. 
Also, [Kandy](https://kotlin.github.io/kandy/) used to visualize the exported data and Linear Regression model.

See also [Kandy User Guides](https://kotlin.github.io/kandy/user-guide.html)
and [Examples Gallery](https://kotlin.github.io/kandy/examples.html)
for the best data visualizations using Kotlin DataFrame and Kandy together!

<img src="kandy_gallery_preview.png" border-effect="rounded" width="705"/>
