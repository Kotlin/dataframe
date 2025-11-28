# Migration to 1.0

## Модули

## Компайлер плагин

убрали градл/ксп плагины

## Deprecations and removals

На пути к 1.0, многие функции были изменены, deprecated или удалены.
В данном разделе вы узнаете обо всех измнениях в API для миграции на 1.0

### Migration to Deephaven CSV

All CSV (as well as TSV) IO was migrated 
to a new, fast and efficient
[Deephaven CSV](https://github.com/deephaven/deephaven-csv) implementation. 
It signigicantly improves CSV IO performance, and brings a lot of
new parametrization options.

All related methods
are now in the separate [`dataframe-csv`](Modules.md#dataframe-csv) module (
which is included by default in the general [`dataframe`](Modules.md#dataframe-general) artifact
and in `%use dataframe` in [Kotlin Notebook](SetupKotlinNotebook.md).
).

Functions were also renamed to the correct CamelCase spelling.

> All outdated CSV IO functions raise `WARNING` in 1.0 and will raise `ERROR` in 1.1.

| 0.15                               | 1.0                                   |
|------------------------------------|---------------------------------------|
| `DataFrame.readCSV(..)`            | `DataFrame.readCsv(..)`               |
| `DataFrame.readTSV(..)`            | `DataFrame.readTsv(..)`               |
| `DataFrame.read(delimeter=.., ..)` | `DataFrame.readCsv(delimeter=.., ..)` |
| `DataFrame.writeCSV(..)`           | `DataFrame.writeCsv(..)`              |

### Migration to Standard Library `Instant`

Since Kotlin 2.1.20, 
[`Instant` is now part of the standard library](https://kotlinlang.org/docs/whatsnew2120.html#new-time-tracking-functionality)
 (as `kotlin.time.Instant`).
You can still use the old (deprecated) `kotlinx.datetime.Instant` type, but its support will be removed in 1.1.


### Removed functions and classes

The next functions and classes raise `ERROR` in 1.0 and will be removed in 1.1.

| 0.15                                                         | 1.0                                                                          | Reason                                |
|--------------------------------------------------------------|------------------------------------------------------------------------------|---------------------------------------|
| `DataColumn.createFrameColumn(name, df, startIndices)`       | `df.chunked(name, startIndices)`                                             | Replaced with an other function       |
| `DataColumn.createWithTypeInference(name, values, nullable)` | `DataColumn.createByInference(name, values, TypeSuggestion.Infer, nullable)` | Replaced with an other function       |
| `DataColumn.create(name, values, infer)`                     | `DataColumn.createByType(name, values, infer)`                               | Replaced with an other function       |
| `col.isComparable()`                                         | `col.valuesAreComparable()`                                                  | Renamed to better reflect its purpose |
| `df.minus(columns)`                                          | `df.remove(columns)`                                                         | Replaced with an other function       |
| `df.move(columns).toLeft()`/`df.moveToLeft(columns)`         | `df.move(columns).toStart()`/`df.moveToStart(columns)`                       | Renamed to better reflect its purpose |
| `df.move(columns).toRight()`/`df.moveToRight(columns)`       | `df.move(columns).toEnd()`/`df.moveToEnd(columns)`                           | Renamed to better reflect its purpose |
| `row.rowMin()`/`row.rowMinOrNull()`                          | `row.rowMinOf()`/`row.rowMinOfOrNull()`                                      | Renamed to better reflect its purpose |
| `row.rowMax()`/`row.rowMaxOrNull()`                          | `row.rowMaxOf()`/`AnyRow.rowMaxOfOrNull()`                                   | Renamed to better reflect its purpose |
| `row.rowPercentile()`/`row.rowPercentileOrNull()`            | `row.rowPercentileOf()`/`row.rowPercentileOfOrNull()`                        | Renamed to better reflect its purpose |
| `row.rowMedian()`/`row.rowMedianOrNull()`                    | `row.rowMedianOf()`/`row.rowMedianOfOrNull()`                                | Renamed to better reflect its purpose |
| `df.convert(columns).to(converter)`                          | `df.convert(columns).asColumn(converter)`                                    | Renamed to better reflect its purpose |
| `df.toHTML(..)`/`df.toStandaloneHTML()`                      | `df.toHtml(..)`/`df.toStandaloneHtml()`                                      | Renamed to the correct CamelCase      |
| `df.writeHTML()`                                             | `df.writeHtml()`                                                             | Renamed to the correct CamelCase      |
| `asURL(fileOrUrl)`/`isURL(path)`                             | `asUrl(fileOrUrl)`/`isUrl(path)`                                             | Renamed to the correct CamelCase      |
| `df.convert(columns).toURL()`/`df.convertToURL(columns)`     | `df.convert(columns).toUrl()`/`df.convertToUrl(columns)`                     | Renamed to the correct CamelCase      |
| `df.filterBy(column)`                                        | `df.filter { column }`                                                       | Replaced with an other function       |
| `FormattingDSL`                                              | `FormattingDsl`                                                              | Renamed to the correct CamelCase      |
| `RGBColor`                                                   | `RgbColor`                                                                   | Renamed to the correct CamelCase      |
| ``                                                           | ``                                                                           | Renamed to better reflect its purpose |
| ``                                                           | ``                                                                           | Renamed to better reflect its purpose |
| ``                                                           | ``                                                                           | Renamed to better reflect its purpose |
| ``                                                           | ``                                                                           | Renamed to better reflect its purpose |

#### Deprecation of `.cols()` in Columns Selection DSL

`.cols()` overloads without arguments, that selects all columns of the dataframe / 
all subcolumns inside the column group in [Columns Selection DSL](ColumnSelectors.md),
are deprecated in favor of `.all()` and `.allCols()` correspondingly, allowing to
support this selection in the Compiler Plugin.

| 0.15                                                         | 1.0                                                                          | 
|--------------------------------------------------------------|------------------------------------------------------------------------------|
| `df.select { cols() }`                                       | `df.select { all() }`                                                        |
| `df.select { group.cols() }`                                 | `df.select { group.allCols() }`                                              |

// region WARNING in 0.15, ERROR in 1.0



internal const val CONVERT_TO_INSTANT =
"kotlinx.datetime.Instant is deprecated in favor of kotlin.time.Instant. Either migrate to kotlin.time.Instant and use convertToStdlibInstant() or use convertToDeprecatedInstant(). $MESSAGE_1_0 and migrated to kotlin.time.Instant in 1.1."
internal const val CONVERT_TO_INSTANT_REPLACE = "this.convertToDeprecatedInstant()"

internal const val TO_INSTANT =
"kotlinx.datetime.Instant is deprecated in favor of kotlin.time.Instant. Either migrate to kotlin.time.Instant and use toStdlibInstant() or use toDeprecatedInstant(). $MESSAGE_1_0 and migrated to kotlin.time.Instant in 1.1."
internal const val TO_INSTANT_REPLACE = "this.toDeprecatedInstant()"

internal const val COL_TYPE_INSTANT =
"kotlinx.datetime.Instant is deprecated in favor of kotlin.time.Instant. Either migrate to kotlin.time.Instant and use ColType.StdlibInstant or use ColType.DeprecatedInstant. $MESSAGE_1_0 and migrated to kotlin.time.Instant in 1.1."
internal const val COL_TYPE_INSTANT_REPLACE = "ColType.DeprecatedInstant"

internal const val INSERT_AFTER_COL_PATH =
"This `after()` overload will be removed in favor of `after { }` with Column Selection DSL. $MESSAGE_1_0"
internal const val INSERT_AFTER_COL_PATH_REPLACE = "this.after { columnPath }"


internal const val COMPARE_RESULT_EQUALS =
"'Equals' is deprecated in favor of 'Matches' to clarify column order is irrelevant. $MESSAGE_1_0"

// endregion

// region WARNING in 1.0, ERROR in 1.1

private const val MESSAGE_1_1 = "Will be ERROR in 1.1."

internal const val APACHE_CSV =
"The Apache-based CSV/TSV reader is deprecated in favor of the new Deephaven CSV reader in dataframe-csv. $MESSAGE_1_1"
internal const val READ_CSV =
"Apache-based readCSV() is deprecated in favor of Deephaven-based readCsv() in dataframe-csv. $MESSAGE_1_1"
internal const val READ_CSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.readCsv"
internal const val READ_CSV_FILE_OR_URL_REPLACE =
"this.readCsv(fileOrUrl = fileOrUrl, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_CSV_FILE_REPLACE =
"this.readCsv(file = file, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_CSV_URL_REPLACE =
"this.readCsv(url = url, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_CSV_STREAM_REPLACE =
"this.readCsv(inputStream = stream, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"

internal const val READ_DELIM =
"Apache-based readDelim() is deprecated in favor of Deephaven-based readDelim() in dataframe-csv. $MESSAGE_1_1"
internal const val READ_DELIM_STREAM_REPLACE =
"this.readDelim(inputStream = inStream, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_DELIM_READER_REPLACE =
"this.readDelimStr(text = reader.readText(), delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"

internal const val READ_TSV =
"Apache-based readTSV() is deprecated in favor of Deephaven-based readTsv() in dataframe-csv. $MESSAGE_1_1"
internal const val READ_TSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.readTsv"
internal const val READ_TSV_FILE_OR_URL_REPLACE =
"this.readTsv(fileOrUrl = fileOrUrl, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_TSV_FILE_REPLACE =
"this.readTsv(file = file, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_TSV_URL_REPLACE =
"this.readTsv(url = url, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"
internal const val READ_TSV_STREAM_REPLACE =
"this.readTsv(inputStream = stream, delimiter = delimiter, header = header, colTypes = colTypes, skipLines = skipLines, readLines = readLines, allowMissingColumns = duplicate, parserOptions = parserOptions)"

internal const val WRITE_CSV =
"The writeCSV() functions are deprecated in favor of writeCsv() in dataframe-csv. $MESSAGE_1_1"
internal const val WRITE_CSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.writeCsv"
internal const val WRITE_CSV_FILE_REPLACE = "this.writeCsv(file = file)"
internal const val WRITE_CSV_PATH_REPLACE = "this.writeCsv(path = path)"
internal const val WRITE_CSV_WRITER_REPLACE = "this.writeCsv(writer = writer)"

internal const val TO_CSV = "toCsv() is deprecated in favor of toCsvStr() in dataframe-csv. $MESSAGE_1_1"
internal const val TO_CSV_IMPORT = "org.jetbrains.kotlinx.dataframe.io.toCsvStr"
internal const val TO_CSV_REPLACE = "this.toCsvStr()"

internal const val SPLIT_STR =
"Please explicitly specify how the String should be split. Shortcut: $MESSAGE_1_1"

internal const val DATAFRAME_OF_WITH_VALUES =
"Deprecated in favor of dataFrameOf(names).withValues(values). $MESSAGE_1_1"

internal const val COLS_AT_ANY_DEPTH = "Deprecated shortcut for better compiler plugin support. $MESSAGE_1_1"
internal const val COLS_AT_ANY_DEPTH_REPLACE = "this.colsAtAnyDepth().filter(predicate)"

internal const val COLS_IN_GROUPS = "Deprecated shortcut for better compiler plugin support. $MESSAGE_1_1"
internal const val COLS_IN_GROUPS_REPLACE = "this.colsInGroups().filter(predicate)"

internal const val SINGLE = "Deprecated shortcut for better compiler plugin support. $MESSAGE_1_1"
internal const val SINGLE_COL_REPLACE = "this.allCols().filter(condition).single()"
internal const val SINGLE_PLAIN_REPLACE = "this.cols().filter(condition).single()"
internal const val SINGLE_SET_REPLACE = "this.filter(condition).single()"

internal const val GENERATE_CODE =
"This function has been deprecated in favor of the more explicit `generateInterfaces()`. The `fields` parameter has also been removed. Use `CodeGenerator` explicitly, if you need it. $MESSAGE_1_1"

internal const val GENERATE_CODE_REPLACE1 = "this.generateInterfaces(extensionProperties = extensionProperties)"
internal const val GENERATE_CODE_REPLACE2 =
"this.generateInterfaces(markerName = markerName, extensionProperties = extensionProperties, visibility = visibility)"

internal const val GENERATE_INTERFACES = "This function is just here for binary compatibility. $MESSAGE_1_1"

internal const val UNIFIED_SIMILAR_CS_API = "Deprecated duplicated functionality. $MESSAGE_1_1"

internal const val CONVERT_TO_DEPRECATED_INSTANT =
"kotlinx.datetime.Instant is deprecated in favor of kotlin.time.Instant. Migrate to kotlin.time.Instant and use convertToStdlibInstant() at your own pace. $MESSAGE_1_1"
internal const val CONVERT_TO_DEPRECATED_INSTANT_REPLACE = "this.convertToStdlibInstant()"

internal const val TO_DEPRECATED_INSTANT =
"kotlinx.datetime.Instant is deprecated in favor of kotlin.time.Instant. Migrate to kotlin.time.Instant and use toStdlibInstant() at your own pace. $MESSAGE_1_1"
internal const val TO_DEPRECATED_INSTANT_REPLACE = "this.toStdlibInstant()"

internal const val COL_TYPE_DEPRECATED_INSTANT =
"kotlinx.datetime.Instant is deprecated in favor of kotlin.time.Instant. Migrate to kotlin.time.Instant and use Coltype.StdlibInstant at your own pace. $MESSAGE_1_1"
internal const val COL_TYPE_DEPRECATED_INSTANT_REPLACE = "ColType.StdlibInstant"

internal const val MESSAGE_SHORTCUT = "This shortcut is deprecated. $MESSAGE_1_1"

internal const val LENGTH_REPLACE = "this.map { it?.length ?: 0 }"
internal const val LOWERCASE_REPLACE = "this.map { it?.lowercase() }"
internal const val UPPERCASE_REPLACE = "this.map { it?.uppercase() }"

internal const val ADD_VARARG_COLUMNS = "Deprecated in favor of `addAll(vararg)` to improve completion. $MESSAGE_1_1"
internal const val ADD_VARARG_COLUMNS_REPLACE = "this.addAll(*columns)"

internal const val ADD_VARARG_FRAMES = "Deprecated in favor of `addAll(vararg)` to improve completion. $MESSAGE_1_1"
internal const val ADD_VARARG_FRAMES_REPLACE = "this.addAll(*dataFrames)"

internal const val IS_EMPTY_REPLACE = "values().all { it == null }"
internal const val IS_NOT_EMPTY_REPLACE = "values().any { it != null }"
internal const val GET_ROW_REPLACE = "df().getRow(index)"
internal const val GET_ROWS_ITERABLE_REPLACE = "df().getRows(indices)"
internal const val GET_ROWS_RANGE_REPLACE = "df().getRows(indices)"
internal const val GET_ROW_OR_NULL_REPLACE = "df().getRowOrNull(index)"
internal const val COPY_REPLACE = "columns().toDataFrame().cast()"

internal const val LISTS_TO_DATAFRAME_MIGRATION =
"Function moved from io to api package, and a new `header` parameter is introduced. $MESSAGE_1_1"

internal const val KEY_VALUE_PROPERTY = "Deprecated in favor of NameValueProperty. $MESSAGE_1_1"
internal const val KEY_VALUE_PROPERTY_KEY =
"This column will be renamed to 'name' when KeyValueProperty will be replaced with NameValueProperty. $MESSAGE_1_1"

// endregion

## Changes in working with JDBC 
