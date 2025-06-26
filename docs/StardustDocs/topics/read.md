[//]: # (title: Read)

<web-summary>
Learn how to load structured data into Kotlin DataFrame 
from CSV, JSON, Excel, SQL databases, and more.
</web-summary>

<card-summary>
Read your data from various file formats into DataFrame.
</card-summary>

<link-summary>
Explore how to read data into Kotlin DataFrame from files, URLs, 
with format auto-detection and parsing options.
</link-summary>


<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Read-->

The Kotlin DataFrame library supports CSV, TSV, JSON, XLS and XLSX, and Apache Arrow input formats.

The reading from SQL databases is also supported.
Read [here](readSqlDatabases.md) to know more 
or explore the [example project](https://github.com/zaleslaw/KotlinDataFrame-SQL-Examples).

The `.read()` function automatically detects the input format based on a file extension and content:

```kotlin
DataFrame.read("input.csv")
```

The input string can be a file path or URL.

## Read from CSV

Before you can read data from CSV, make sure you have the following dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-csv:$dataframe_version")
```

It's included by default if you have `org.jetbrains.kotlinx:dataframe:$dataframe_version` already.

To read a CSV file, use the `.readCsv()` function.

Since DataFrame v0.15, this new CSV integration is available.
It is faster and more flexible than the old one, now being based on
[Deephaven CSV](https://github.com/deephaven/deephaven-csv).

{style="note"}

To read a CSV file from a file:

```kotlin
import java.io.File

DataFrame.readCsv("input.csv")
// Alternatively
DataFrame.readCsv(File("input.csv"))
```

To read a CSV file from a URL:

```kotlin
import java.net.URI

DataFrame.readCsv(URI("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv").toURL())
```

Zip and GZip files are supported as well.

To read CSV from `String`:

```kotlin
val csv = """
    A,B,C,D
    12,tuv,0.12,true
    41,xyz,3.6,not assigned
    89,abc,7.1,false
""".trimIndent()

DataFrame.readCsvStr(csv)
```

### Specify delimiter

By default, CSV files are parsed using `,` as the delimiter. To specify a custom delimiter, use the `delimiter` argument:

<!---FUN readCsvCustom-->

```kotlin
val df = DataFrame.readCsv(
    file,
    delimiter = '|',
    header = listOf("A", "B", "C", "D"),
    parserOptions = ParserOptions(nullStrings = setOf("not assigned")),
)
```

<!---END-->

Aside from the delimiter, there are many other parameters to change.
These include the header, the number of rows to skip, the number of rows to read, the quote character, and more.
Check out the KDocs for more information.

### Column type inference from CSV

Column types are inferred from the CSV data.

We rely on the fast implementation of [Deephaven CSV](https://github.com/deephaven/deephaven-csv) for inferring and
parsing to (nullable) `Int`, `Long`, `Double`, and `Boolean` types.
For other types we fall back to [the parse operation](parse.md).

Suppose that the CSV from the previous
example had the following content:

<table>
<tr><th>A</th><th>B</th><th>C</th><th>D</th></tr>
<tr><td>12</td><td>tuv</td><td>0.12</td><td>true</td></tr>
<tr><td>41</td><td>xyz</td><td>3.6</td><td>not assigned</td></tr>
<tr><td>89</td><td>abc</td><td>7.1</td><td>false</td></tr>
</table>

Then the [`DataFrame`](DataFrame.md) schema we get is:

```text
A: Int
B: String
C: Double
D: Boolean?
```

[`DataFrame`](DataFrame.md) can [parse](parse.md) columns as JSON too, so when reading the following table with JSON object in column D:

<table>
<tr><th>A</th><th>D</th></tr>
<tr><td>12</td><td>{"B":2,"C":3}</td></tr>
<tr><td>41</td><td>{"B":3,"C":2}</td></tr>
</table>

We get this data schema where D is [`ColumnGroup`](DataColumn.md#columngroup) with two nested columns:

```text
A: Int
D:
    B: Int
    C: Int
```

For a column where values are lists of JSON values:
<table>
<tr><th>A</th><th>G</th></tr>
<tr><td>12</td><td>[{"B":1,"C":2,"D":3},{"B":1,"C":3,"D":2}]</td></tr>
<tr><td>41</td><td>[{"B":2,"C":1,"D":3}]</td></tr>
</table>

```text
A: Int
G: *
    B: Int
    C: Int
    D: Int
```

### Work with locale-specific numbers

Sometimes columns in your CSV can be interpreted differently depending on your system locale.

<table>
<tr><th>numbers</th></tr>
<tr><td>12,123</td></tr>
<tr><td>41,111</td></tr>
</table>

Here a comma can be a decimal-, or thousands separator, and thus become different values.
You can deal with it in multiple ways, for instance:

1) Provide locale as parser option

<!---FUN readNumbersWithSpecificLocale-->

```kotlin
val df = DataFrame.readCsv(
    file,
    parserOptions = ParserOptions(locale = Locale.UK),
)
```

<!---END-->

2) Disable type inference for a specific column and convert it yourself

<!---FUN readNumbersWithColType-->

```kotlin
val df = DataFrame.readCsv(
    file,
    colTypes = mapOf("colName" to ColType.String),
)
```

<!---END-->

### Work with specific date-time formats

When parsing date or date-time columns, you might encounter formats different from the default `ISO_LOCAL_DATE_TIME`.
Like:

<table>
<tr><th>date</th></tr>
<tr><td>13/Jan/23 11:49 AM</td></tr>
<tr><td>14/Mar/23 5:35 PM</td></tr>
</table>

Because the format here `"dd/MMM/yy h:mm a"` differs from the default (`ISO_LOCAL_DATE_TIME`),
columns like this may be recognized as simple `String` values rather than actual date-time columns.

You can fix this whenever you [parse](parse.md) a string-based column (e.g., using [`DataFrame.readCsv()`](read.md#read-from-csv),
[`DataFrame.readTsv()`](read.md#read-from-csv), or [`DataColumn<String>.convertTo<>()`](convert.md)) by providing
a custom date-time pattern. 

There are two ways to do this:

1) By providing the date-time pattern as raw string to the `ParserOptions` argument:

<!---FUN readDatesWithSpecificDateTimePattern-->

```kotlin
val df = DataFrame.readCsv(
    file,
    parserOptions = ParserOptions(dateTimePattern = "dd/MMM/yy h:mm a")
)
```

<!---END-->

2) By providing a `DateTimeFormatter` to the `ParserOptions` argument:

<!---FUN readDatesWithSpecificDateTimeFormatter-->

```kotlin
val df = DataFrame.readCsv(
    file,
    parserOptions = ParserOptions(dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yy h:mm a"))
)
```

<!---END-->
These two approaches are essentially the same, just specified in different ways.
The result will be a dataframe with properly parsed `DateTime` columns.

> Note: Although these examples focus on reading CSV files, 
> these `ParserOptions` can be supplied to any `String`-column-handling operation 
> (like, `readCsv`, `readTsv`, `stringCol.convertTo<>()`, etc.) 
> This allows you to configure the locale, null-strings, date-time patterns, and more.
> 
> For more details on the parse operation, see the [`parse operation`](parse.md).

### Provide a default type for all columns

While you can provide a `ColType` per column, you might not
always know how many columns there are or what their names are.
In such cases, you can disable type inference for all columns
by providing a default type for all columns:

<!---FUN readDatesWithDefaultType-->

```kotlin
val df = DataFrame.readCsv(
    file,
    colTypes = mapOf(ColType.DEFAULT to ColType.String),
)
```

<!---END-->

This default can be combined with specific types for other columns as well.

### Unlocking Deephaven CSV features

For each group of functions (`readCsv`, `readDelim`, `readTsv`, etc.)
we provide one overload which has the `adjustCsvSpecs` parameter.
This is an advanced option because it exposes the
[CsvSpecs.Builder](https://github.com/deephaven/deephaven-csv/blob/main/src/main/java/io/deephaven/csv/CsvSpecs.java)
of the underlying Deephaven implementation.
Generally, we don't recommend using this feature unless there's no other way to achieve your goal.

For example, to enable the (unconfigurable but) very fast [ISO DateTime Parser of Deephaven CSV](https://medium.com/@deephavendatalabs/a-high-performance-csv-reader-with-type-inference-4bf2e4baf2d1):

<!---FUN readDatesWithDeephavenDateTimeParser-->

```kotlin
val df = DataFrame.readCsv(
    inputStream = file.openStream(),
    adjustCsvSpecs = { // it: CsvSpecs.Builder
        it.putParserForName("date", Parsers.DATETIME)
    },
)
```

<!---END-->

## Read from JSON

Before you can read data from JSON, make sure you have the following dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-json:$dataframe_version")
```

It's included by default if you have `org.jetbrains.kotlinx:dataframe:$dataframe_version` already.

To read a JSON file, use the `.readJson()` function. JSON files can be read from a file or a URL.

Note that after reading a JSON with a complex structure, you can get hierarchical
[`DataFrame`](DataFrame.md): [`DataFrame`](DataFrame.md) with `ColumnGroup`s and [`FrameColumn`](DataColumn.md#framecolumn)s.

To read a JSON file from a file:

<!---FUN readJson-->

```kotlin
val df = DataFrame.readJson(file)
```

<!---END-->

To read a JSON file from a URL:

```kotlin
DataFrame.readJson("https://covid.ourworldindata.org/data/owid-covid-data.json")
```

### Column type inference from JSON

Type inference for JSON is much simpler than for CSV.
JSON string literals always become a `String`.
Number literals are converted to a unified `Number` type which will fit all encountered numbers.
Boolean literals are converted to `Boolean`.

Let's take a look at the following JSON:

```json
[
    {
        "A": "1",
        "B": 1,
        "C": 1.0,
        "D": true
    },
    {
        "A": "2",
        "B": 2,
        "C": 1.1,
        "D": null
    },
    {
        "A": "3",
        "B": 3,
        "C": 1,
        "D": false
    },
    {
        "A": "4",
        "B": 4,
        "C": 1.3,
        "D": true
    }
]
```

We can read it from file:

```kotlin
val df = DataFrame.readJson(file)
```

The corresponding [`DataFrame`](DataFrame.md) schema is:

```text
A: String
B: Int
C: Double
D: Boolean?
```

Column A has `String` type because all values are string literals, no implicit conversion is performed. Column C
has the `Double` type because it's the smallest unified number type for `Int` and `Float`.

### JSON parsing options

#### Manage type clashes

By default, if a type clash occurs when reading JSON, a new column group is created consisting of: "value", "array", and
any number of object properties:

"value" will be set to the value of the JSON element if it's a primitive, else it will be `null`.\
"array" will be set to the array of values if the JSON element is an array, else it will be `[]`.\
If the JSON element is an object, then each property will spread out to its own column in the group, else these columns
will be `null`.

In this case `typeClashTactic = JSON.TypeClashTactic.ARRAY_AND_VALUE_COLUMNS`.

For example:

```json
[
    { "a": "text" },
    { "a": { "b": 2 } },
    { "a": [ 6, 7, 8 ] }
]
```

will be read like (including `null` and `[]` values):

```text
⌌----------------------------------------------⌍
|  | a:{b:Int?, value:String?, array:List<Int>}|
|--|-------------------------------------------|
| 0|   {b:null, value:"text",  array:[]       }|
| 1|   {b:2,    value:null,    array:[]       }|
| 2|   {b:null, value:null,    array:[6, 7, 8]}|
⌎----------------------------------------------⌏
```

This makes it more convenient to work with the data, but it can be confusing if you're not expecting it or if you
just need the type to be an `Any`.

For this case, you can set `typeClashTactic = JSON.TypeClashTactic.ANY_COLUMNS` to get the following:

```text
⌌-------------⌍
|  |     a:Any|
|--|----------|
| 0|    "text"|
| 1|   { b:2 }|
| 2| [6, 7, 8]|
⌎-------------⌏
```

This option is also possible to set in the Gradle- and KSP plugin by providing `jsonOptions`.

#### Specify Key/Value Paths

If you have a JSON like:

```json
{
    "dogs": {
        "fido": {
            "age": 3,
            "breed": "poodle"
        },
        "spot": {
            "age": 5,
            "breed": "labrador"
        },
        "rex": {
            "age": 2,
            "breed": "golden retriever"
        },
        "lucky": { ... },
        "rover": { ... },
        "max": { ... },
        "buster": { ... },
        ...
    },
    "cats": { ... }
}
```

You will get a column for each dog, which becomes an issue when you have a lot of dogs.
This issue is especially noticeable when generating data schemas from JSON, as you might run out of memory
when doing that due to the sheer number of generated interfaces. Instead, you can use `keyValuePaths` to specify paths 
to the objects that should be read as key value frame columns.

This can be the difference between:

```text
⌌---------------------------------------------------------------------------------------------------------------------------------------------...
|  |                      dogs:{fido:{age:Int, breed:String}, spot:{age:Int, breed:String}, rex:{age:Int, breed:String}, lucky:{age:Int, breed...
|--|------------------------------------------------------------------------------------------------------------------------------------------...
| 0| { fido:{ age:3, breed:poodle }, spot:{ age:5, breed:labrador }, rex:{ age:2, breed:golden retriever }, lucky:{ age:1, breed:poodle }, rov...
⌎---------------------------------------------------------------------------------------------------------------------------------------------...
```

and

```text
⌌------------------------------------------------------------------------------------------------------⌍
|  | dogs:[key:String, value:{age:Int, breed:String}]| cats:[key:String, value:{age:Int, breed:String}]|
|--|-------------------------------------------------|-------------------------------------------------|
| 0|                                          [7 x 2]|                                          [6 x 2]|
⌎------------------------------------------------------------------------------------------------------⌏
```

with dogs looking like

```text
⌌-------------------------------------------------⌍
|  | key:String|     value:{age:Int, breed:String}|
|--|-----------|----------------------------------|
| 0|       fido|           { age:3, breed:poodle }|
| 1|       spot|         { age:5, breed:labrador }|
| 2|        rex| { age:2, breed:golden retriever }|
| 3|      lucky|           { age:1, breed:poodle }|
| 4|      rover|         { age:3, breed:labrador }|
| 5|        max| { age:2, breed:golden retriever }|
| 6|     buster|           { age:1, breed:poodle }|
⌎-------------------------------------------------⌏
```

(The results are wrapped in a [`FrameColumn`](DataColumn.md#framecolumn) instead of a `ColumnGroup` since lengths between "cats" and "dogs" can vary,
among other reasons.)

To specify the paths, you can use the `JsonPath` class:

```kotlin
DataFrame.readJsonStr(
    text = myJson,
    keyValuePaths = listOf(
        JsonPath().append("dogs"), // which will result in '$["dogs"]'
        JsonPath().append("cats"), // which will result in '$["cats"]'
    ),
)
```

Note: For the KSP plugin, the `JsonPath` class is not available, so you will have to use the `String` version of the
paths instead. For example: `jsonOptions = JsonOptions(keyValuePaths = ["""$""", """$[*]["versions"]"""])`.
Only the bracket notation of json path is supported, as well as just double quotes, arrays, and wildcards.

For more examples, see the "examples/json" module.

## Read from Excel

Before you can read data from Excel, add the following dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-excel:$dataframe_version")
```

It's included by default if you have `org.jetbrains.kotlinx:dataframe:$dataframe_version` already.

To read an Excel spreadsheet, use the `.readExcel()` function. Excel spreadsheets can be read from a file or a URL. Supported
Excel spreadsheet formats are: xls, xlsx.

To read an Excel spreadsheet from a file:

```kotlin
val df = DataFrame.readExcel(file)
```

To read an Excel spreadsheet from a URL:

```kotlin
DataFrame.readExcel("https://example.com/data.xlsx")
```

### Cell type inference from Excel

Cells representing dates will be read as `kotlinx.datetime.LocalDateTime`.
Cells with number values, including whole numbers such as "100", or calculated formulas will be read as `Double`.

Sometimes cells can have the wrong format in an Excel file. For example, you expect to read a column of `String`:

```text
IDS
100 <-- Intended to be String, but has numeric cell format in original .xlsx file
A100
B100
C100
```

You will get column of `Serializable` instead (common parent for `Double` and `String`).

You can fix it by providing an additional parameter:

<!---FUN fixMixedColumn-->

```kotlin
val df = DataFrame.readExcel("mixed_column.xlsx", stringColumns = StringColumns("A"))
```

<!---END-->

## Read Apache Arrow formats

Before you can read data from Apache Arrow format, add the following dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-arrow:$dataframe_version")
```

It's included by default if you have `org.jetbrains.kotlinx:dataframe:$dataframe_version` already.

To read Apache Arrow formats, use the `.readArrowFeather()` function:

<!---FUN readArrowFeather-->

```kotlin
val df = DataFrame.readArrowFeather(file)
```

<!---END-->

[`DataFrame`](DataFrame.md) supports reading [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format)
and [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files)
from raw Channel (ReadableByteChannel for streaming and SeekableByteChannel for random access), ArrowReader, InputStream, File, or ByteArray.

> If you use Java 9+, follow the [Apache Arrow Java compatibility](https://arrow.apache.org/docs/java/install.html#java-compatibility) guide.
>
{style="note"}
