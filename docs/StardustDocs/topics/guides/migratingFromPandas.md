# Coming from Pandas

<web-summary>
Moving from Pandas to Kotlin DataFrame: the same workflows side by side, and the few places
where the mental model is different on purpose.
</web-summary>

<card-summary>
Know Pandas? See the same everyday tasks in Kotlin DataFrame, and what works differently and why.
</card-summary>

<link-summary>
A guide for Pandas users: common tasks side by side, and the key differences — types, immutability, nulls, grouping.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.guides.MigratingFromPandas-->

You already know how to load, clean, group and join tables. This page shows the same tasks in
Kotlin DataFrame, and explains the few places where it works differently on purpose.

It has two parts:

- **This page** is a short tour: the ideas first, then the everyday tasks side by side.
- **[The Pandas to Kotlin DataFrame reference](pandasToKotlinDataFrameReference.md)** is a long, exact lookup table.
  It is written so that a coding agent can use it — see [Using an AI coding agent](#using-an-ai-coding-agent).

## Set up

The quickest start is a [Kotlin Notebook](SetupKotlinNotebook.md#integrate-kotlin-dataframe).
It is like Jupyter, but inside IntelliJ IDEA. Run this in a cell:

```text
%use dataframe
```

In a Gradle or Maven project, add the library and, for type-safe columns, the compiler plugin.
See [Setup](Setup.md) and [Compiler plugin](Compiler-Plugin.md).

## Five ideas to take with you

| In Pandas                                              | In Kotlin DataFrame                                                                                        |
|--------------------------------------------------------|------------------------------------------------------------------------------------------------------------|
| A column is a string key, checked when the code runs.  | A column is a typed property, checked when the code compiles: `df.revenue`. See [schemas](schemas.md).    |
| Many operations can change the frame in place.         | Nothing changes in place. Every operation returns a new [`DataFrame`](DataFrame.md); the old one stays as it was. |
| A mask picks rows: `df[df.revenue > 100]`.             | A lambda picks rows: [`df.filter { revenue > 100 }`](filter.md). Inside it, the columns of the current row are properties. |
| `NaN` stands for missing, and it spreads quietly.      | A missing value is `null`, and the column type says so (`Double?`). The compiler makes you handle it. See [NaN and NA](nanAndNa.md). |
| `groupby` gives you an object you can call methods on. | [`groupBy`](groupBy.md) gives you a group you must finish with an aggregation (`sum`, `aggregate`, ...).  |

Two more differences you will meet on day one. There is no index, so rows are numbered by position and there is
no `loc`, `iloc` or `reset_index`. And a column can be read in two ways: as a property (`revenue`) or by name and
type, inside a row lambda (`"revenue"<Int>()`) — see [Access APIs](apiLevels.md) and [String API](StringApi.md).

## The same tasks side by side

All examples use one small table of sales: six rows, with a `discount` that is missing in two of them.
The first example creates it; `df` in every later example is that table.

### Create a dataframe

<tabs>
<tab title="Pandas">

```python
import pandas as pd

df = pd.DataFrame({
    "region": ["North", "North", "South", "South", "North", "South"],
    "product": ["Pen", "Ink", "Pen", "Ink", "Pen", "Pen"],
    "year": [2023, 2023, 2023, 2024, 2024, 2024],
    "revenue": [100, 150, 80, 200, 120, 90],
    "cost": [60, 90, 70, 120, 70, 50],
    "discount": [0.1, None, 0.05, None, 0.2, 0.0],
})
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasCreateDataFrame-->

```kotlin
val df = dataFrameOf(
    "region" to listOf("North", "North", "South", "South", "North", "South"),
    "product" to listOf("Pen", "Ink", "Pen", "Ink", "Pen", "Pen"),
    "year" to listOf(2023, 2023, 2023, 2024, 2024, 2024),
    "revenue" to listOf(100, 150, 80, 200, 120, 90),
    "cost" to listOf(60, 90, 70, 120, 70, 50),
    "discount" to listOf(0.1, null, 0.05, null, 0.2, 0.0),
)
```

<inline-frame src="./resources/pandasCreateDataFrame.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

### Read a file

<tabs>
<tab title="Pandas">

```python
df = pd.read_csv("sales.csv")
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasReadCsv-->

```kotlin
val df = DataFrame.readCsv("sales.csv")
```

<!---END-->

</tab>
</tabs>

An empty cell becomes `null`, and the column type becomes nullable (`discount: Double?`).
Other file formats are read with `readJson`, `readExcel`, `readArrowFeather` and `readParquet`. A database is read
with `readSqlTable` or `readSqlQuery`, and they take a connection, not a path. See [Read](read.md).

### Filter rows

<tabs>
<tab title="Pandas">

```python
big_north = df[(df.revenue > 100) & (df.region == "North")]
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasFilterRows-->

```kotlin
df.filter { revenue > 100 && region == "North" }
```

<inline-frame src="./resources/pandasFilterRows.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

### Add a column

<tabs>
<tab title="Pandas">

```python
with_profit = df.assign(profit=df.revenue - df.cost)
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasAddColumn-->

```kotlin
df.add("profit") { revenue - cost }
```

<inline-frame src="./resources/pandasAddColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

### Missing values

<tabs>
<tab title="Pandas">

```python
n_missing = df.discount.isna().sum()
filled = df.fillna({"discount": 0.0})
dropped = df.dropna(subset=["discount"])
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasCountNulls-->

```kotlin
df.count { discount == null }
```

<!---END-->

<!---FUN pandasFillMissingDiscount-->

```kotlin
df.fillNulls { discount }.with { 0.0 }
```

<inline-frame src="./resources/pandasFillMissingDiscount.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN pandasDropMissingDiscount-->

```kotlin
df.dropNulls { discount }
```

<inline-frame src="./resources/pandasDropMissingDiscount.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

Both libraries count the same 2 missing discounts. Here the two models really differ. In Pandas, arithmetic on a
column with gaps gives `NaN` for those rows and
carries on. In Kotlin DataFrame, `discount` is `Double?`, so you have to say what `null` means before the code
compiles:

<tabs>
<tab title="Pandas">

```python
# NaN for the two rows with no discount
net = df.revenue * (1 - df.discount)
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasNullableArithmetic-->

```kotlin
// `discount` is `Double?`: the compiler makes you decide what null means.
df.add("net") { revenue * (1 - (discount ?: 0.0)) }
```

<inline-frame src="./resources/pandasNullableArithmetic.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

### Group and aggregate

<tabs>
<tab title="Pandas">

```python
summary = df.groupby("region").agg(total=("revenue", "sum"), avgCost=("cost", "mean")).reset_index()
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasGroupByAggregate-->

```kotlin
df.groupBy { region }.aggregate {
    sum { revenue } into "total"
    mean { cost } into "avgCost"
}
```

<inline-frame src="./resources/pandasGroupByAggregate.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

Both libraries give the same numbers: `North` and `South` each total 370.

### Pivot

<tabs>
<tab title="Pandas">

```python
pivoted = df.pivot_table(values="revenue", index="region", columns="year", aggfunc="sum")
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasPivotRevenueByYear-->

```kotlin
df.pivot { year }.groupBy { region }.sum { revenue }.ungroup("year")
```

<inline-frame src="./resources/pandasPivotRevenueByYear.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

`pivot` puts the new columns into one column *group* called `year`. `ungroup("year")` moves them
to the top level, which is what `pivot_table` gives you directly.

### Join

<tabs>
<tab title="Pandas">

```python
managers = pd.DataFrame({"region": ["North", "South", "East"], "manager": ["Ann", "Bob", "Eve"]})
joined = managers.merge(df, on="region", how="left")
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasLeftJoin-->

```kotlin
val managers = dataFrameOf(
    "region" to listOf("North", "South", "East"),
    "manager" to listOf("Ann", "Bob", "Eve"),
)
managers.leftJoin(df, "region")
```

<inline-frame src="./resources/pandasLeftJoin.html" width="100%" height="500px"></inline-frame>

<!---END-->

</tab>
</tabs>

The key columns are plain arguments: `join(other, "region")` is an inner join, and `leftJoin`, `rightJoin` and
`fullJoin` are the others. There is no `on =` and no `by =`.

### Write a file

<tabs>
<tab title="Pandas">

```python
df.to_csv("out.csv", index=False)
df.to_json("out.json")
```

</tab>
<tab title="Kotlin DataFrame">

<!---FUN pandasWriteCsv-->

```kotlin
df.writeCsv(csv)
```

<!---END-->

<!---FUN pandasWriteJson-->

```kotlin
df.writeJson(json)
```

<!---END-->

</tab>
</tabs>

Here `csv` and `json` are `File`s; a `String` path works too.

## Things that will surprise you

These are real differences, not missing features. Each one is covered by tests on the same table.

- **Nothing changes in place.** `df.filter { revenue > 100 }` returns a new frame with 3 rows; `df` still has 6.
  There is no `df["profit"] = ...` — use `add`.
- **`describe()` is wider.** It describes every column, not only the numeric ones, and it has a `nulls` column
  (Pandas needs `isnull().sum()` for that).
- **Percentiles can differ.** The 25th percentile of `revenue` is `89.17` here and `92.5` in Pandas. Kotlin
  DataFrame estimates quantiles with method R8, Pandas by default with R7 (see
  [percentile](percentile.md#quantile-estimation-methods)), so do not compare quantiles between the two libraries
  number by number.
- **`drop(n)` drops rows, not columns.** It removes the first `n` rows (Pandas `drop(4)` removes the row labelled 4).
  To remove a column, use `remove`.
- **A single letter in a CSV is read as a `Char`**, not as a string: a column of `A`, `B` has type `Char`.
  Pandas keeps such a column as text (`object`, or `str` since Pandas 3.0).
- **Groups keep the order of first appearance.** `groupBy { region and product }` lists the groups as they first
  show up in the data and gives `220, 150, 170, 200`. Pandas sorts the groups by key and gives `150, 220, 200, 170`;
  `groupby(..., sort=False)` gives the same order as Kotlin DataFrame.
- **`pivot` gives a column group**, as shown above.

## Using an AI coding agent

Coding agents can write Pandas-style calls that do not exist here, such as `df.groupby`, `df.shape`,
`df.na.count()` or `join(..., by = "id")`. Give your agent the
[Pandas to Kotlin DataFrame reference](pandasToKotlinDataFrameReference.md): it lists the real name for each Pandas
call, the Pandas names that do not exist, and the rules for writing code that compiles.

## What's next

- [Quickstart](quickstart.md) — a complete example, from reading data to plotting it.
- [Access APIs](apiLevels.md) — properties, strings, and when each one is available.
- [Column selectors](ColumnSelectors.md) — the `select { a and b }` language.
- [Group by](groupBy.md), [pivot](pivot.md), [join](join.md), [read](read.md), [write](write.md).
- [Migration to 1.0](MigrationTo_1_0.md) — if you used an older version of Kotlin DataFrame.
