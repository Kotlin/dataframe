package krangl.typed

import org.apache.commons.csv.CSVFormat
import java.io.*
import java.math.BigDecimal
import java.net.URL
import java.util.zip.GZIPInputStream
import kotlin.reflect.KClass

internal val defaultCsvFormat = CSVFormat.DEFAULT.withHeader().withIgnoreSurroundingSpaces()

internal val defaultTdfFormat = CSVFormat.TDF.withHeader().withIgnoreSurroundingSpaces()

internal fun isCompressed(fileOrUrl: String) = listOf("gz", "zip").contains(fileOrUrl.split(".").last())

internal fun isCompressed(file: File) = listOf("gz", "zip").contains(file.extension)

fun readCSV(
        fileOrUrl: String,
        format: CSVFormat = defaultCsvFormat,
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        asStream(fileOrUrl),
        format = format,
        colTypes = colTypes,
        isCompressed = isCompressed(fileOrUrl))

fun readCSV(
        file: File,
        format: CSVFormat = defaultCsvFormat,
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        inStream = FileInputStream(file),
        format = format,
        colTypes = colTypes,
        isCompressed = isCompressed(file)
)

fun readTSV(
        fileOrUrl: String,
        format: CSVFormat = defaultTdfFormat,
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        inStream = asStream(fileOrUrl),
        format = format,
        colTypes = colTypes,
        isCompressed = isCompressed(fileOrUrl))

fun readTSV(
        file: File,
        format: CSVFormat = defaultTdfFormat,
        colTypes: Map<String, ColType> = mapOf()
) = readDelim(
        FileInputStream(file),
        format = format,
        colTypes = colTypes,
        isCompressed = isCompressed(file)
)

private fun asStream(fileOrUrl: String) = (if (isURL(fileOrUrl)) {
    URL(fileOrUrl).toURI()
} else {
    File(fileOrUrl).toURI()
}).toURL().openStream()

fun readDelim(
        inStream: InputStream,
        format: CSVFormat = defaultCsvFormat,
        isCompressed: Boolean = false,
        colTypes: Map<String, ColType> = mapOf()
) =
        if (isCompressed) {
            InputStreamReader(GZIPInputStream(inStream))
        } else {
            BufferedReader(InputStreamReader(inStream, "UTF-8"))
        }.run {
            readDelim(this, format, colTypes = colTypes)
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

fun ColType.toType() = when(this){
    ColType.Int -> Int::class
    ColType.Long -> Long::class
    ColType.Double -> Double::class
    ColType.Boolean -> Boolean::class
    ColType.BigDecimal -> BigDecimal::class
    ColType.String -> String::class
}

val MISSING_VALUE = "NA"

fun readDelim(
        reader: Reader,
        format: CSVFormat = CSVFormat.DEFAULT.withHeader(),
        colTypes: Map<String, ColType> = mapOf(),
        skip: Int = 0
): UntypedDataFrame {

    val formatWithNullString = if (format.isNullStringSet) {
        format
    } else {
        format.withNullString(MISSING_VALUE)
    }

    var reader = reader
    if (skip > 0) {
        reader = BufferedReader(reader)
        repeat(skip) { reader.readLine() }
    }

    val csvParser = formatWithNullString.parse(reader)
    val records = csvParser.records

    val columnNames = csvParser.headerMap?.keys
            ?: (1..records[0].count()).map { index -> "X${index}" }

    val generator = ColumnNameGenerator()
    val uniqueNames = columnNames.map { generator.createUniqueName(it) }

    val cols = uniqueNames.mapIndexed { colIndex, colName ->
        val defaultColType = colTypes[".default"]
        val colType = colTypes[colName] ?: defaultColType
        var hasNulls = false
        val values = records.map { it[colIndex]?.emptyAsNull().also { if (it == null) hasNulls = true } }
        val column = column(colName, values, hasNulls)
        when(colType) {
            null -> column.tryParseAny()
            ColType.String -> column
            else -> {
                val parser = parsersMap[colType.toType()]!!
                column.parse(parser)
            }
        }
    }

    return cols.asDataFrame()
}

internal fun String.emptyAsNull(): String? = if (this.isEmpty()) null else this

internal fun String.toBooleanOrNull() =
        when (toUpperCase()) {
            "T" -> true
            "TRUE" -> true
            "YES" -> true
            "F" -> false
            "FALSE" -> false
            "NO" -> false
            else -> null
        }

class StringParser<T : Any>(val type: KClass<T>, val parse: (String) -> T?)

inline fun <reified T : Any> stringParser(noinline body: (String) -> T?) = StringParser(T::class, body)

val allParsers = listOf(
        stringParser { it.toIntOrNull() },
        stringParser { it.toLongOrNull() },
        stringParser { it.toDoubleOrNull() },
        stringParser { it.toBooleanOrNull() },
        stringParser { it.toBigDecimalOrNull() }
)

val parsersMap = allParsers.associateBy { it.type }

inline fun <reified T : Any> getParser() = parsersMap[T::class] as? StringParser<T>

inline fun <reified T : Any> TypedColData<String?>.parse(): TypedColData<T?> {
    val parser = getParser<T>() ?: throw Exception("Couldn't find parser for type ${T::class}")
    return parse(parser)
}

fun <T : Any> TypedColData<String?>.parse(parser: StringParser<T>): TypedColData<T?> {
    val parsedValues = values.map {
        it?.let {
            parser.parse(it) ?: throw Exception("Couldn't parse '${it}' to type ${parser.type}")
        }
    }
    return column(name, parsedValues, nullable, parser.type) as TypedColData<T?>
}

fun TypedColData<String?>.tryParseAny(): TypedColData<*> {
    var parserId = 0
    val parsedValues = mutableListOf<Any?>()

    do {
        val parser = allParsers[parserId]
        parsedValues.clear()
        for (str in values) {
            if (str == null) parsedValues.add(null)
            else {
                val res = parser.parse(str)
                if (res == null) {
                    parserId++
                    break
                }
                parsedValues.add(res)
            }
        }
    } while (parserId < allParsers.size && parsedValues.size != values.size)
    if (parserId == allParsers.size) return this
    return column(name, parsedValues, nullable, allParsers[parserId].type)
}

