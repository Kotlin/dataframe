# Migration to 1.0

## Deprecations and removals

As we move toward version 1.0, many functions have been changed, deprecated, or removed.
This section provides a complete overview of all API changes to help you migrate to 1.0.

### Migration to Deephaven CSV

All CSV (as well as TSV) IO was migrated to a new, fast, and efficient
[Deephaven CSV](https://github.com/deephaven/deephaven-csv) implementation.
It significantly improves CSV IO performance and brings many new parametrization options.

All related methods are now located in the separate [`dataframe-csv`](Modules.md#dataframe-csv) module
(which is included by default in the general [`dataframe`](Modules.md#dataframe-general) artifact
and in `%use dataframe` in [Kotlin Notebook](SetupKotlinNotebook.md)).

Functions were also renamed to the correct [CamelCase](https://en.wikipedia.org/wiki/Camel_case) spelling.

All new functions keep the same arguments as before and additionally introduce new ones.
Also, [there are new arguments that expose Deephaven CSV features](read.md#unlocking-deephaven-csv-features).


See [](read.md#read-from-csv).

> All outdated CSV IO functions raise `WARNING` in 1.0 and will raise `ERROR` in 1.1.

| 0.15                                            | 1.0                                             |
|-------------------------------------------------|-------------------------------------------------|
| `CSV`/`TSV`                                     | `CsvDeephaven`/`TsvDeephaven`                   |
| `DataFrame.readCSV(..)`/`DataFrame.readTSV(..)` | `DataFrame.readCsv(..)`/`DataFrame.readTsv(..)` |
| `DataFrame.read(delimeter=.., ..)`              | `DataFrame.readCsv(delimeter=.., ..)`           |
| `df.writeCSV(..)`/`df.writeTSV(..)`             | `df.writeCsv(..)`/`df.writeTsv(..)`             |
| `df.toCSV(..)`                                  | `df.toCsvStr(..)`                               |

### Migration to Standard Library `Instant`

Since Kotlin 2.1.20,
[`Instant` is now part of the standard library](https://kotlinlang.org/docs/whatsnew2120.html#new-time-tracking-functionality)  
(as `kotlin.time.Instant`).
You can still use the old (deprecated) `kotlinx.datetime.Instant` type, but its support will be removed in Kotlin DataFrame 1.1.

> New `Instant` in the Kotlin Standard Library becomes stable in 2.3.0.
> In earlier versions, all related operations should be marked with the `@OptIn(ExperimentalTime::class)` annotation.
{style="note"}

For now, each `Instant`-related operation has been split into two new ones —
one for the new stdlib `kotlin.time.Instant` and one for the old deprecated `kotlinx.datetime.Instant`.
The behavior of old operations remains unchanged: they work with `kotlinx.datetime.Instant` and raise `ERROR` in 1.0.
In version 1.1, they will be returned and will operate on the new stdlib `kotlin.time.Instant`.

<table>
<tr>
<th>0.15</th>
<th>1.0</th>
<th>Note</th>
</tr>
<tr>
<td rowspan="2"><code>col.convertToInstant()</code></td>
<td><code>col.convertToDeprecatedInstant()</code></td>
<td><code>WARNING</code> in 1.0, <code>ERROR</code> in 1.1</td>
</tr>
<tr>
<td><code>col.convertToStdlibInstant()</code></td>
<td>Will be renamed back into <code>convertToInstant() in 1.1</code></td>
</tr>
<tr>
<td rowspan="2"><code>df.convert { columns }.toInstant()</code></td>
<td><code>df.convert { columns }.convertToDeprecatedInstant()</code></td>
<td><code>WARNING</code> in 1.0, <code>ERROR</code> in 1.1</td>
</tr>
<tr>
<td><code>df.convert { columns }.convertToStdlibInstant()</code></td>
<td>Will be renamed back into <code>convertToInstant() in 1.1</code></td>
</tr>
<tr>
<td rowspan="2"><code>ColType.Instant</code></td>
<td><code>ColType.DeprecatedInstant</code></td>
<td><code>WARNING</code> in 1.0, <code>ERROR</code> in 1.1</td>
</tr>
<tr>
<td><code>ColType.StdlibInstant</code></td>
<td>Will be renamed back into <code>Instant</code> in 1.1</td>
</tr>
</table>


In version 1.0, all parsing operations still convert `Instant`
values into the deprecated `kotlinx.datetime.Instant`.
To enable parsing into the new standard library `kotlin.time.Instant`,
set the corresponding parsing option **`ParserOptions.parseExperimentalInstant`**
(that will be default in 1.1).
For example:

```kotlin
DataFrame.readCsv(
    ...,
    parserOptions = ParserOptions(parseExperimentalInstant = true)
)
```

### Deprecation of `cols()` in Columns Selection DSL

`cols()` overloads without arguments, which select all columns of a DataFrame or
all subcolumns inside a column group in the [Columns Selection DSL](ColumnSelectors.md),
are deprecated in favor of `all()` and `allCols()` respectively.
These replacements allow the [Compiler Plugin](Compiler-Plugin.md) to fully support such selections.

`colsAtAnyDepth()`, `colsInGroups()`, and `single()` overloads with a `predicate` argument
that filters columns are also deprecated for better Compiler Plugin support.
Use `.filter(predicate)` for filtering instead.

| 0.15                                             | 1.0                                                              | 
|--------------------------------------------------|------------------------------------------------------------------|
| `df.select { cols() }`                           | `df.select { all() }`                                            |
| `df.select { colGroup.cols() }`                  | `df.select { colGroup.allCols() }`                               |
| `df.select { colsAtAnyDepth { predicate } }`     | `df.select { colsAtAnyDepth().filter { predicate } }`            |
| `df.select { colsInGroups { predicate } }`       | `df.select { colsInGroups().filter { predicate } }`              |
| `df.select { single { predicate } }`             | `df.select { cols().filter { predicate }.single() }`             |
| `df.select { colGroup.singleCol { predicate } }` | `df.select { colGroup.allCols().filter { predicate }.single() }` |
| `df.select { colSet.single { predicate } }`      | `df.select { colSet.filter { predicate }.single() }`             |



### Removed functions and classes

The next functions and classes raise `ERROR` in 1.0 and will be removed in 1.1.

| 0.15                                                           | 1.0                                                                          | Reason                                 |
|----------------------------------------------------------------|------------------------------------------------------------------------------|----------------------------------------|
| `DataColumn.createFrameColumn(name, df, startIndices)`         | `df.chunked(name, startIndices)`                                             | Replaced with another function.        |
| `DataColumn.createWithTypeInference(name, values, nullable)`   | `DataColumn.createByInference(name, values, TypeSuggestion.Infer, nullable)` | Replaced with another function.        |
| `DataColumn.create(name, values, infer)`                       | `DataColumn.createByType(name, values, infer)`                               | Replaced with another function.        |
| `col.isComparable()`                                           | `col.valuesAreComparable()`                                                  | Renamed to better reflect its purpose. |
| `df.minus { columns }`                                         | `df.remove { columns }`                                                      | Replaced with another function.        |
| `df.move { columns }.toLeft()`/`df.moveToLeft{ columns }`      | `df.move { columns }.toStart()`/`df.moveToStart { columns }`                 | Renamed to better reflect its purpose. |
| `df.move { columns }.toRight()`/`df.moveToRight{ columns }`    | `df.move { columns }.toEnd()`/`df.moveToEnd{ columns }`                      | Renamed to better reflect its purpose. |
| `row.rowMin()`/`row.rowMinOrNull()`                            | `row.rowMinOf()`/`row.rowMinOfOrNull()`                                      | Renamed to better reflect its purpose. |
| `row.rowMax()`/`row.rowMaxOrNull()`                            | `row.rowMaxOf()`/`AnyRow.rowMaxOfOrNull()`                                   | Renamed to better reflect its purpose. |
| `row.rowPercentile()`/`row.rowPercentileOrNull()`              | `row.rowPercentileOf()`/`row.rowPercentileOfOrNull()`                        | Renamed to better reflect its purpose. |
| `row.rowMedian()`/`row.rowMedianOrNull()`                      | `row.rowMedianOf()`/`row.rowMedianOfOrNull()`                                | Renamed to better reflect its purpose. |
| `df.convert { columns }.to { converter }`                      | `df.convert { columns }.asColumn { converter }`                              | Renamed to better reflect its purpose. |
| `df.toHTML(..)`/`df.toStandaloneHTML()`                        | `df.toHtml(..)`/`df.toStandaloneHtml()`                                      | Renamed to the correct CamelCase.      |
| `df.writeHTML()`                                               | `df.writeHtml()`                                                             | Renamed to the correct CamelCase.      |
| `asURL(fileOrUrl)`/`isURL(path)`                               | `asUrl(fileOrUrl)`/`isUrl(path)`                                             | Renamed to the correct CamelCase.      |
| `df.convert { columns }.toURL()`/`df.convertToURL { columns }` | `df.convert { columns }.toUrl()`/`df.convertToUrl { columns }`               | Renamed to the correct CamelCase.      |
| `df.filterBy(column)`                                          | `df.filter { column }`                                                       | Replaced with another function.        |
| `FormattingDSL`                                                | `FormattingDsl`                                                              | Renamed to the correct CamelCase.      |
| `RGBColor`                                                     | `RgbColor`                                                                   | Renamed to the correct CamelCase.      |
| `df.insert(column).after(columnPath)`                          | `df.insert(column).after { columnPath }`                                     | Replaced with another function.        |
| `CompareResult.Equals` / `CompareResult.isEqual()`             | `CompareResult.Matches` / `CompareResult.matches()`                          | Renamed to better reflect its purpose. |
| `CompareResult.isSuperOrEqual()`                               | `CompareResult.isSuperOrMatches()`                                           | Renamed to better reflect its purpose. |

The next functions and classes raise `WARNING` in 1.0 and `ERROR` in 1.1.

| 0.15                                                                                                     | 1.0                                                                                                                           | Reason                                                                  |
|----------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| `df.split { columns }.default(..)` / `df.split { columns }.into(..)` / `df.split { columns }.inward(..)` | `df.split { columns }.by(..).default(..)` / `df.split { columns }.by(..).into(..)` / `df.split { columns }.by(..).inward(..)` | Removed a shortcut to clarify the behaviour; Only for `String` columns. |
| `dataFrameOf(header, values)`                                                                            | `dataFrameOf(header).withValues(values)`                                                                                      | Replaced with another function.                                         |
| `df.generateCode(..)`                                                                                    | `df.generateInterfaces(..)`                                                                                                   | Replaced with another function.                                         |
| `df.select { mapToColumn(name, infer) { body } }`                                                        | `df.select { expr(name, infer) { body } }`                                                                                    | Removed duplicated functionality.                                       |
| `stringCol.length()`                                                                                     | `stringCol.map { it?.length ?: 0 }`                                                                                           | Removed a shortcut to clarify the behaviour; Only for `String` columns. |
| `stringCol.lowercase()` / `stringCol.uppercase()`                                                        | `stringCol.map { it?.lowercase() }` / `stringCol.map { it?.uppercase() }`                                                     | Removed a shortcut to clarify the behaviour; Only for `String` columns. |
| `df.add(columns)` / `df.add(dataframes)`                                                                 | `df.addAll(columns)` / `df.addAll(dataframes)`                                                                                | Renamed to to improve completion.                                       |
| `row.isEmpty()` / `row.isNotEmpty()`                                                                     | `row.values().all { it == null }` / `row.values().all { it == null }`                                                         | Removed a shortcut to clarify the behaviour;                            |
| `row.getRow(index)` /  `row.getRowOrNull(index)` / `row.getRows(indices)`                                | `row.df().getRow(index)` /  `row.df().getRowOrNull(index)` / `row.df().getRows(indices)`                                      | Removed a shortcut to clarify the behaviour;                            |
| `df.copy()`                                                                                              | `df.columns().toDataFrame().cast()`                                                                                           | Removed a shortcut to clarify the behaviour;                            |
| `KeyValueProperty<T>`                                                                                    | `NameValueProperty<T>`                                                                                                        | Removed duplicated functionality.                                       |

<!--TODO (https://github.com/Kotlin/dataframe/issues/1630)

## Modules

## Compiler Plugin

## Changes in working with JDBC

-->
