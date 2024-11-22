[//]: # (title: parse)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns a [`DataFrame`](DataFrame.md) in which the given `String` columns are parsed into other types.

This is a special case of the [convert](convert.md) operation.

<!---FUN parseAll-->

```kotlin
df.parse()
```

<!---END-->

To parse only particular columns use a [column selector](ColumnSelectors.md):

<!---FUN parseSome-->

```kotlin
df.parse { age and weight }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.parseSome.html"/>
<!---END-->

`parse` tries to parse every `String` column into one of supported types in the following order:
* `Int`
* `Long`
* `Instant` (`kotlinx.datetime` and `java.time`)
* `LocalDateTime` (`kotlinx.datetime` and `java.time`)
* `LocalDate` (`kotlinx.datetime` and `java.time`)
* `Duration` (`kotlin.time` and `java.time`)
* `LocalTime` (`java.time`)
* `URL` (`java.net`)
* `Double` (with optional locale settings)
* `Boolean`
* `BigDecimal`
* `JSON` (arrays and objects)

Available parser options:
* `locale: Locale` is used to parse doubles
* `dateTimePattern: String` is used to parse date and time
* `dateTimeFormatter: DateTimeFormatter` is used to parse date and time
* `nullStrings: List<String>` is used to treat particular strings as `null` value. Default null strings are **"null"** and **"NULL"**

<!---FUN parseWithOptions-->

```kotlin
df.parse(options = ParserOptions(locale = Locale.CHINA, dateTimeFormatter = DateTimeFormatter.ISO_WEEK_DATE))
```

<!---END-->

You can also set global parser options that will be used by default in [`read`](read.md), [`convert`](convert.md),
and `parse` operations:

<!---FUN globalParserOptions-->

```kotlin
DataFrame.parser.locale = Locale.FRANCE
DataFrame.parser.addDateTimePattern("dd.MM.uuuu HH:mm:ss")
```

<!---END-->
