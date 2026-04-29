# Migration to 1.0

## Deprecations and removals

As we move toward version 1.0, many functions have been changed, deprecated, or removed.
This section provides a complete overview of all API changes to help you migrate to 1.0.

### Renamed functions and classes to the correct CamelCase spelling { id="camelCase" }

All functions and classes in Kotlin DataFrame
have been renamed to
[the correct CamelCase spelling](https://developer.android.com/kotlin/style-guide#camel_case).

See below for a complete list of the renamed functions and classes.

### Migration to Deephaven CSV

All CSV (as well as TSV) IO was migrated to a new, fast, and efficient
[Deephaven CSV](https://github.com/deephaven/deephaven-csv) implementation.
It significantly improves CSV IO performance and brings many new parametrization options.

All related methods are now located in the separate [`dataframe-csv`](Modules.md#dataframe-csv) module
(which is included by default in the general [`dataframe`](Modules.md#dataframe-general) artifact
and in `%use dataframe` in [Kotlin Notebook](SetupKotlinNotebook.md)).

Functions were also renamed to [the correct CamelCase spelling](#camelCase).

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
[
`Instant` is now part of the standard library](https://kotlinlang.org/docs/whatsnew2120.html#new-time-tracking-functionality)  
(as `kotlin.time.Instant`).
You can still use the old (deprecated) `kotlinx.datetime.Instant` type, but its support will be removed in Kotlin
DataFrame 1.1.

> New `Instant` in the Kotlin Standard Library becomes stable in 2.3.0.
> In earlier versions, all related operations should be marked with the `@OptIn(ExperimentalTime::class)` annotation.
> {style="note"}

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


In version 1.0-Beta5 and later, all parsing operations convert `Instant`
values into the new standard library `kotlin.time.Instant` type by default.
To enable parsing into the deprecated `kotlinx.datetime.Instant`,
set the corresponding parsing option **`ParserOptions.parseExperimentalInstant = false`**
(before 1.0-Beta5, this option was `false`, from 1.0-Beta5 onwards it is `true` by default).

For example:

```kotlin
DataFrame.readCsv(
    ...,
parserOptions = ParserOptions(parseExperimentalInstant = false)
)
```

### Deprecation of `cols()` and other methods in Columns Selection DSL

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
| `df.insert(column).after(columnPath)`                          | `df.insert(column).after { columnPath }`                                     | Replaced with another overload.        |
| `df.insert(column).under(columnPath)`                          | `df.insert(column).under { columnPath }`                                     | Replaced with another overload.        |
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

## Parsing and Converting Date-Time

In 0.15 and up to 1.0-Beta4 we did support the [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)
types; however, they were still treated second-level in DataFrame. [`ParserOptions`](parse.md#parser-options)
were still built around the Java `DataTimeFormatter`-paradigm.
Starting from 1.0-Beta5, [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) types now become first-class
citizens,
while still allowing you to use Java types if you need them.

<table>
<tr>
<th>0.15</th>
<th>1.0</th>
<th>Reason</th>
</tr>
<tr>
<td>

```kotlin
df.parse()
```
</td>
<td>

```kotlin
df.parse()
```
</td>
<td>Default parsing behavior remains largely unchanged.</td>
</tr>
<tr>
<td>

```kotlin
df.parse(
    ParserOptions(
        skipTypes = setOf(
            typeOf<kotlinx.datetime.LocalDate>(), 
            ...,
        ),
    ),
)
```
</td>
<td>

```kotlin
df.parse(
    ParserOptions(dateTime = DateTimeParserOptions.Java),
)
```
</td>
<td>If you want to force parsing to Java date-time types, you no longer have use skipTypes, you can simply change the `dateTime` argument.</td>
</tr>
<tr>
<td>

```kotlin
DataFrame.parser.addSkipType(
    typeOf<kotlinx.datetime.LocalDate>(),
)
```
</td>
<td>

```kotlin
DataFrame.parser.dateTimeLibrary = ParseDateTimeLibrary.JAVA
```
</td>
<td>The same logic applies for the <a href="parse.md#global-parser-options">global parser options</a>.</td>
</tr>
<tr>
<td rowspan="2">

```kotlin
ParserOptions(dateTimeFormatter = myFormatter)
```
</td>
<td>

```kotlin
ParserOptions(
    dateTime = DateTimeParserOptions.Kotlin
        .withFormat<_>(myKotlinFormat),
)
```
</td>
<td rowspan="2">You now need to explicitly specify you expect Java or Kotlin date-time types via `DateTimeParserOptions.X`. Then you can specify the relevant options, like a `kotlinx.datetime.format.DateTimeFormat` or `java.time.DateTimeFormatter`. These are typed now too, optionally for Java.</td>
</tr>
<tr>
<td>

```kotlin
ParserOptions(
    dateTime = DateTimeParserOptions.Java
        .withFormatter<java.time.LocalDateTime>(myJavaFormatter),
)
```
</td>
</tr>
<tr>
<td>

```kotlin
ParserOptions(
    locale = myLocale,
    dateTimeFormatter = myFormatter,
)
```
</td>
<td>

```kotlin
ParserOptions(
    dateTime = DateTimeParserOptions.Java
        .withLocale(myLocale)
        .withFormatter<java.time.LocalDateTime>(myFormatter),
)
```
</td>
<td>Locale for date-time only works for Java types. If you need Kotlin types ánd a locale use <a href="convert.md">convert</a> to convert to Kotlin types afterwards.</td>
</tr>
<tr>
<td rowspan="2">

```kotlin
ParserOptions(dateTimePattern = "MM/dd yyyy")
```
</td>
<td>

```kotlin
@OptIn(FormatStringsInDatetimeFormats::class)
ParserOptions(
    dateTime = DateTimeParserOptions.Kotlin
        .withPattern<kotlinx.datetime.LocalDate>("MM/dd yyyy"),
)
```
</td>
<td rowspan="2">Again, you now need to specify the target date-time library and the expected type for your pattern (optionally for Java). In Kotlin you also need to opt-in, because using `DateTimeFormat` instead is <a href="https://github.com/Kotlin/kotlinx-datetime#using-unicode-format-strings-like-yyyy-mm-dd">recommended</a>.</td>
</tr>
<tr>
<td>

```kotlin
ParserOptions(
    dateTime = DateTimeParserOptions.Java
        .withPattern<java.time.LocalDate>("MM/dd yyyy"),
)
```
</td>
</tr>
<tr>
<td rowspan="2">

```kotlin
DataFrame.parser.addDateTimePattern("MM/dd yyyy")
```
</td>
<td>

```kotlin
@OptIn(FormatStringsInDatetimeFormats::class)
DataFrame.parser
    .addDateTimeUnicodePattern<kotlinx.datetime.LocalDate>("MM/dd yyyy")
```
</td>
<td rowspan="2">Same idea. Though you can now also use `...addDateTimeFormat()` / `...addJavaDateTimeFormatter()`, which we would recommend more.</td>
</tr>
<tr>
<td>

```kotlin
DataFrame.parser
    .addJavaDateTimePattern<java.time.LocalDate>("MM/dd yyyy")
```
</td>
</tr>
<tr>
<td rowspan="2">

```kotlin
convert { stringCols }.toLocalDate(pattern = "MM/dd yyyy")
```
</td>
<td>

```kotlin
@OptIn(FormatStringsInDatetimeFormats::class)
convert { stringCols }.toLocalDate(pattern = "MM/dd yyyy")
```
</td>
<td rowspan="2">Same logic applies to convert: you need to opt-in to use patterns for Kotlin types and specify "Java" for Java types.</td>
</tr>
<tr>
<td>

```kotlin
convert { stringCols }.toJavaLocalDate(pattern = "MM/dd yyyy")
```
</td>
</tr>
<tr>
<td>

```kotlin
stringCol.convertToLocalDate(locale = Locale.GERMAN)
```
</td>
<td>

```kotlin
stringCol
    .convertToJavaLocalDate(locale = Locale.GERMAN)
    .convertToLocalDate()
```
</td>
<td>If you need to supply a locale to be able to parse date-time types, parse to Java types first, then convert to Kotlin ones.</td>
</tr>
</table>

For more information, check out [](parse.md#parsing-date-time-strings).

<!--TODO (https://github.com/Kotlin/dataframe/issues/1630)

## Modules

## Compiler Plugin

## Changes in working with JDBC

-->
