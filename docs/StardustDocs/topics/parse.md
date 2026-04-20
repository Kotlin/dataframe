[//]: # (title: parse)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Parse-->

Returns a [`DataFrame`](DataFrame.md) in which the given `String` and `Char` columns are parsed into other types.

This is a special case of the [](convert.md) operation.

This parsing operation is sometimes executed implicitly, for example, when [reading from CSV](read.md) or
[type converting from `String`/`Char` columns](convert.md).
You can recognize this by the `locale` or `parserOptions` arguments in these functions.

Related operations: [](updateConvert.md)

<inline-frame src="./resources/dfParse.html" width="100%" height="500px"></inline-frame>

<!---FUN parseAll-->

```kotlin
df.parse()
```

<!---END-->

<inline-frame src="./resources/parseAll.html" width="100%" height="500px"></inline-frame>


When no columns are specified, all `String` and `Char` columns are parsed,
even those inside [column groups](DataColumn.md#columngroup) and inside [frame columns](DataColumn.md#framecolumn).

To parse only particular columns, use a [column selector](ColumnSelectors.md):

<!---FUN parseSome-->

```kotlin
df.parse { date and value }
```
<inline-frame src="./resources/parseSome.html" width="100%" height="500px"></inline-frame>

<!---END-->

### Parsing Order

`parse` tries to parse every `String`/`Char` column into one of the supported types in the following order:

| Type                                                                                                                                                     | Notes                                                                                                                                         |
|----------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `Int`                                                                                                                                                    |                                                                                                                                               |
| `Long`                                                                                                                                                   |                                                                                                                                               |
| `Instant` (`kotlin.time`)                                                                                                                                | (**K**) - Requires `parseExperimentalInstant = true`, enabled by default in DataFrame 1.0.0-Beta5.                                            |
| `Instant` (`kotlinx.datetime`)                                                                                                                           | (**K**) - Requires `parseExperimentalInstant = false`.                                                                                        |
| Custom Kotlin date-time types (`kotlinx.datetime`):<br/>`LocalDateTime`, `LocalDate`, `LocalTime`, `YearMonth`, `UtcOffset`, `DateTimeComponents`        | (**K**) - Where a `DateTimeFormat` is provided for in [](#parser-options).                                                                    |
| Custom Java date-time types (`java.time`):<br/>`LocalDateTime`, `LocalDate`, `LocalTime`, `Instant`                                                      | (**J**) - Where a `DateTimeFormatter` is provided for in [](#parser-options).                                                                 |
| Custom global Kotlin date-time types (`kotlinx.datetime`):<br/>`LocalDateTime`, `LocalDate`, `LocalTime`, `YearMonth`, `UtcOffset`, `DateTimeComponents` | (**K**, **P**) - Where a `DateTimeFormat` is provided for in [](#global-parser-options).                                                      |
| Custom global Java date-time types (`java.time`):<br/>`LocalDateTime`, `LocalDate`, `LocalTime`, `Instant`                                               | (**J**, **P**) - Where a `DateTimeFormatter` is provided for in [](#global-parser-options).                                                   |
| Default Kotlin date-time ISO types (`kotlinx.datetime`):<br/>`LocalDateTime`, `LocalDate`, `LocalTime`, `YearMonth`, `UtcOffset`, `DateTimeComponents`   | (**K**, **P**)                                                                                                                                |
| Default Java date-time ISO types (`java.time`):<br/>`LocalDateTime`, `LocalDate`, `LocalTime`, `Instant`                                                 | (**J**, **P**)                                                                                                                                |
| `Duration` (`kotlin.time`)                                                                                                                               | (**K**)                                                                                                                                       |
| `Duration` (`java.time`)                                                                                                                                 | (**J**)                                                                                                                                       |
| `Month` (`kotlinx.datetime`)                                                                                                                             | (**K**)                                                                                                                                       |
| `DayOfWeek` (`kotlinx.datetime`)                                                                                                                         | (**K**)                                                                                                                                       |
| `URL` (`java.net`)                                                                                                                                       |                                                                                                                                               |
| [`Double` (with optional locale settings)](#parsing-doubles)                                                                                             |                                                                                                                                               |
| `Double`                                                                                                                                                 | with "C.UTF-8" locale, used as fallback for `Double` parsing.                                                                                 |
| `Boolean`                                                                                                                                                | "true" / "false", "t" / "f", "yes" / "no" with any capitalization.                                                                            |
| `Uuid` ([`kotlin.uuid.Uuid`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.uuid/-uuid/))                                                          | Requires `parseExperimentalUuid = true`.                                                                                                      |
| `BigInteger` (`java.math`)                                                                                                                               |                                                                                                                                               |
| `BigDecimal` (`java.math`)                                                                                                                               |                                                                                                                                               |
| `JSON`                                                                                                                                                   | Requires the `org.jetbrains.kotlinx:dataframe-json` dependency.<br/>Arrays will turn into a dataframe.<br/>Objects will turn into a data row. |
| `Char`                                                                                                                                                   |                                                                                                                                               |
| `String`                                                                                                                                                 |                                                                                                                                               |

> **K**: Kotlin date-time parsing must not be disabled for this parser to run.
> 
> **J**: Java date-time parsing must not be disabled for this parser to run.
> 
> **P**: this parser only runs when **no** custom date-time formats are provided in [](#parser-options).
> 
> See [](#parser-options), [](#global-parser-options), and [](#parsing-date-time-strings).

You can get this list by accessing `availableParserTypes` on the [](#global-parser-options) as well.

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
* `locale: Locale` is used to [parse doubles](#parsing-doubles) (and Java date-time types)
  * Global default locale is `Locale.getDefault()`
* `dateTime: DateTimeParserOptions` can be used to force parsing to Kotlin-, or Java date-time classes, and override
   default and custom global date-time formats. By default, it's `null`, meaning we try Kotlin types first,
   and if that fails, we try Java types. See [](#parsing-date-time-strings).
   This argument was added in DataFrame 1.0.0-Beta5.
* `nullStrings: List<String>` is used to treat particular strings as `null` value
  * Global default null strings are `["null", "NULL", "NA", "N/A"]`.
  * When [reading from CSV](read.md), these are expanded to `["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]`.
  See the KDocs there for the exact details
* `skipTypes: Set<KType>` types that should be skipped during parsing
  * Empty set by global default; parsing can result in any [supported type](#parsing-order).
* `useFastDoubleParser: Boolean` is used to enable or disable the [new fast double parser](#parsing-doubles)
  * Enabled by global default
* `parseExperimentalUuid: Boolean` is used to enable or disable parsing to the experimental [`kotlin.uuid.Uuid` class](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.uuid/-uuid/).
  * Disabled by global default
* `parseExperimentalInstant: Boolean` is used to enable or disable parsing to the 
  [`kotlin.time.Instant` class](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-instant/), available from Kotlin 2.1+. Will parse to `kotlinx.datetime.Instant` if `false`.
  * Disabled by global default, enabled in DataFrame 1.0.0-Beta5.

<inline-frame src="./resources/dfParseWithOptions.html" width="100%" height="500px"></inline-frame>

<!---FUN parseWithOptions-->

```kotlin
df.parse(
    options = ParserOptions(
        locale = Locale.GERMAN,
        dateTime = DateTimeParserOptions.Java
            .withFormatter<java.time.LocalDateTime>(formatter = DateTimeFormatter.ISO_WEEK_DATE),
    ),
)
```

<!---END-->

<inline-frame src="./resources/parseWithOptions.html" width="100%" height="500px"></inline-frame>

### Global Parser Options

As mentioned before, you can change the default global parser options that will be used by [`read`](read.md),
[`convert`](convert.md), and other `parse` operations.
Whenever you don't explicitly provide [parser options](#parser-options) to a function call or leave any of its
arguments `null`, DataFrame will use these global options instead.

For example, to change the locale to French and add a custom Java date-time pattern for all following DataFrame calls, do:

<!---FUN globalParserOptions-->

```kotlin
DataFrame.parser.locale = Locale.FRANCE
DataFrame.parser.addJavaDateTimePattern("dd.MM.uuuu HH:mm:ss")
```

<!---END-->

For `locale`, this means that the one being used by the parser is defined as:

↪ The locale given as function argument directly, or in `parserOptions`, if it is not `null`, else

&nbsp;&nbsp;&nbsp;&nbsp;↪ The locale set by `DataFrame.parser.locale = ...`, if it is not `null`, else

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↪ `Locale.getDefault()`, which is the system's default locale that can be changed with `Locale.setDefault()`.

Global Parser Options can also be adjusted to change whether some parsers are included or excluded in a parsing call:
- `parseExperimentalUuid`, `parseExperimentalInstant`
- `skipTypes`
- `dateTimeLibrary` (`JAVA`, `KOTLIN`, or `null`)

These settings, however, will only affect functions that call `parse()`.
They will not affect the behavior of [`convert`](convert.md) operations
(with `useFastDoubleParser` being the exception).

In other words:

<!---FUN globalParserOptionsConvertCombination-->

```kotlin
DataFrame.parser.parseExperimentalUuid = false
stringCol.convertTo<kotlin.uuid.Uuid>() // will still parse to `kotlin.uuid.Uuid`, as expected
```

<!---END-->

Global parser options can always be reset to default by calling:

<!---FUN resetGlobalParserOptions-->

```kotlin
DataFrame.parser.resetToDefault()
```

<!---END-->

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

### Parsing Date-time Strings

> This functionality has been changed significantly in DataFrame 1.0.0-Beta5,
> giving you a clear choice between Kotlin stdlib/[`kotlinx-datetime`](https://github.com/Kotlin/kotlinx-datetime)
> and [`java.time`](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html) types.
> 
> We recommend using types,
> however, kotlinx-datetime [lacks localization support](https://github.com/Kotlin/kotlinx-datetime/discussions/253).
> 
> If you need to provide a custom `Locale`, we recommend parsing 
> to a `java.time`-based class first by adjusting the [](#parser-options) before [converting](convert.md)
> it to `kotlinx.datetime`.

By default, DataFrame tries parsing date-time strings using
- Custom [global](#global-parser-options) Kotlin-, and Java date-time formats, if provided;
- Default Kotlin-, and Java ISO date-time formats.

You can customize this behavior from the [](#global-parser-options) by:
- Providing custom date-time formats/formatters and/or custom date-time patterns:
    - For Kotlin date-time types: `addDateTimeFormat<T>(format)`, `addDateTimeUnicodePattern<T>(pattern)`
    - For Java date-time types: `addJavaDateTimeFormatter<T>(formatter)`, `addJavaDateTimePattern<T>(pattern)`;
- Forcing one or the other date-time format type by changing `dateTimeLibrary` to `KOTLIN` or `JAVA`
    - (by default, `null`; both can be parsed to, but Kotlin has priority). 
- Resetting to default formats;

Example, parsing a date-time string by adding a custom format to [global parser options](#global-parser-options):
<!---FUN globalParserOptionsAddDateTimeFormat-->
<tabs>
<tab title="Kotlin">

```kotlin
// Adding a custom DateTimeFormat using the kotlinx-datetime Format-DSL
val format = LocalDate.Format {
    monthNumber(padding = Padding.SPACE)
    char('/')
    day()
    char(' ')
    year()
}
DataFrame.parser.addDateTimeFormat(format)

// now this will succeed!
columnOf("12/24 2023").parse()
```

</tab>
<tab title="Java">

```kotlin
val formatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .appendLiteral('/')
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .appendLiteral(' ')
    .appendValue(ChronoField.YEAR, 4)
    .toFormatter()

// Adding a custom DateTimeFormatter type-safely for LocalDate only
DataFrame.parser.addJavaDateTimeFormatter<java.time.LocalDate>(formatter)

// or, adding it for all java-types: Local(Date)(Time), and Instant
DataFrame.parser.addJavaDateTimeFormatter(formatter)

// setting the locale to US
DataFrame.parser.locale = Locale.US

// now this will succeed!
columnOf("12/24 2023").parse()
```

</tab></tabs>
<!---END-->

Example, parsing a date-time string by adding a custom pattern to [global parser options](#global-parser-options):

<!---FUN globalParserOptionsAddPattern-->
<tabs>
<tab title="Kotlin">

```kotlin
// Adding a custom DateTimeFormat using the kotlinx-datetime Unicode pattern
// This requires explicitly opting in and providing a type
@OptIn(FormatStringsInDatetimeFormats::class)
DataFrame.parser.addDateTimeUnicodePattern<LocalDate>("MM/dd yyyy")

// now this will succeed!
columnOf("12/24 2023").parse()
```

</tab>
<tab title="Java">

```kotlin
// Adding a custom DateTimeFormatter by pattern type-safely for LocalDate only
DataFrame.parser.addJavaDateTimePattern<java.time.LocalDate>("MM/dd yyyy")

// or, adding it for all java-types: Local(Date)(Time), and Instant
DataFrame.parser.addJavaDateTimePattern("MM/dd yyyy")

// setting the locale to US
DataFrame.parser.locale = Locale.US

// now this will succeed!
columnOf("12/24 2023").parse()
```

</tab></tabs>
<!---END-->

It is only possible to supply patterns or formats in a supported date-time type.
For Kotlin, these are `LocalDateTime`, `LocalDate`, `LocalTime`, `YearMonth`, `UtcOffset`, and `DateTimeComponents`
(a.k.a. all [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) types that have a `.Format {}` builder).

For Java, these are `LocalDateTime`, `LocalDate`, `LocalTime`, and `Instant`.
We might expand these in the future. Let us know if you need any other types.

**`ParserOptions.dateTime: DateTimeParserOptions`:**

If a parsing function is provided with [`ParserOptions`](#parser-options) and `ParserOptions.dateTime`
is not `null`, the global `dateTimeLibrary` parser option will be overridden.

Concretely, `ParserOptions(dateTime = DateTimeParserOptions.Java)` is equivalent to having
`DataFrame.parser.dateTimeLibrary = ParseDateTimeLibrary.JAVA` for that particular function call.
In addition, if that `DateTimeParserOptions` has any custom formats or patterns, the custom- and default
global formats will be ignored, allowing you to essentially override them.

The two `DateTimeParserOptions` can be created from a set of type-format(ter) pairs, or using a builder-like pattern:

Example, parsing a date-time string by adding a custom format to [parser options](#parser-options):

<!---FUN parserOptionsWithDateTimeFormat-->
<tabs>
<tab title="Kotlin">

```kotlin
val format = LocalDate.Format {
    monthNumber(padding = Padding.SPACE)
    char('/')
    day()
    char(' ')
    year()
}

// now this will succeed!
columnOf("12/24 2023")
    .parse(
        options = ParserOptions(
            dateTime = DateTimeParserOptions.Kotlin
                .withFormat(format),
        ),
    )
```

</tab>
<tab title="Java">

```kotlin
val formatter = DateTimeFormatterBuilder()
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .appendLiteral('/')
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .appendLiteral(' ')
    .appendValue(ChronoField.YEAR, 4)
    .toFormatter()

// Adding a custom DateTimeFormatter type-safely for LocalDate only
DataFrame.parser.addJavaDateTimeFormatter<java.time.LocalDate>(formatter)

// or, adding it for all java-types: Local(Date)(Time), and Instant
DataFrame.parser.addJavaDateTimeFormatter(formatter)

// now this will succeed!
columnOf("12/24 2023").parse(
    options = ParserOptions(
        dateTime = DateTimeParserOptions.Java
            // Supplying a custom DateTimeFormatter type-safely for LocalDate only
            .withFormatter<java.time.LocalDate>(formatter)
            // or, supplying it for all java-types: Local(Date)(Time), and Instant
            .withFormatter(formatter)
            // setting the locale to US
            .withLocale(Locale.US),
    ),
)
```

</tab></tabs>
<!---END-->

Example, parsing a date-time string by adding a custom pattern to [parser options](#parser-options):

<!---FUN parserOptionsWithPattern-->
<tabs>
<tab title="Kotlin">

```kotlin
// Now this will succeed!
// This requires explicitly opting in and providing a type
@OptIn(FormatStringsInDatetimeFormats::class)
columnOf("12/24 2023")
    .parse(
        options = ParserOptions(
            dateTime = DateTimeParserOptions.Kotlin
                .withPattern<LocalDate>("MM/dd yyyy"),
        ),
    )
```

</tab>
<tab title="Java">

```kotlin
// Now this will succeed!
columnOf("12/24 2023")
    .parse(
        options = ParserOptions(
            dateTime = DateTimeParserOptions.Java
                // Supplying a custom pattern type-safely for LocalDate only
                .withPattern<java.time.LocalDate>("MM/dd yyyy")
                // or, supplying it for all java-types: Local(Date)(Time), and Instant
                .withPattern("MM/dd yyyy")
                // setting the locale to US
                .withLocale(Locale.US),
        ),
    )
```

</tab></tabs>
<!---END-->

Some functions, like [`convertToLocalDate()`](convert.md), take a `DateTimeFormat` or Unicode date-time pattern directly.
This is a shortcut for `ParserOptions`+`DateTimeParserOptions`
that behaves exactly the same as the builder-like pattern above.

**Java `Locale` argument:**

`DateTimeParserOptions.Java` has a `locale` argument.
This can adjust the locale used for parsing date-time strings
and can have a different value than the locale in [](#parser-options).
If `null`, [`ParserOptions.locale`](#parser-options)
will be used instead. If that is `null`, we default to [](#global-parser-options),
and finally to the default system locale.

**Kotlin `DateTimeComponents` fallback mechanism:**

When using [`DataFrame.convert` or `DataColumn.convertTo`](convert.md) to
convert from `String` to a kotlinx-datetime type, like `LocalDate`, fails to parse,
the `DateTimeComponents` fallback-mechanism kicks in.
Oftentimes it may namely be possible to parse the date-time string to the more flexible `DateTimeComponents`
first and then convert that to `LocalDate` with a potential little loss of information.
This means we can successfully call:

```kotlin
columnOf("Mon, 30 Jun 2008 11:05:30 -0300").convertTo<LocalDate>()
```
even though
```kotlin
columnOf("Mon, 30 Jun 2008 11:05:30 -0300").parse()
```
would produce a `DateTimeComponents` column.

Take this mechanism into account when providing custom `DateTimeFormats` to the
([global](#global-parser-options)) [ParserOptions](#parser-options).
