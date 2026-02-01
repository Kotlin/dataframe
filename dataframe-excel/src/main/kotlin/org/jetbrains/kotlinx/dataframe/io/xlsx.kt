package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellReference
import org.apache.poi.util.DefaultTempFileCreationStrategy
import org.apache.poi.util.LocaleUtil
import org.apache.poi.util.LocaleUtil.getUserTimeZone
import org.apache.poi.util.TempFile
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.util.DF_READ_EXCEL
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.Calendar
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import java.time.LocalDate as JavaLocalDate
import java.time.LocalDateTime as JavaLocalDateTime
import java.util.Date as JavaDate

public class Excel : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readExcel(stream)

    override fun readDataFrame(path: Path, header: List<String>): AnyFrame = DataFrame.readExcel(path)

    override fun acceptsExtension(ext: String): Boolean = ext == "xls" || ext == "xlsx"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 40000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadExcelMethod(pathRepresentation)
}

public class ExcelSchemaReader : SchemaReader {
    override fun accepts(path: String, qualifier: String): Boolean =
        super.accepts(path, qualifier) && path.endsWith(".xlsx") || path.endsWith(".xls")

    override fun read(path: String): DataFrame<*> = DataFrame.readExcel(path)
}

private const val MESSAGE_REMOVE_1_1 = "Will be removed in 1.1."
internal const val READ_EXCEL_OLD = "This function is only here for binary compatibility. $MESSAGE_REMOVE_1_1"

internal class DefaultReadExcelMethod(path: String?) :
    AbstractDefaultReadMethod(path, MethodArguments.EMPTY, READ_EXCEL)

private const val READ_EXCEL = "readExcel"
private const val READ_EXCEL_TEMP_FOLDER_PREFIX = "dataframe-excel"

/**
 * To prevent [Issue #402](https://github.com/Kotlin/dataframe/issues/402):
 *
 * Creates new temp directory instead of the default `/tmp/poifiles` which would
 * cause permission issues for multiple users.
 */
private fun setWorkbookTempDirectory() {
    val tempDir = try {
        Files.createTempDirectory(READ_EXCEL_TEMP_FOLDER_PREFIX)
            .toFile()
            .also { it.deleteOnExit() }
    } catch (e: Exception) {
        // Ignore, let WorkbookFactory use the default temp directory instead
        return
    }
    TempFile.setTempFileCreationStrategy(
        DefaultTempFileCreationStrategy(tempDir),
    )
}

@Deprecated(message = READ_EXCEL_OLD, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    url: URL,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
): AnyFrame =
    readExcel(url, sheetName, skipRows, columns, stringColumns, rowsCount, nameRepairStrategy, firstRowIsHeader)

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param stringColumns range of columns to read as String regardless of a cell type.
 * For example, by default numeric cell with value "3" will be parsed as Double with value being 3.0. With this option, it will be simply "3"
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE].
 * @param firstRowIsHeader when set to true, it will take the first row (after skipRows) as the header.
 * when set to false, it operates as [NameRepairStrategy.MAKE_UNIQUE],
 * ensuring unique column names will make the columns be named according to excel columns, like "A", "B", "C" etc.
 * for unstructured data.
 * @param parseEmptyAsNull when set to true, empty strings in cells are parsed as null (default true).
 * These cells are ignored when inferring the column’s type.
 */
public fun DataFrame.Companion.readExcel(
    url: URL,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
    parseEmptyAsNull: Boolean = true,
): AnyFrame {
    setWorkbookTempDirectory()
    val wb = WorkbookFactory.create(url.openStream())
    return wb.use {
        readExcel(
            wb,
            sheetName,
            skipRows,
            columns,
            stringColumns?.toFormattingOptions(),
            rowsCount,
            nameRepairStrategy,
            firstRowIsHeader,
            parseEmptyAsNull,
        )
    }
}

@Deprecated(message = READ_EXCEL_OLD, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    file: File,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
): AnyFrame =
    readExcel(file, sheetName, skipRows, columns, stringColumns, rowsCount, nameRepairStrategy, firstRowIsHeader)

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param stringColumns range of columns to read as String regardless of a cell type.
 * For example, by default numeric cell with value "3" will be parsed as Double with value being 3.0. With this option, it will be simply "3"
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE].
 * @param firstRowIsHeader when set to true, it will take the first row (after skipRows) as the header.
 * when set to false, it operates as [NameRepairStrategy.MAKE_UNIQUE],
 * ensuring unique column names will make the columns be named according to excel columns, like "A", "B", "C" etc.
 * for unstructured data.
 * @param parseEmptyAsNull when set to true, empty strings in cells are parsed as null (default true).
 * These cells are ignored when inferring the column’s type.
 */
public fun DataFrame.Companion.readExcel(
    file: File,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
    parseEmptyAsNull: Boolean = true,
): AnyFrame =
    readExcel(
        file.toPath(),
        sheetName,
        skipRows,
        columns,
        stringColumns,
        rowsCount,
        nameRepairStrategy,
        firstRowIsHeader,
        parseEmptyAsNull,
    )

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param stringColumns range of columns to read as String regardless of a cell type.
 * For example, by default numeric cell with value "3" will be parsed as Double with value being 3.0. With this option, it will be simply "3"
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE].
 * @param firstRowIsHeader when set to true, it will take the first row (after skipRows) as the header.
 * when set to false, it operates as [NameRepairStrategy.MAKE_UNIQUE],
 * ensuring unique column names will make the columns be named according to excel columns, like "A", "B", "C" etc.
 * for unstructured data.
 * @param parseEmptyAsNull when set to true, empty strings in cells are parsed as null (default true).
 * These cells are ignored when inferring the column’s type.
 */
public fun DataFrame.Companion.readExcel(
    path: Path,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
    parseEmptyAsNull: Boolean = true,
): AnyFrame {
    path.inputStream().use { inputStream ->
        setWorkbookTempDirectory()
        @Suppress("ktlint:standard:comment-wrapping")
        val wb = WorkbookFactory.create(inputStream, /* password = */ null)
        return wb.use {
            readExcel(
                it,
                sheetName,
                skipRows,
                columns,
                stringColumns?.toFormattingOptions(),
                rowsCount,
                nameRepairStrategy,
                firstRowIsHeader,
                parseEmptyAsNull,
            )
        }
    }
}

@Deprecated(message = READ_EXCEL_OLD, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    fileOrUrl: String,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
): AnyFrame =
    readExcel(fileOrUrl, sheetName, skipRows, columns, stringColumns, rowsCount, nameRepairStrategy, firstRowIsHeader)

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param stringColumns range of columns to read as String regardless of a cell type.
 * For example, by default numeric cell with value "3" will be parsed as Double with value being 3.0. With this option, it will be simply "3"
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE].
 * @param firstRowIsHeader when set to true, it will take the first row (after skipRows) as the header.
 * when set to false, it operates as [NameRepairStrategy.MAKE_UNIQUE],
 * ensuring unique column names will make the columns be named according to excel columns, like "A", "B", "C" etc.
 * for unstructured data.
 * @param parseEmptyAsNull when set to true, empty strings in cells are parsed as null (default true).
 * These cells are ignored when inferring the column’s type.
 */
public fun DataFrame.Companion.readExcel(
    fileOrUrl: String,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
    parseEmptyAsNull: Boolean = true,
): AnyFrame =
    readExcel(
        asUrl(fileOrUrl),
        sheetName,
        skipRows,
        columns,
        stringColumns,
        rowsCount,
        nameRepairStrategy,
        firstRowIsHeader,
        parseEmptyAsNull,
    )

@Deprecated(message = READ_EXCEL_OLD, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    inputStream: InputStream,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
): AnyFrame =
    readExcel(inputStream, sheetName, skipRows, columns, stringColumns, rowsCount, nameRepairStrategy, firstRowIsHeader)

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param stringColumns range of columns to read as String regardless of a cell type.
 * For example, by default numeric cell with value "3" will be parsed as Double with value being 3.0. With this option, it will be simply "3"
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE].
 * @param firstRowIsHeader when set to true, it will take the first row (after skipRows) as the header.
 * when set to false, it operates as [NameRepairStrategy.MAKE_UNIQUE],
 * ensuring unique column names will make the columns be named according to excel columns, like "A", "B", "C" etc.
 * for unstructured data.
 * @param parseEmptyAsNull when set to true, empty strings in cells are parsed as null (default true).
 * These cells are ignored when inferring the column’s type.
 */
public fun DataFrame.Companion.readExcel(
    inputStream: InputStream,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
    parseEmptyAsNull: Boolean = true,
): AnyFrame {
    setWorkbookTempDirectory()
    val wb = WorkbookFactory.create(inputStream)
    return wb.use {
        readExcel(
            it,
            sheetName,
            skipRows,
            columns,
            stringColumns?.toFormattingOptions(),
            rowsCount,
            nameRepairStrategy,
            firstRowIsHeader,
            parseEmptyAsNull,
        )
    }
}

@Deprecated(message = READ_EXCEL_OLD, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    wb: Workbook,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    formattingOptions: FormattingOptions? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
): AnyFrame =
    readExcel(wb, sheetName, skipRows, columns, formattingOptions, rowsCount, nameRepairStrategy, firstRowIsHeader)

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param formattingOptions range of columns to read as String regardless of a cell type.
 * For example, by default numeric cell with value "3" will be parsed as Double with value being 3.0. With this option, it will be simply "3"
 * See also [FormattingOptions.formatter] and [DataFormatter.formatCellValue].
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE].
 * @param firstRowIsHeader when set to true, it will take the first row (after skipRows) as the header.
 * when set to false, it operates as [NameRepairStrategy.MAKE_UNIQUE],
 * ensuring unique column names will make the columns be named according to excel columns, like "A", "B", "C" etc.
 * for unstructured data.
 * @param parseEmptyAsNull when set to true, empty strings in cells are parsed as null (default true).
 * These cells are ignored when inferring the column’s type.
 */
public fun DataFrame.Companion.readExcel(
    wb: Workbook,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    formattingOptions: FormattingOptions? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
    parseEmptyAsNull: Boolean = true,
): AnyFrame {
    val sheet: Sheet = sheetName
        ?.let { wb.getSheet(it) ?: error("Sheet with name $sheetName not found") }
        ?: wb.getSheetAt(0)
    return readExcel(
        sheet,
        columns,
        formattingOptions,
        skipRows,
        rowsCount,
        nameRepairStrategy,
        firstRowIsHeader,
        parseEmptyAsNull,
    )
}

/**
 * @param range comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 */
@JvmInline
public value class StringColumns(public val range: String)

public fun StringColumns.toFormattingOptions(formatter: DataFormatter = DataFormatter()): FormattingOptions =
    FormattingOptions(range, formatter)

/**
 * @param range comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param formatter
 */
public class FormattingOptions(range: String, public val formatter: DataFormatter = DataFormatter()) {
    public val columnIndices: Set<Int> = getColumnIndices(range).toSet()
}

/**
 * @param sheet sheet to read.
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param formattingOptions range of columns to read as String regardless of a cell's type.
 * For example, by default numeric cell with value "3" will be parsed as Double with value being 3.0. With this option, it will be simply "3"
 * See also [FormattingOptions.formatter] and [DataFormatter.formatCellValue].
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE].
 * @param firstRowIsHeader when set to true, it will take the first row (after skipRows) as the header.
 * when set to false, it operates as [NameRepairStrategy.MAKE_UNIQUE],
 * ensuring unique column names will make the columns be named according to excel columns, like "A", "B", "C" etc.
 * for unstructured data.
 */
public fun DataFrame.Companion.readExcel(
    sheet: Sheet,
    columns: String? = null,
    formattingOptions: FormattingOptions? = null,
    skipRows: Int = 0,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
    firstRowIsHeader: Boolean = true,
    parseEmptyAsNull: Boolean = true,
): AnyFrame {
    val columnIndexes: Iterable<Int> = when {
        columns != null -> getColumnIndices(columns)

        firstRowIsHeader -> {
            val headerRow = checkNotNull(sheet.getRow(skipRows)) {
                "Row number ${skipRows + 1} (1-based index) is not defined on the sheet ${sheet.sheetName}"
            }
            val firstCellNum = headerRow.firstCellNum
            check(firstCellNum != (-1).toShort()) {
                "There are no defined cells on header row number ${skipRows + 1} (1-based index). Pass `columns` argument to specify what columns to read or make sure the index is correct"
            }
            headerRow.firstCellNum until headerRow.lastCellNum
        }

        else -> {
            val largestRow = sheet.rowIterator().asSequence().maxByOrNull { it.lastCellNum }
            checkNotNull(largestRow) {
                "There are no defined cells"
            }
            largestRow.firstCellNum until largestRow.lastCellNum
        }
    }

    val headerRow: Row? = if (firstRowIsHeader) {
        sheet.getRow(skipRows)
    } else {
        sheet.shiftRows(0, sheet.lastRowNum, 1)
        sheet.createRow(0)
    }

    val first = skipRows + 1
    val last = rowsCount?.let { first + it - 1 } ?: sheet.lastRowNum
    val valueRowsRange = (first..last)

    val columnNameCounters = mutableMapOf<String, Int>()
    val columns = columnIndexes.map { index ->
        val headerCell = headerRow?.getCell(index)
        val nameFromCell = if (headerCell?.cellType == CellType.NUMERIC) {
            headerCell.numericCellValue.toString() // Support numeric-named columns
        } else {
            headerCell?.stringCellValue
                ?: CellReference.convertNumToColString(index) // Use Excel column names if no data
        }

        val name = repairNameIfRequired(
            nameFromCell,
            columnNameCounters,
            if (firstRowIsHeader) nameRepairStrategy else NameRepairStrategy.MAKE_UNIQUE,
        )
        columnNameCounters[nameFromCell] =
            columnNameCounters.getOrDefault(nameFromCell, 0) + 1 // increase the counter for specific column name
        val getCellValue: (Cell?) -> Any? = { cell ->
            if (cell == null) {
                null
            } else {
                val rawValue: Any? = if (formattingOptions != null && index in formattingOptions.columnIndices) {
                    formattingOptions.formatter.formatCellValue(cell)
                } else {
                    cell.cellValue(sheet.sheetName)
                }
                if (parseEmptyAsNull && rawValue is String && rawValue.isEmpty()) {
                    null
                } else {
                    rawValue
                }
            }
        }
        val values: List<Any?> = valueRowsRange.map {
            val row: Row? = sheet.getRow(it)
            val cell: Cell? = row?.getCell(index)
            getCellValue(cell)
        }
        DataColumn.createByInference(name, values)
    }
    return dataFrameOf(columns)
}

private fun getColumnIndices(columns: String): List<Int> =
    columns.split(",").flatMap {
        if (it.contains(":")) {
            val (start, end) = it.split(":").map { CellReference.convertColStringToIndex(it) }
            start..end
        } else {
            listOf(CellReference.convertColStringToIndex(it))
        }
    }

/**
 * This is a universal function for name repairing
 * and should be moved to the API module later,
 * when the functionality will be enabled for all IO sources.
 *
 * TODO: https://github.com/Kotlin/dataframe/issues/387
 */
private fun repairNameIfRequired(
    nameFromCell: String,
    columnNameCounters: MutableMap<String, Int>,
    nameRepairStrategy: NameRepairStrategy,
): String =
    when (nameRepairStrategy) {
        NameRepairStrategy.DO_NOTHING -> nameFromCell

        NameRepairStrategy.CHECK_UNIQUE ->
            if (columnNameCounters.contains(nameFromCell)) {
                throw DuplicateColumnNamesException(
                    columnNameCounters.keys.toList(),
                )
            } else {
                nameFromCell
            }

        // probably it's never empty because of filling empty column names earlier
        NameRepairStrategy.MAKE_UNIQUE ->
            if (nameFromCell.isEmpty()) {
                val emptyName = "Unknown column"
                if (columnNameCounters.contains(emptyName)) {
                    "${emptyName}${columnNameCounters[emptyName]}"
                } else {
                    emptyName
                }
            } else {
                if (columnNameCounters.contains(nameFromCell)) {
                    "${nameFromCell}${columnNameCounters[nameFromCell]}"
                } else {
                    nameFromCell
                }
            }
    }

private fun Cell?.cellValue(sheetName: String): Any? {
    if (this == null) return null

    fun getValueFromType(type: CellType?): Any? =
        when (type) {
            CellType._NONE -> error(
                "Cell $address of sheet $sheetName has a CellType that should only be used internally. This is a bug, please report https://github.com/Kotlin/dataframe/issues",
            )

            CellType.NUMERIC -> {
                val number = numericCellValue
                when {
                    DateUtil.isCellDateFormatted(this) -> DateUtil.getLocalDateTime(number).toKotlinLocalDateTime()
                    else -> number
                }
            }

            CellType.STRING -> stringCellValue

            CellType.FORMULA -> getValueFromType(cachedFormulaResultType)

            CellType.BLANK -> stringCellValue

            CellType.BOOLEAN -> booleanCellValue

            CellType.ERROR -> errorCellValue

            null -> null
        }
    return getValueFromType(cellType)
}

public enum class WorkBookType {
    XLS,
    XLSX,
}

/**
 * Writes this DataFrame to an Excel file as a single sheet.
 *
 * Implemented with [Apache POI](https://poi.apache.org) using `HSSFWorkbook` for XLS files,
 * `XSSFWorkbook` for standard XLSX files, and `SXSSFWorkbook` for memory-efficient streaming when creating new XLSX files.
 *
 * @param path The path to the file where the data will be written.
 * @param columnsSelector A [selector][ColumnsSelector] to determine which columns to include in the file. The default is all columns.
 * @param sheetName The name of the sheet in the Excel file. If null, the default name will be used.
 * @param writeHeader A flag indicating whether to write the header row in the Excel file. Defaults to true.
 * @param workBookType The [type of workbook][WorkBookType] to create (e.g., XLS or XLSX). Defaults to XLSX.
 * @param keepFile If `true` and the file already exists, a new sheet will be appended instead of overwriting the file.
 * This may result in higher memory usage and slower performance compared to creating a new file.
 * Defaults to `false`.
 *
 * @throws [IllegalArgumentException] if the [sheetName] is invalid or workbook already contains a sheet with this name.
 */
public fun <T> DataFrame<T>.writeExcel(
    path: String,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true,
    workBookType: WorkBookType = WorkBookType.XLSX,
    keepFile: Boolean = false,
): Unit = writeExcel(File(path), columnsSelector, sheetName, writeHeader, workBookType, keepFile)

/**
 * Writes this DataFrame to an Excel file as a single sheet.
 *
 * Implemented with [Apache POI](https://poi.apache.org) using `HSSFWorkbook` for XLS files,
 * `XSSFWorkbook` for standard XLSX files,
 * and `SXSSFWorkbook` for memory-efficient streaming when creating new XLSX files.
 *
 * @param file The file where the data will be written.
 * @param columnsSelector A [selector][ColumnsSelector] to determine which columns to include in the file. The default is all columns.
 * @param sheetName The name of the sheet in the Excel file. If null, the default name will be used.
 * @param writeHeader A flag indicating whether to write the header row in the Excel file. Defaults to true.
 * @param workBookType The [type of workbook][WorkBookType] to create (e.g., XLS or XLSX). Defaults to XLSX.
 * @param keepFile If `true` and the file already exists, a new sheet will be appended instead of overwriting the file.
 * This may result in higher memory usage and slower performance compared to creating a new file.
 * Defaults to `false`.
 *
 * @throws [IllegalArgumentException] if the [sheetName] is invalid or workbook already contains a sheet with this name.
 */
public fun <T> DataFrame<T>.writeExcel(
    file: File,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true,
    workBookType: WorkBookType = WorkBookType.XLSX,
    keepFile: Boolean = false,
): Unit =
    writeExcel(
        path = file.toPath(),
        columnsSelector = columnsSelector,
        sheetName = sheetName,
        writeHeader = writeHeader,
        workBookType = workBookType,
        keepFile = keepFile,
    )

/**
 * Writes this DataFrame to an Excel file as a single sheet.
 *
 * Implemented with [Apache POI](https://poi.apache.org) using `HSSFWorkbook` for XLS files,
 * `XSSFWorkbook` for standard XLSX files,
 * and `SXSSFWorkbook` for memory-efficient streaming when creating new XLSX files.
 *
 * @param path The path to a file where the data will be written.
 * @param columnsSelector A [selector][ColumnsSelector] to determine which columns to include in the file. The default is all columns.
 * @param sheetName The name of the sheet in the Excel file. If null, the default name will be used.
 * @param writeHeader A flag indicating whether to write the header row in the Excel file. Defaults to true.
 * @param workBookType The [type of workbook][WorkBookType] to create (e.g., XLS or XLSX). Defaults to XLSX.
 * @param keepFile If `true` and the file already exists, a new sheet will be appended instead of overwriting the file.
 * This may result in higher memory usage and slower performance compared to creating a new file.
 * Defaults to `false`.
 *
 * @throws [IllegalArgumentException] if the [sheetName] is invalid or workbook already contains a sheet with this name.
 */
public fun <T> DataFrame<T>.writeExcel(
    path: Path,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true,
    workBookType: WorkBookType = WorkBookType.XLSX,
    keepFile: Boolean = false,
) {
    val factory =
        if (keepFile && path.exists() && path.fileSize() > 0L) {
            val fis = path.inputStream()
            when (workBookType) {
                WorkBookType.XLS -> HSSFWorkbook(fis)
                WorkBookType.XLSX -> XSSFWorkbook(fis)
            }
        } else {
            when (workBookType) {
                WorkBookType.XLS -> HSSFWorkbook()
                WorkBookType.XLSX -> SXSSFWorkbook()
            }
        }
    return path.outputStream().use {
        writeExcel(it, columnsSelector, sheetName, writeHeader, factory)
    }
}

/**
 * Writes this DataFrame to an Excel file using an existing [Workbook] instance into given [OutputStream].
 *
 * Uses [Apache POI](https://poi.apache.org).
 * Supports [XSSFWorkbook] and [SXSSFWorkbook] for XLSX and [HSSFWorkbook] for XLS,
 * and allows users to manage the workbook externally.
 *
 * @param outputStream The output stream where the Excel data will be written.
 * @param columnsSelector A [selector][ColumnsSelector] to determine which columns to include in the file. The default is all columns.
 * @param sheetName The name of the sheet in the Excel file. If null, the default name will be used.
 * @param writeHeader A flag indicating whether to write the header row in the Excel file. Defaults to true.
 * @param factory The [Workbook] instance, allowing integration with an existing workbook.
 *
 * @throws [IllegalArgumentException] if the [sheetName] is invalid or workbook already contains a sheet with this name.
 */
public fun <T> DataFrame<T>.writeExcel(
    outputStream: OutputStream,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true,
    factory: Workbook,
) {
    val wb: Workbook = factory
    writeExcel(wb, columnsSelector, sheetName, writeHeader)
    wb.write(outputStream)
    wb.close()
}

/**
 * Creates a new [Sheet] in the given [Workbook] and writes this DataFrame content into it.
 *
 * Uses [Apache POI](https://poi.apache.org).
 * Supports [XSSFWorkbook] and [SXSSFWorkbook] for XLSX and [HSSFWorkbook] for XLS,
 * and allows users to manage the workbook externally.
 *
 * Automatically handles datetime types.
 * Skips null values to prevent Apache POI from treating empty cells incorrectly.
 *
 * @param wb The [Workbook] where the sheet will be created.
 * @param columnsSelector A [selector][ColumnsSelector] to determine which columns to include. Defaults to all columns.
 * @param sheetName The name of the sheet. If null, a default sheet name is used.
 * @param writeHeader Whether to include a header row with column names. Defaults to true.
 *
 * @return The created [Sheet] instance containing the DataFrame data.
 *
 * @throws [IllegalArgumentException] if the [sheetName] is invalid or workbook already contains a sheet with this name.
 */
public fun <T> DataFrame<T>.writeExcel(
    wb: Workbook,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true,
): Sheet {
    val sheet = if (sheetName != null) {
        wb.createSheet(sheetName)
    } else {
        wb.createSheet()
    }

    var i = 0
    val columns = select(columnsSelector)

    if (writeHeader) {
        val row = sheet.createRow(i)
        columns.columnNames().forEachIndexed { index, s ->
            val cell = row.createCell(index)
            cell.setCellValue(s)
        }
        i++
    }

    val createHelper = wb.creationHelper
    val cellStyleDate = wb.createCellStyle()
    val cellStyleDateTime = wb.createCellStyle()
    val cellStyleTime = wb.createCellStyle()
    cellStyleDate.dataFormat = createHelper.createDataFormat().getFormat("dd.mm.yyyy")
    cellStyleDateTime.dataFormat = createHelper.createDataFormat().getFormat("dd.mm.yyyy hh:mm:ss")
    cellStyleTime.dataFormat = createHelper.createDataFormat().getFormat("hh:mm:ss")

    columns.forEach {
        val row = sheet.createRow(i)
        it.values().forEachIndexed { index, any ->
            // In file created by LibreOffice Calc (empty_cell.xls)
            // empty cell B2 is treated by Apache POI as null (See XlsxTest.empty cell is null)
            // so here we don't create cell for null value
            if (any != null) {
                val cell = row.createCell(index)
                cell.setCellValueByGuessedType(any)

                when (any) {
                    is JavaLocalDate, is LocalDate -> {
                        cell.cellStyle = cellStyleDate
                    }

                    is Calendar, is JavaDate -> {
                        cell.cellStyle = cellStyleDateTime
                    }

                    is JavaLocalDateTime -> {
                        if (any.year < 1900) {
                            cell.cellStyle = cellStyleTime
                        } else {
                            cell.cellStyle = cellStyleDateTime
                        }
                    }

                    is LocalDateTime -> {
                        if (any.year < 1900) {
                            cell.cellStyle = cellStyleTime
                        } else {
                            cell.cellStyle = cellStyleDateTime
                        }
                    }

                    else -> {}
                }
            }
        }
        i++
    }
    return sheet
}

private fun Cell.setCellValueByGuessedType(any: Any) =
    when (any) {
        is AnyRow -> this.setCellValue(
            try {
                any.toJson()
            } catch (_: NoClassDefFoundError) {
                error(
                    "Encountered a DataRow value when writing to an Excel cell. It must be serialized to JSON, requiring the 'dataframe-json' dependency.",
                )
            },
        )

        is AnyFrame -> this.setCellValue(
            try {
                any.toJson()
            } catch (_: NoClassDefFoundError) {
                error(
                    "Encountered a DataFrame value when writing to an Excel cell. It must be serialized to JSON, requiring the 'dataframe-json' dependency.",
                )
            },
        )

        is Number -> this.setCellValue(any.toDouble())

        is JavaLocalDate -> this.setCellValue(any)

        is JavaLocalDateTime -> this.setTime(any)

        is Boolean -> this.setCellValue(any)

        is Calendar -> this.setDate(any.time)

        is JavaDate -> this.setDate(any)

        is RichTextString -> this.setCellValue(any)

        is String -> this.setCellValue(any)

        is LocalDate -> this.setCellValue(any.toJavaLocalDate())

        is LocalDateTime -> this.setTime(any.toJavaLocalDateTime())

        // Another option would be to serialize everything else to string,
        // but people can convert columns to string with any serialization framework they want
        // so i think toString should do until more use cases arise.
        else -> this.setCellValue(any.toString())
    }

/**
 * Set LocalDateTime value correctly also if date have zero value in Excel.
 * Zero dates are usually used for storing a time component only,
 * are displayed as 00.01.1900 in Excel and as 30.12.1899 in LibreOffice Calc and also in POI.
 * POI can not set 1899 year directly.
 */
private fun Cell.setTime(localDateTime: JavaLocalDateTime) {
    this.setCellValue(DateUtil.getExcelDate(localDateTime.plusDays(1)) - 1.0)
}

/**
 * Set Date value correctly also if date has zero value in Excel.
 * Zero dates are usually used for storing a time component only,
 * are displayed as 00.01.1900 in Excel and as 30.12.1899 in LibreOffice Calc and also in POI.
 * POI can not set 1899 year directly.
 */
private fun Cell.setDate(date: JavaDate) {
    val calStart = LocaleUtil.getLocaleCalendar()
    calStart.time = date
    this.setTime(calStart.toInstant().atZone(getUserTimeZone().toZoneId()).toLocalDateTime())
}

// region deprecated

/** For binary compatibility */
@Deprecated(DF_READ_EXCEL, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    url: URL,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame =
    readExcel(
        url = url,
        sheetName = sheetName,
        skipRows = skipRows,
        columns = columns,
        stringColumns = stringColumns,
        rowsCount = rowsCount,
        nameRepairStrategy = nameRepairStrategy,
        firstRowIsHeader = true,
    )

/** For binary compatibility */
@Deprecated(DF_READ_EXCEL, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    file: File,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame =
    readExcel(
        file = file,
        sheetName = sheetName,
        skipRows = skipRows,
        columns = columns,
        stringColumns = stringColumns,
        rowsCount = rowsCount,
        nameRepairStrategy = nameRepairStrategy,
        firstRowIsHeader = true,
    )

/** For binary compatibility */
@Deprecated(DF_READ_EXCEL, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    fileOrUrl: String,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame =
    readExcel(
        fileOrUrl = fileOrUrl,
        sheetName = sheetName,
        skipRows = skipRows,
        columns = columns,
        stringColumns = stringColumns,
        rowsCount = rowsCount,
        nameRepairStrategy = nameRepairStrategy,
        firstRowIsHeader = true,
    )

/** For binary compatibility */
@Deprecated(DF_READ_EXCEL, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    inputStream: InputStream,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    stringColumns: StringColumns? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame =
    readExcel(
        inputStream = inputStream,
        sheetName = sheetName,
        skipRows = skipRows,
        columns = columns,
        stringColumns = stringColumns,
        rowsCount = rowsCount,
        nameRepairStrategy = nameRepairStrategy,
        firstRowIsHeader = true,
    )

/** For binary compatibility */
@Deprecated(DF_READ_EXCEL, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    wb: Workbook,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    formattingOptions: FormattingOptions? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame =
    readExcel(
        wb = wb,
        sheetName = sheetName,
        skipRows = skipRows,
        columns = columns,
        formattingOptions = formattingOptions,
        rowsCount = rowsCount,
        nameRepairStrategy = nameRepairStrategy,
        firstRowIsHeader = true,
    )

/** For binary compatibility */
@Deprecated(DF_READ_EXCEL, level = DeprecationLevel.HIDDEN)
public fun DataFrame.Companion.readExcel(
    sheet: Sheet,
    columns: String? = null,
    formattingOptions: FormattingOptions? = null,
    skipRows: Int = 0,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame =
    readExcel(
        sheet = sheet,
        columns = columns,
        formattingOptions = formattingOptions,
        skipRows = skipRows,
        rowsCount = rowsCount,
        nameRepairStrategy = nameRepairStrategy,
        firstRowIsHeader = true,
    )

// endregion
