[//]: # (title: Read)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Read-->

`DataFrame` supports CSV, TSV, JSON, XLS and XLSX, Apache Arrow input formats.

`read` method automatically detects input format based on file extension and content

```kotlin
DataFrame.read("input.csv")
```

Input string can be a file path or URL.

## Reading CSV

All these calls are valid:

```kotlin
import java.io.File
import java.net.URL

DataFrame.readCSV("input.csv")
DataFrame.readCSV(File("input.csv"))
DataFrame.readCSV(URL("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv"))
```

All `readCSV` overloads support different options.
For example, you can specify custom delimiter if it differs from `,`, charset
and column names if your CSV is missing them

<!---FUN readCsvCustom-->

```kotlin
val df = DataFrame.readCSV(
    file,
    delimiter = '|',
    header = listOf("A", "B", "C", "D"),
    parserOptions = ParserOptions(nullStrings = setOf("not assigned"))
)
```

<!---END-->

Column types will be inferred from the actual CSV data. Suppose that CSV from the previous
example had the following content:

<table>
<tr><th>A</th><th>B</th><th>C</th><th>D</th></tr>
<tr><td>12</td><td>tuv</td><td>0.12</td><td>true</td></tr>
<tr><td>41</td><td>xyz</td><td>3.6</td><td>not assigned</td></tr>
<tr><td>89</td><td>abc</td><td>7.1</td><td>false</td></tr>
</table>

Dataframe schema we get is:

```text
A: Int
B: String
C: Double
D: Boolean?
```

DataFrame will try to parse columns as JSON, so when reading following table with JSON object in column D:

<table>
<tr><th>A</th><th>D</th></tr>
<tr><td>12</td><td>{"B":2,"C":3}</td></tr>
<tr><td>41</td><td>{"B":3,"C":2}</td></tr>
</table>

We get this data schema where D is ColumnGroup with 2 children columns:

```text
A: Int
D:
    B: Int
    C: Int
```

For column where values are lists of JSON values:
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

### Dealing with locale specific numbers

Sometimes columns in your CSV can be interpreted differently depending on your system locale.

<table>
<tr><th>numbers</th></tr>
<tr><td>12,123</td></tr>
<tr><td>41,111</td></tr>
</table>

Here comma can be decimal or thousands separator, thus different values.
You can deal with it in two ways

1) Provide locale as a parser option

<!---FUN readNumbersWithSpecificLocale-->

```kotlin
val df = DataFrame.readCSV(
    file,
    parserOptions = ParserOptions(locale = Locale.UK),
)
```

<!---END-->

2) Disable type inference for specific column and convert it yourself

<!---FUN readNumbersWithColType-->

```kotlin
val df = DataFrame.readCSV(
    file,
    colTypes = mapOf("colName" to ColType.String)
)
```

<!---END-->


## Reading JSON

Basics for reading JSONs are the same: you can read from file or from remote URL.

```kotlin
DataFrame.readJson("https://covid.ourworldindata.org/data/owid-covid-data.json")
```

Note that after reading a JSON with a complex structure, you can get hierarchical
dataframe: dataframe with `ColumnGroup`s and `FrameColumn`s.

Also note that type inferring process for JSON is much simpler than for CSV.
JSON string literals are always supposed to have String type, number literals
take different `Number` kinds, boolean literals are converted to `Boolean`.

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

We can read it from file

<!---FUN readJson-->

```kotlin
val df = DataFrame.readJson(file)
```

<!---END-->

Corresponding dataframe schema will be

```text
A: String
B: Int
C: Number
D: Boolean?
```

Column A has `String` type because all values are string literals, no implicit conversion is performed. Column C
has `Number` type because it's the least common type for `Int` and `Double`.

### JSON Reading Options: Type Clash Tactic

By default, if a type clash occurs when reading JSON, a new column group is created consisting of: "value", "array", and
any number of object properties:

"value" will be set to the value of the JSON element if it's a primitive, else it will be `null`.\
"array" will be set to the array of values if the json element is an array, else it will be `[]`.\
If the json element is an object, then each property will spread out to its own column in the group, else these columns
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

### JSON Reading Options: Key/Value Paths

If you have some JSON looking like

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

you will get a column for each dog, which becomes an issue when you have a lot of dogs.
This issue is especially noticeable when generating data schemas from the JSON, as you might even run out of memory
when doing that due to the sheer number of generated interfaces.\
Instead, you can use `keyValuePaths` to specify paths to the objects that should be read as key value frame columns.

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

(The results are wrapped in a `FrameColumn` instead of a `ColumnGroup` since lengths between "cats" and "dogs" can vary,
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

## Reading Excel

Add dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-excel:$dataframe_version")
```

Right now DataFrame supports reading Excel spreadsheet formats: xls, xlsx.

You can read from file or URL.

Cells representing dates will be read as `kotlinx.datetime.LocalDateTime`.
Cells with number values, including whole numbers such as "100", or calculated formulas will be read as `Double`

Sometimes cells can have wrong format in Excel file, for example you expect to read column of String:

```text
IDS
100 <-- Intended to be String, but has wrong cell format in original .xlsx file
A100
B100
C100
```

You will get column of Serializable instead (common parent for Double & String)

You can fix it using convert:

<!---FUN fixMixedColumn-->

```kotlin
val df = dataFrameOf("IDS")(100.0, "A100", "B100", "C100")
val df1 = df.convert("IDS").with(Infer.Type) {
    if (it is Double) {
        it.toLong().toString()
    } else {
        it
    }
}
df1["IDS"].type() shouldBe typeOf<String>()
```

<!---END-->

## Reading Apache Arrow formats

Add dependency:

```kotlin
implementation("org.jetbrains.kotlinx:dataframe-arrow:$dataframe_version")
```

<warning>
Make sure to follow [Apache Arrow Java compatibility](https://arrow.apache.org/docs/java/install.html#java-compatibility) guide when using Java 9+ 
</warning>

Dataframe supports reading
from [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format)
and [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files)

<!---FUN readArrowFeather-->

```kotlin
val df = DataFrame.readArrowFeather(file)
```

<!---END-->


