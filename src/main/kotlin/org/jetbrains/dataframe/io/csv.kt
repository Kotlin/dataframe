package org.jetbrains.dataframe.io

import org.apache.commons.csv.CSVFormat
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.impl.ColumnNameGenerator
import java.io.*
import java.math.BigDecimal
import java.net.URL
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

internal val defaultCsvFormat = CSVFormat.DEFAULT.withHeader().withIgnoreSurroundingSpaces()

internal val defaultTdfFormat = CSVFormat.TDF.withHeader().withIgnoreSurroundingSpaces()

internal fun isCompressed(fileOrUrl: String) = listOf("gz", "zip").contains(fileOrUrl.split(".").last())

internal fun isCompressed(file: File) = listOf("gz", "zip").contains(file.extension)

internal fun isCompressed(url: URL) = isCompressed(url.path)

internal val defaultEncoding = "UTF-8"

fun DataFrame.Companion.readDelimStr(
    text: String, format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0
) = readDelim(StringReader(text), format, colTypes, skipLines)

fun DataFrame.Companion.read(
    fileOrUrl: String,
    delimeter: Char,
    skipLines: Int = 0,
    encoding: String = defaultEncoding,
    colTypes: Map<String, ColType> = mapOf(),
) = readCSV(fileOrUrl, defaultCsvFormat.withDelimiter(delimeter), colTypes, skipLines, encoding)

fun DataFrame.Companion.readCSV(
    fileOrUrl: String,
    format: CSVFormat = defaultCsvFormat,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    encoding: String = defaultEncoding
) = readDelim(asStream(fileOrUrl), format, isCompressed(fileOrUrl), colTypes, skipLines, encoding)

fun DataFrame.Companion.readCSV(
    file: File,
    format: CSVFormat = defaultCsvFormat,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    encoding: String = defaultEncoding
) = readDelim(FileInputStream(file), format, isCompressed(file), colTypes, skipLines, encoding)

fun DataFrame.Companion.readCSV(
    url: URL,
    format: CSVFormat = defaultCsvFormat,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    encoding: String = defaultEncoding
) = readDelim(url.openStream(), format, isCompressed(url), colTypes, skipLines, encoding)

fun DataFrame.Companion.readTSV(
    fileOrUrl: String,
    format: CSVFormat = defaultTdfFormat,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    encoding: String = defaultEncoding
) = readDelim(asStream(fileOrUrl), format, isCompressed(fileOrUrl), colTypes, skipLines, encoding)

fun DataFrame.Companion.readTSV(
    file: File,
    format: CSVFormat = defaultTdfFormat,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    encoding: String = defaultEncoding
) = readDelim(FileInputStream(file), format, isCompressed(file), colTypes, skipLines, encoding)

private fun asStream(fileOrUrl: String) = (if (isURL(fileOrUrl)) {
    URL(fileOrUrl).toURI()
} else {
    File(fileOrUrl).toURI()
}).toURL().openStream()

fun DataFrame.Companion.readDelim(
    inStream: InputStream,
    format: CSVFormat = defaultCsvFormat,
    isCompressed: Boolean = false,
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0,
    encoding: String = defaultEncoding
) =
    if (isCompressed) {
        InputStreamReader(GZIPInputStream(inStream))
    } else {
        BufferedReader(InputStreamReader(inStream, encoding))
    }.run {
        readDelim(this, format, colTypes, skipLines)
    }

internal fun isURL(fileOrUrl: String): Boolean = listOf("http:", "https:", "ftp:").any { fileOrUrl.startsWith(it) }

enum class ColType {
    Int,
    Long,
    Double,
    Boolean,
    BigDecimal,
    String,
}

fun ColType.toType() = when (this) {
    ColType.Int -> Int::class
    ColType.Long -> Long::class
    ColType.Double -> Double::class
    ColType.Boolean -> Boolean::class
    ColType.BigDecimal -> BigDecimal::class
    ColType.String -> String::class
}

val MISSING_VALUE = "NA"

fun DataFrame.Companion.readDelim(
    reader: Reader,
    format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
    colTypes: Map<String, ColType> = mapOf(),
    skipLines: Int = 0
): AnyFrame {

    val formatWithNullString = if (format.isNullStringSet) {
        format
    } else {
        format.withNullString(MISSING_VALUE)
    }

    var reader = reader
    if (skipLines > 0) {
        reader = BufferedReader(reader)
        repeat(skipLines) { reader.readLine() }
    }

    val csvParser = formatWithNullString.parse(reader)
    val records = csvParser.records

    val columnNames = csvParser.headerMap?.keys
        ?: (1..records[0].count()).map { index -> "X${index}" }

    val generator = ColumnNameGenerator()
    val uniqueNames = columnNames.map { generator.addUnique(it) }

    val cols = uniqueNames.mapIndexed { colIndex, colName ->
        val defaultColType = colTypes[".default"]
        val colType = colTypes[colName] ?: defaultColType
        var hasNulls = false
        val values = records.map { it[colIndex]?.emptyAsNull().also { if (it == null) hasNulls = true } }
        val column = column(colName, values, hasNulls)
        when (colType) {
            null -> column.tryParseAny()
            ColType.String -> column
            else -> {
                val parser = Parsers[colType.toType()]!!
                column.parse(parser)
            }
        }
    }

    return cols.asDataFrame<Unit>()
}

internal fun String.emptyAsNull(): String? = if (this.isEmpty()) null else this

fun AnyFrame.writeCSV(file: File, format: CSVFormat = CSVFormat.DEFAULT.withHeader()) =
    writeCSV(FileWriter(file), format)

fun AnyFrame.writeCSV(path: String, format: CSVFormat = CSVFormat.DEFAULT.withHeader()) =
    writeCSV(FileWriter(path), format)

fun AnyFrame.writeCSV(writer: Appendable, format: CSVFormat = CSVFormat.DEFAULT.withHeader()) {

    val printer = format.print(writer)
    try {
        printer.printRecord(columnNames())
        forEach {
            printer.printRecord(it.values)
        }
    } finally {
        printer.close()
    }
}