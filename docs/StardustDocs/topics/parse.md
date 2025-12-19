[//]: # (title: parse)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns a [`DataFrame`](DataFrame.md) in which the given `String` and `Char` columns are parsed into other types.

This is a special case of the [](convert.md) operation.

This parsing operation is sometimes executed implicitly, for example, when [reading from CSV](read.md) or
[type converting from `String`/`Char` columns](convert.md).
You can recognize this by the `locale` or `parserOptions` arguments in these functions.

Related operations: [](updateConvert.md)

<!---FUN parseAll-->

```kotlin
df.parse()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.parseAll.html" width="100%"/>
<!---END-->

When no columns are specified, all `String` and `Char` columns are parsed,
even those inside [column groups](DataColumn.md#columngroup) and inside [frame columns](DataColumn.md#framecolumn).

To parse only particular columns, use a [column selector](ColumnSelectors.md):

<!---FUN parseSome-->

```kotlin
df.parse { age and weight }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.parseSome.html" width="100%"/>
<!---END-->

### Parsing Order

`parse` tries to parse every `String`/`Char` column into one of the supported types in the following order:
* `Int`
* `Long`
* `Instant` (`kotlin.time`) (requires `parseExperimentalInstant = true`, enabled by default in DataFrame 1.0.0-Beta5)
* `Instant` (`kotlinx.datetime` and `java.time`) (requires `parseExperimentalInstant = false`)
* `LocalDateTime` (`kotlinx.datetime` and `java.time`)
* `LocalDate` (`kotlinx.datetime` and `java.time`)
* `Duration` (`kotlin.time` and `java.time`)
* `LocalTime` (`java.time`)
* `URL` (`java.net`)
* [`Double` (with optional locale settings)](#parsing-doubles)
* `Boolean`
* `Uuid` ([`kotlin.uuid.Uuid`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.uuid/-uuid/)) (requires `parseExperimentalUuid = true`) 
* `BigDecimal`
* `JSON` (arrays and objects) (requires the `org.jetbrains.kotlinx:dataframe-json` dependency)
* `Char`
* `String`

When `.parse()` is called on a single column and the input (`String`/`Char`) type is the same as the output type,
(a.k.a., it cannot be parsed further) an `IllegalStateException` is thrown.
To avoid this, use `col.tryParse()` instead.

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
* `parseExperimentalUuid: Boolean` is used to enable or disable parsing to the experimental [`kotlin.uuid.Uuid` class](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.uuid/-uuid/).
  * Disabled by global default
* `parseExperimentalInstant: Boolean` is used to enable or disable parsing to the 
  [`kotlin.time.Instant` class](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-instant/), available from Kotlin 2.1+. Will parse to `kotlinx.datetime.Instant` if `false`.
  * Disabled by global default, enabled in DataFrame 1.0.0-Beta5.

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

For `locale`, this means that the one being used by the parser is defined as:

↪ The locale given as function argument directly, or in `parserOptions`, if it is not `null`, else

&nbsp;&nbsp;&nbsp;&nbsp;↪ The locale set by `DataFrame.parser.locale = ...`, if it is not `null`, else

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↪ `Locale.getDefault()`, which is the system's default locale that can be changed with `Locale.setDefault()`.

### Parsing Doubles

DataFrame has a new fast and powerful double parser enabled by default.
It is based on [the FastDoubleParser library](https://github.com/wrandelshofer/FastDoubleParser) for its
high performance and configurability
(in the future, we might expand this support to `Float`, `BigDecimal`, and `BigInteger` as well).

The parser is locale-aware; it will use the locale set by the
[(global)](#global-parser-options) [parser options](#parser-options) to parse the doubles.
It also has a fallback mechanism built in, meaning it can recognize characters from
all other locales (and some from [Wikipedia](https://en.wikipedia.org/wiki/Decimal_separator))
and parse them correctly as long as they don't conflict with the current locale.

For example, if your locale uses ',' as decimal separator, it will not recognize ',' as thousands separator, but it will
recognize ''', ' ', '٬', '_', ' ', etc. as such.
The same holds for characters like "e", "inf", "×10^", "NaN", etc. (ignoring case).

This means you can safely parse `"123'456 789,012.345×10^6"` with a US locale but not `"1.234,5"`.

Aside from this, DataFrame also explicitly recognizes "∞", "inf", "infinity", and "infty" as `Double.POSITIVE_INFINITY`
(as well as their negative counterparts), "nan", "na", and "n/a" as `Double.NaN`,
and all forms of whitespace are treated equally.

If `FastDoubleParser` fails to parse a `String` as `Double`, DataFrame will try
to parse it using the standard `NumberFormat.parse()` function as a last resort.

If you experience any issues with the new parser, you can turn it off by setting
`useFastDoubleParser = false`, which will use the old `NumberFormat.parse()` function instead.

Please [report](https://github.com/Kotlin/dataframe/issues) any issues you encounter. 
