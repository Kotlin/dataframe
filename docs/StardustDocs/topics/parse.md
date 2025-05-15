[//]: # (title: parse)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns a [`DataFrame`](DataFrame.md) in which the given `String` columns are parsed into other types.

This is a special case of the [convert](convert.md) operation.

This parsing operation is sometimes executed implicitly, for example, when [reading from CSV](read.md) or
[type converting from `String` columns](convert.md).
You can recognize this by the `locale` or `parserOptions` arguments in these functions.

<!---FUN parseAll-->

```kotlin
df.parse()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.parseAll.html" width="100%"/>
<!---END-->

To parse only particular columns use a [column selector](ColumnSelectors.md):

<!---FUN parseSome-->

```kotlin
df.parse { age and weight }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.parseSome.html" width="100%"/>
<!---END-->

### Parsing Order

`parse` tries to parse every `String` column into one of supported types in the following order:
* `Int`
* `Long`
* `Instant` (`kotlinx.datetime` and `java.time`)
* `LocalDateTime` (`kotlinx.datetime` and `java.time`)
* `LocalDate` (`kotlinx.datetime` and `java.time`)
* `Duration` (`kotlin.time` and `java.time`)
* `LocalTime` (`java.time`)
* `URL` (`java.net`)
* [`Double` (with optional locale settings)](#parsing-doubles)
* `Boolean`
* `BigDecimal`
* `JSON` (arrays and objects) (requires the `org.jetbrains.kotlinx:dataframe-json` dependency)

### Parser Options

DataFrame supports multiple parser options that can be used to customize the parsing behavior.
These can be supplied to the `parse` function (or any other function that can implicitly parse `Strings`)
as an argument.

For each option you don't supply (or supply `null`) DataFrame will take the value from the
[Global Parser Options](#global-parser-options).

Available parser options:
* `locale: Locale` is used to [parse doubles](#parsing-doubles)
  * Global default locale is `Locale.getDefault()`
* `dateTimePattern: String` is used to parse date and time
  * Global default supports ISO (local) date-time
* `dateTimeFormatter: DateTimeFormatter` is used to parse date and time
  * Is derived from `dateTimePattern` and/or `locale` if `null`
* `nullStrings: List<String>` is used to treat particular strings as `null` value
  * Global default null strings are **"null"** and **"NULL"**
  * When [reading from CSV](read.md), we include even more defaults, like **""**, and **"NA"**.
  See the KDocs there for the exact details
* `skipTypes: Set<KType>` types that should be skipped during parsing
  * Empty set by global default; parsing can result in any supported type
* `useFastDoubleParser: Boolean` is used to enable or disable the [new fast double parser](#parsing-doubles)
  * Enabled by global default

<!---FUN parseWithOptions-->

```kotlin
df.parse(options = ParserOptions(locale = Locale.CHINA, dateTimeFormatter = DateTimeFormatter.ISO_WEEK_DATE))
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.parseWithOptions.html" width="100%"/>
<!---END-->

### Global Parser Options

As mentioned before, you can change the default global parser options that will be used by [`read`](read.md),
[`convert`](convert.md), and other `parse` operations.
Whenever you don't explicitly provide [parser options](#parser-options) to a function call,
DataFrame will use these global options instead.

For example, to change the locale to French and add a custom date-time pattern for all following DataFrame calls, do:

<!---FUN globalParserOptions-->

```kotlin
DataFrame.parser.locale = Locale.FRANCE
DataFrame.parser.addDateTimePattern("dd.MM.uuuu HH:mm:ss")
```

<!---END-->
