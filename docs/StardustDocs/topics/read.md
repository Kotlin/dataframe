[//]: # (title: Reading dataframes)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Read-->

`DataFrame` supports CSV and JSON input formats.

`read` method automatically detects input format based on file extension and content

```kotlin
DataFrame.read("input.csv")
```

Input string can be a file path or URL.

### Reading CSV
All these calls are valid:

```kotlin
import java.io.File
import java.net.URL

DataFrame.readCSV("input.csv")
DataFrame.readCSV(File("input.csv"))
DataFrame.readCSV(URL("https://raw.githubusercontent.com/Kotlin/dataframe/master/data/securities.csv"))
```

All `readCSV` overloads support different options.
For example, you can specify custom delimiter if it differs from `,`, charset
and headers names if your CSV is missing them

<!---FUN readCsvCustom-->

```kotlin
val df = DataFrame.readCSV(
    file,
    delimiter = '|',
    headers = listOf("A", "B", "C", "D"),
    parserOptions = ParserOptions(nulls = setOf("not assigned"))
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

### Reading JSON
Basics for reading JSONs are the same: you can read from file or from remote URL.

```kotlin
DataFrame.readJson("https://covid.ourworldindata.org/data/owid-covid-data.json")
```

Note that after reading a JSON with a complex structure, you can get hierarchical
dataframe: dataframe with `GroupColumn`s and `FrameColumn`s.

Also note that type inferring process for JSON is much simpler than for CSV.
JSON string literals are always supposed to have String type, number literals
take different `Number` kinds, boolean literals are converted to `Boolean`.

Let's take a look at the following JSON:

```json
[
  { "A": "1", "B": 1, "C": 1.0, "D": true },
  { "A": "2", "B": 2, "C": 1.1, "D": null },
  { "A": "3", "B": 3, "C": 1, "D": false },
  { "A": "4", "B": 4, "C": 1.3, "D": true }
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

Column A has `String` type because all values are string literals, no implicit conversion is performed. Column C has `Number` type because it's the least common type for `Int` and `Double`.
