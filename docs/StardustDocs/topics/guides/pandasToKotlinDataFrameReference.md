
# Pandas to Kotlin DataFrame reference

<web-summary>
An exact lookup from Pandas calls to Kotlin DataFrame calls, with the Pandas names that do not exist
and the rules for writing code that compiles. Written for coding agents and for careful readers.
</web-summary>

<card-summary>
Pandas call → Kotlin DataFrame call, the Pandas-isms that do not exist, and the rules for code that compiles.
</card-summary>

<link-summary>
Reference for translating Pandas code to Kotlin DataFrame: exact calls, missing names, behaviour differences.
</link-summary>

This page is a lookup table. For a short introduction with explanations, read
[Coming from Pandas](migratingFromPandas.md) first.

**Instructions for a coding agent.** Translate Pandas code with this page, not from memory: most Pandas method
names do not exist in Kotlin DataFrame, and the ones that do exist often return something different.
If a Pandas call is not in the tables below, do not invent a name — look it up in the
[operations overview](operations.md), or tell the user it is not covered.

## Rules for writing code that compiles

1. **Never mutate.** No `df["x"] = ...`, no `inplace`. Every operation returns a new `DataFrame`; assign the
   result to a new `val`.
2. **Row lambdas read columns as properties** — `df.filter { revenue > 100 }`. Properties exist only when the
   frame has a known schema: a notebook (`%use dataframe`), the compiler plugin, or `cast<Schema>()` with a
   `@DataSchema` interface. Without a schema, read a column by name and type: `"revenue"<Int>()`.
3. **Key columns are positional arguments, not named ones.** `df.join(other, "id")`, not `join(other, by = "id")`
   and not `merge(on = ...)`.
4. **Column selectors use `and`**: `select { region and revenue }`. Not a list, not `[["a", "b"]]`.
5. **Name a result column with `into`**: `sum(revenue) into "total"`. Inside `aggregate { }` write one
   aggregation per line.
6. **Handle `null` explicitly.** A missing value is `null`; the column type is nullable (`Double?`).
   In a row lambda use `?:`, `?.` or `!!`; at frame level use `fillNulls` / `dropNulls`.
7. **A group is not a result.** `groupBy { }` must be finished with `aggregate { }`, `sum { }`, `count()` or
   another aggregation.
8. **Prefer the current names.** `readCsv` / `writeCsv` / `readJson` / `writeJson` (CamelCase). The old
   `readCSV` / `writeCSV` still compile with a deprecation warning; `readJSON` / `writeJSON` do not exist.
9. **Imports.** In a notebook `%use dataframe` imports everything. In a project import the functions you use,
   for example `org.jetbrains.kotlinx.dataframe.api.filter` and `org.jetbrains.kotlinx.dataframe.io.readCsv`.

## Create, read, write

| Pandas                                  | Kotlin DataFrame                                                    | Notes                                              |
|-----------------------------------------|---------------------------------------------------------------------|----------------------------------------------------|
| `pd.DataFrame({"a": [1, 2]})`           | `dataFrameOf("a" to listOf(1, 2))`                                  | Add `.cast<Schema>()` to get properties.           |
| `pd.read_csv(path)`                     | `DataFrame.readCsv(path)`                                           | Empty cell → `null`. Single letters → `Char`.      |
| `pd.read_json(path)`                    | `DataFrame.readJson(path)`                                          |                                                    |
| `pd.read_feather(path)`                 | `DataFrame.readArrowFeather(path)`                                  | `readArrow` takes an `ArrowReader`, not a path.    |
| `pd.read_parquet(path)`                 | `DataFrame.readParquet(path)`                                       |                                                    |
| `pd.read_sql_table("name", engine)`     | `DataFrame.readSqlTable(connection, "name")`                        | First argument: `DbConnectionConfig`, `DataSource` or `Connection` — never a URL string. Types: see below. |
| `pd.read_sql_query("SELECT ...", engine)` | `DataFrame.readSqlQuery(connection, "SELECT * FROM sales")`       | Same first argument. Checked on PostgreSQL. |
| `df.to_csv(path, index=False)`          | `df.writeCsv(file)`                                                 | There is no index, so nothing to turn off.         |
| `df.to_json(path)`                      | `df.writeJson(file)`                                                |                                                    |
| `df.to_feather(path)`                   | `df.writeArrowFeather(file)`                                        | `writeArrow` does not exist.                       |
| `pd.read_excel(path)`                   | `DataFrame.readExcel(file)`                                         | Every number is read as `Double`, whole numbers too. |
| `df.to_excel(path, index=False)`        | `df.writeExcel(file)`                                               |                                                    |
| `df.to_parquet(path)`                   | not available                                                       | There is no Parquet writer in any module; reading works. |

## Inspect

| Pandas              | Kotlin DataFrame                          | Notes                                                              |
|---------------------|-------------------------------------------|--------------------------------------------------------------------|
| `df.head(n)`        | `df.head(n)` or `df.take(n)`              |                                                                    |
| `df.tail(n)`        | `df.tail(n)`                              |                                                                    |
| `df.shape`          | `df.rowsCount()`, `df.columnsCount()`, `df.size()` | `size()` prints `6 x 6`. `nrow` / `ncol` are not public.   |
| `len(df)`           | `df.rowsCount()`                          |                                                                    |
| `df.columns`        | `df.columnNames()`                        | `columns()` returns `DataColumn`s, not names.                      |
| `df.dtypes`         | `df.schema()`                             | Prints `name: Type`; a trailing `?` means nullable.                |
| `df.describe()`     | `df.describe()`                           | Describes *all* columns; has `nulls`, `top`, `freq`.               |
| `df.isnull().sum()` | `df.describe().select { name and nulls }` | One column: `df.count { discount == null }`.                       |
| `df["a"].nunique()` | `df.countDistinct { a }`                  |                                                                    |
| `df["a"].value_counts()` | `df.valueCounts { a }`               | Result has a `count` column.                                       |
| `df["a"].unique()`  | `df.select { a }.distinct()`              | Returns a `DataFrame`, not a list.                                 |

## Select, filter, sort

| Pandas                               | Kotlin DataFrame (properties)                | Kotlin DataFrame (strings)                   |
|--------------------------------------|----------------------------------------------|----------------------------------------------|
| `df["a"]`                            | `df.a` or `df["a"]`                          | `df["a"]`                                    |
| `df[["a", "b"]]`                     | `df.select { a and b }`                      | `df.select("a", "b")`                        |
| `df[df.a > 10]`                      | `df.filter { a > 10 }`                       | `df.filter { "a"<Int>() > 10 }`              |
| `df[(df.a > 1) & (df.s == "x")]`     | `df.filter { a > 1 && s == "x" }`            |                                              |
| `df.sort_values(["a", "b"])`         | `df.sortBy { a and b }`                      |                                              |
| `df.sort_values("a", ascending=False)` | `df.sortByDesc { a }`                      |                                              |
| `df.iloc[:n]` / `df.iloc[n:]`        | `df.take(n)` / `df.drop(n)`                  | `drop(n)` removes the first `n` **rows**.    |

## Add, change, rename, remove

| Pandas                                        | Kotlin DataFrame                                       | Notes                                          |
|-----------------------------------------------|--------------------------------------------------------|------------------------------------------------|
| `df["c"] = df.a + df.b` / `df.assign(c=...)`  | `df.add("c") { a + b }`                                | Returns a new frame.                           |
| `np.where(df.a > 1, "hi", "lo")`              | `df.add("c") { if (a > 1) "hi" else "lo" }`            |                                                |
| `df["a"] = df["a"] * 2`                       | `df.update { a }.with { it * 2 }`                      | Type may not change — use `convert` for that.  |
| `df.loc[df.y == 2024, "a"] *= 2`              | `df.update { a }.where { y == 2024 }.with { it * 2 }`  |                                                |
| `df.astype({"y": str})`                       | `df.convert { y }.to<String>()`                        |                                                |
| `df.rename(columns={"a": "b"})`               | `df.rename("a" to "b")` or `df.rename { a }.into("b")` | Not `rename { "a" to "b" }`.                   |
| `df.drop(columns=["a"])`                      | `df.remove { a }` or `df.remove("a")`                  | `drop` is for rows.                            |
| `df.a.map(lambda x: x * 2)`                   | `df.a.map { it * 2 }`                                  | Returns a `DataColumn`.                        |
| `df.copy()`                                   | *not needed* — frames are immutable                    |                                                |

## Missing values

| Pandas                          | Kotlin DataFrame                    | Notes                                                                 |
|---------------------------------|-------------------------------------|-----------------------------------------------------------------------|
| `df.d.isna().sum()`             | `df.count { d == null }`            | `NaN` is a separate thing: see [NaN and NA](nanAndNa.md).             |
| `df.fillna({"d": 0.0})`         | `df.fillNulls { d }.with { 0.0 }`   |                                                                       |
| `df.dropna(subset=["d"])`       | `df.dropNulls { d }`                | With no selector, all columns are checked.                            |
| `df.dropna()`                   | `df.dropNulls()`                    |                                                                       |
| `df.a * df.d` with `NaN` in `d` | `df.add("c") { a * (d ?: 0.0) }`    | Pandas propagates `NaN`; Kotlin refuses to compile until you decide.  |

## Group, aggregate, pivot

| Pandas                                                   | Kotlin DataFrame                                                                       |
|----------------------------------------------------------|----------------------------------------------------------------------------------------|
| `df.groupby("r").revenue.sum()`                          | `df.groupBy { r }.sum { revenue }`                                                     |
| `df.groupby("r").size()`                                 | `df.groupBy { r }.count()`                                                             |
| `df.groupby("r").agg(t=("rev", "sum"), m=("cost", "mean"))` | `df.groupBy { r }.aggregate { sum(rev) into "t"; mean(cost) into "m" }`             |
| `df.groupby(["r", "p"]).rev.sum()`                       | `df.groupBy { r and p }.aggregate { sum(rev) into "t" }`                               |
| `df.pivot_table(values="v", index="r", columns="y", aggfunc="sum")` | `df.pivot { y }.groupBy { r }.sum { v }.ungroup("y")`                        |

Notes:

- The result of `groupBy { }.sum { revenue }` keeps the column name `revenue`; use `aggregate` and `into` to rename.
- `df.groupBy { r }.aggregate { sum() }` compiles but is **not** `groupby().sum()`: it returns a single nested
  column `aggregated`. For all numeric columns use `df.groupBy { r }.sum()`.
- `pivot` returns a *column group*. Without `ungroup("y")` the new columns are nested inside `y`.
- Groups come out in the order of first appearance in the data. Pandas sorts them by key; `groupby(..., sort=False)`
  gives the Kotlin DataFrame order. Do not compare group results by position without sorting both sides.

## Combine

| Pandas                               | Kotlin DataFrame                       | Notes                                                       |
|--------------------------------------|----------------------------------------|-------------------------------------------------------------|
| `df1.merge(df2, on="id")`            | `df1.join(df2, "id")`                  | Also `innerJoin`. Result columns come from `df1`, then new ones from `df2`. |
| `df1.merge(df2, on="id", how="left")` | `df1.leftJoin(df2, "id")`             | Also `rightJoin`, `fullJoin`. Unmatched rows hold `null`.   |
| `pd.concat([df1, df2])`              | `df1.concat(df2)`                      | Rows are appended; there is no index to reset.              |
| `pd.concat([df1, df2], axis=1)`      | `df1.add(df2)`                         | Columns are appended.                                       |
| `df1.merge(df2, left_on="a", right_on="b")` | `df1.join(df2) { "a" match right["b"] }` | Keeps only the left key column; `merge` keeps both.   |

## Reshape and sequences

| Pandas                                        | Kotlin DataFrame                                     | Notes                                                        |
|-----------------------------------------------|------------------------------------------------------|--------------------------------------------------------------|
| `df.melt(id_vars=["r"], var_name="k", value_name="v")` | `df.select { r and a and b }.gather { a and b }.into("k", "v")` | `gather` lists the values row by row, `melt` column by column. `select` first: `gather` keeps every other column. |
| `df.explode("l")`                             | `df.explode { l }`                                   | `l` is a column of lists.                                    |
| `df.s.str.split("-", expand=True)`            | `df.split { s }.by("-").into("x", "y")`              | You name the new columns.                                    |
| `df.a.cumsum()`                               | `df.a.cumSum()`                                      | Capital `S`.                                                 |
| `df.a.shift(1)`                               | `df.add("prev") { prev()?.a }`                       | The first row is `null` (`NaN` in Pandas).                   |
| `df.a.diff()`                                 | `df.add("d") { diff(0) { a } }`                      | `diff` needs a value for the first row; here `0`. Compare with `df.a.diff().fillna(0)`. |
| `df.a.value_counts(normalize=True)`           | `df.valueCounts { a }`                               | No `normalize`: divide the `count` column by `df.rowsCount()`. |
| `df.nlargest(n, "a")`                         | `df.sortByDesc { a }.take(n)`                        |                                                              |
| `df.nsmallest(n, "a")`                        | `df.sortBy { a }.take(n)`                            |                                                              |
| `df.sample(n)`                                | `df.shuffle().take(n)`                               | Random in both. Only the number of rows is checked: with a seed the two libraries pick different rows. |
| `df.groupby("r").cost.transform("sum")`       | `df.join(df.groupBy { r }.aggregate { sum(cost) into "total" }, "r")` | Keeps the row order of `df` and adds a column. |

## Strings, dates, row by row

| Pandas                                        | Kotlin DataFrame                                     | Notes                                                        |
|-----------------------------------------------|------------------------------------------------------|--------------------------------------------------------------|
| `df.s.str.upper()`                            | `df.s.map { it.uppercase() }`                        | The column holds Kotlin `String`s: use their functions.      |
| `df[df.s.str.contains("or")]`                 | `df.filter { s.contains("or") }`                     |                                                              |
| `df.s.str.len()`                              | `df.s.map { it.length }`                             |                                                              |
| `df.apply(lambda r: r.a - r.b, axis=1)`       | `df.map { a - b }`                                   | One value per row.                                           |
| `pd.to_datetime(df.s)`                        | `df.convert { s }.toLocalDate()`                     | Values become `kotlinx.datetime.LocalDate`; use `.year`, `.month`. |

## Iterate and compute

| Pandas                      | Kotlin DataFrame                                     |
|-----------------------------|------------------------------------------------------|
| `for _, row in df.iterrows()` | `for (row in df) { row.revenue }`                  |
| `df.iloc[0]`                | `df.first()`                                         |
| `df.a.mean()`, `.sum()`     | `df.a.mean()`, `df.a.sum()`                          |
| `df.a.median()`             | `df.a.median()`                                      |
| `df.a.quantile(0.25)`       | `df.a.percentile(25.0)` — **numbers can differ**, see below |

## Pandas names that do not exist

Each name below was tried on a `DataFrame` and does not compile. Use the replacement.

| Do not write                            | Write instead                                         |
|-----------------------------------------|-------------------------------------------------------|
| `df.groupby("a")`                       | `df.groupBy { a }` (capital `B`)                      |
| `df.query("a > 1")`                     | `df.filter { a > 1 }`                                 |
| `df.shape`, `df.size`, `len(df)`        | `df.rowsCount()`, `df.columnsCount()`, `df.size()`    |
| `df.dtypes`, `df.info()`                | `df.schema()`                                         |
| `df.isnull()`, `df.isna()`, `df.na.count()` | `df.describe().select { name and nulls }`         |
| `df.columns` (property)                 | `df.columnNames()`                                    |
| `df.loc[...]`, `df.iloc[...]`, `df.index`, `df.reset_index()` | there is no index — see *Select, filter, sort* |
| `df.assign(...)`                        | `df.add("c") { ... }`                                 |
| `df.merge(other, on="id")`              | `df.join(other, "id")`                                |
| `df.sort_values(...)`                   | `df.sortBy { ... }`                                   |
| `df.fillna(...)`, `df.dropna()`         | `df.fillNulls { ... }.with { ... }`, `df.dropNulls()` |
| `df.to_csv(...)`                        | `df.writeCsv(file)`                                   |
| `df.nunique()`, `df.a.nunique()`        | `df.countDistinct { a }`                              |
| `df.astype(...)`                        | `df.convert { a }.to<String>()`                       |
| `df.pivot_table(...)`                   | `df.pivot { ... }.groupBy { ... }.sum { ... }`        |
| `df.a.value_counts()`                   | `df.valueCounts { a }`                                |
| `df.a.unique()`                         | `df.select { a }.distinct()`                          |
| `df.a.tolist()`                         | `df.a.toList()`                                       |
| `df.d.isnull()`                         | `df.count { d == null }`                              |
| `df.iterrows()`                         | `for (row in df) { row.revenue }`                     |
| `df.rename(columns={...})`              | `df.rename("a" to "b")`                               |
| `df.drop(columns=[...])`                | `df.remove { ... }`                                   |
| `df.T`                                  | not available                                         |
| `df.values` (property)                  | `df.values()` (a function)                            |
| `df["c"] = ...` (assignment)            | `df.add("c") { ... }`                                 |
| `df.join(other, by = "id")`             | `df.join(other, "id")` — there is no `by` parameter   |
| `DataFrame.readJSON(...)`, `writeJSON(...)` | `readJson`, `writeJson`                           |
| `DataFrame.readArrow(path)`, `writeArrow(...)` | `readArrowFeather` / `readArrowIPC`, `writeArrowFeather` / `writeArrowIPC` |

## Where the results differ

These were measured on the same six-row table in Kotlin DataFrame and in Pandas 1.3.5, 2.3.3 and 3.1.1.
The three Pandas versions agree on every row below except the text type of a column.

| What                            | Pandas                    | Kotlin DataFrame                                 |
|---------------------------------|---------------------------|--------------------------------------------------|
| 25th percentile of `revenue`    | `92.5`                    | `89.17` (`89.16666666666666`)                    |
| `describe()` columns            | numeric only              | every column, plus `nulls`                       |
| Missing value                   | `NaN`, silently spreads   | `null`; column type is `T?`                      |
| Order of groups                 | sorted by key (`sort=False` keeps first appearance) | first appearance       |
| Single-letter CSV values        | text: `object`, `str` since Pandas 3.0 | `Char`                              |
| `pivot` result                  | flat columns              | one column group, needs `ungroup`                |
| `drop(n)`                       | drops labels / columns    | drops the first `n` rows                         |
| Numbers after an Excel round trip | whole numbers stay `int64` | every number is `Double`                       |
| `melt` and `gather`             | all values of the first column, then of the second | row by row                       |
| Key columns of `merge(left_on, right_on)` and `join { a match right.b }` | both are kept | only the left one |
| PostgreSQL `integer` and a column with `NULL`s | `int64`, and `float64` with `NaN` | `Int`, and `Double?` with `null` |
| PostgreSQL `numeric` (the result of `AVG`) | `float64` | `BigDecimal` |

## Not covered yet

Do not guess a name for these. Each was searched for by name in the main sources of every module and was not found:

- `rolling`, `resample`, `ewm`, `expanding` (windowed and time-series functions);
- `to_parquet` (there is no `writeParquet`);
- `normalize=` in `value_counts` (divide by the number of rows, see above).

Reading from a database was run on **PostgreSQL** only, on a table with text, integer and floating-point columns.
Other databases, other column types (dates, arrays, JSON) and writing to a database were not run here.

If the task needs something that is not on this page, say so instead of guessing a name.
