# Migration Guide: Pandas to Kotlin DataFrame

<web-summary>
Quickly transition from Pandas to Kotlin DataFrame: load your datasets, perform essential transformations, and visualize your results — directly within a Kotlin Notebook.
</web-summary>

<card-summary>
Switching from Pandas? Kotlin DataFrame makes it easy to load, process, analyze, and visualize your data — fully interactive and type-safe!
</card-summary>

<link-summary>
Explore Kotlin DataFrame as a Pandas user: read your data, transform columns, group or join tables, and build insightful visualizations with Kotlin Notebook.
</link-summary>

This guide helps Pandas users easily transition to **Kotlin DataFrame**, translating common tasks from Python/Pandas into Kotlin DataFrame operations.

We recommend [starting with **Kotlin Notebook**](gettingStartedKotlinNotebook.md) for the best beginner experience.
It's similar to Jupyter Notebook, but built directly into your IDE. 
You can use IDE features, include it in your Gradle or Maven project, and work comfortably within the JVM ecosystem. 
It also provides interactive data exploration, DataFrame rendering, and plotting out of the box.  

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.guides.QuickStartGuide-->

## Quick Setup

To start working with Kotlin DataFrame in a Kotlin Notebook, run the cell with the next code:

```kotlin
%useLatestDescriptors
%use dataframe
```

This will load all necessary DataFrame dependencies (of the latest stable version) and all imports, as well as DataFrame
rendering. Learn more [here](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe).

---

## Essential Kotlin Basics

### Variables
- Use `val` to define variables that cannot change after initialization.
- Use `var` for variables that can be reassigned.

### Lambdas
- Lambdas (`{ it.column > 0 }`) are compact functions used extensively for filtering and transformations.
- `it` refers to the current row or element in a DataFrame context.

### Nullability
- Kotlin uses explicit nullability (`String?` means the value can be null).
- Kotlin DataFrame safely handles missing data by clearly marking nullable types.

### Method Chaining
- Kotlin DataFrame methods return new dataframes, not modifying the original.
- This functional approach encourages cleaner, safer pipelines.

---

## Loading Data

| Task             | Pandas                                      | Kotlin DataFrame                               |
|------------------|---------------------------------------------|------------------------------------------------|
| Load CSV         | `pd.read_csv("data.csv")`                   | `DataFrame.readCSV("data.csv")`                |
| Load JSON        | `pd.read_json("data.json")`                 | `DataFrame.readJSON("data.json")`              |
| Load from SQL    | `pd.read_sql("SELECT * FROM table", conn)`  | `DataFrame.readSqlTable("table", jdbcUrl)`     |
| Load from Arrow  | `pd.read_feather("data.arrow")`             | `DataFrame.readArrow("data.arrow")`            |

---

## Inspecting Data

| Task              | Pandas                 | Kotlin DataFrame    |
|-------------------|------------------------|---------------------|
| Preview rows      | `df.head()`            | `df.head()`         |
| Get shape         | `df.shape`             | `df.nrow`, `df.ncol`|
| Column types      | `df.dtypes`            | `df.schema()`       |
| Count missing     | `df.isnull().sum()`    | `df.na.count()`     |

---

## Selecting Data

| Task                | Pandas                | Kotlin DataFrame                |
|---------------------|-----------------------|---------------------------------|
| Select single col   | `df["col"]`           | `df["col"]` or `df.col`         |
| Multiple columns    | `df[["a", "b"]]`      | `df.select { a and b }`         |
| Filter rows         | `df[df.a > 10]`       | `df.filter { a > 10 }`          |

---

## Transforming Data

| Task               | Pandas                              | Kotlin DataFrame                     |
|--------------------|-------------------------------------|--------------------------------------|
| Add column         | `df["c"] = df.a + df.b`             | `df.add("c") { a + b }`              |
| Modify column      | `df["a"] = df["a"] * 100`           | `df.update { a from { it * 100 } }`  |
| Rename column      | `df.rename(columns={"old":"new"})`  | `df.rename { "old" to "new" }`       |
| Drop column        | `df.drop("col", axis=1)`            | `df.remove("col")`                   |

---

## Grouping and Aggregation

| Task                     | Pandas                                | Kotlin DataFrame                         |
|--------------------------|---------------------------------------|------------------------------------------|
| Group & sum              | `df.groupby("col").sum()`             | `df.groupBy { col }.aggregate { sum() }` |
| Multiple aggregations    | `agg({"a":"mean","b":"sum"})`         | `aggregate { mean(a) and sum(b) }`       |

---

## Joining DataFrames

| Task              | Pandas                            | Kotlin DataFrame                    |
|-------------------|-----------------------------------|-------------------------------------|
| Inner join        | `df1.merge(df2, on="id")`         | `df1.join(df2, by = "id")`          |
| Left join         | `df1.merge(df2, how="left")`      | `df1.join(df2, type = JoinType.Left)`|

---

## Exporting/Saving Data

| Task           | Pandas                                 | Kotlin DataFrame                             |
|----------------|----------------------------------------|----------------------------------------------|
| Save to CSV    | `df.to_csv("file.csv", index=False)`   | `df.writeCSV("file.csv")`                    |
| Save to JSON   | `df.to_json("file.json")`              | `df.writeJSON("file.json")`                  |
| Save to Arrow  | `df.to_feather("file.arrow")`          | `df.writeArrow("file.arrow")`                |

---

## Example Data Pipeline A: Filtering & Aggregation

### Pandas (Matplotlib)

```python

df = pd.read_csv("sales.csv")
df = df[df.amount > 0]
summary = df.groupby("region").amount.sum().reset_index()


```

### Kotlin DataFrame (Kandy)

```kotlin
val df = DataFrame.readCSV("sales.csv")
val summary = df
    .filter { amount > 0 }
    .groupBy { region }
    .aggregate { sum(amount).into("total") }
```

---

## Example Data Pipeline B: Add Column & Pivoting

### Pandas (Matplotlib)

```python
df["profit"] = df.revenue - df.cost
pivot = df.pivot_table(values="profit", index="product", columns="year")
```

### Kotlin DataFrame (Kandy)

```kotlin
val pivot = df
    .add("profit") { revenue - cost }
    .pivot(product, year) { mean(profit) }
```

---

## Visualization Example

### Pandas (Matplotlib)

```python
import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("sales.csv")
df.groupby('region')['amount'].sum().plot(kind='bar')
plt.title("Sales by Region")
plt.xlabel("Region")
plt.ylabel("Amount")
plt.show()
```

### Kotlin DataFrame (Kandy)

```kotlin
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save

val df = DataFrame.readCSV("sales.csv")
val summary = df.groupBy { region }.aggregate { sum(amount).into("total") }

summary.plot {
    bar {
        x(region)
        y("total")
    }
    layout.title = "Sales by Region"
    xAxisLabel = "Region"
    yAxisLabel = "Amount"
}.save("sales_by_region.png")
```

---

## In conclusion

- Kotlin DataFrame prioritizes type safety and readability.
- Some Pandas features (like advanced time indexing, resample()) aren’t available yet.
- Use Kotlin’s typed DSL for error-free refactoring and improved IDE support.


## What's Next?
If you're ready to go through a complete example, we recommend our [Quickstart Guide](quickstart.md) 
— you'll learn the basics of reading data, transforming it, and creating visualization step-by-step.  

Ready to go deeper? Check out what’s next:

- 📘 **[Explore in-depth guides and various examples](Guides-And-Examples.md)** with different datasets,
  API usage examples, and practical scenarios that help you understand the main features of Kotlin DataFrame.

- 🛠️ **[Browse the operations overview](operations.md)** to learn what Kotlin DataFrame can do.

- 🧠 **Understand the design** and core concepts in the [library overview](concepts.md).

- 🔤 **[Learn more about Extension Properties](extensionPropertiesApi.md)**  
  and make working with your data both convenient and type-safe.

- 💡 **[Use Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md)**  
  for auto-generated column access in your IntelliJ IDEA projects.

- 📊 **Master Kandy** for stunning and expressive DataFrame visualizations learning
  [Kandy Documentation](https://kotlin.github.io/kandy).
