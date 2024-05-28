package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
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
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.nio.file.Files
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

public class Excel : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readExcel(stream)

    override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readExcel(file)

    override fun acceptsExtension(ext: String): Boolean = ext == "xls" || ext == "xlsx"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 40000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadExcelMethod(pathRepresentation)
}

internal class DefaultReadExcelMethod(path: String?) : AbstractDefaultReadMethod(path, MethodArguments.EMPTY, readExcel)

private const val readExcel = "readExcel"
private const val readExcelTempFolderPrefix = "dataframe-excel"

/**
 * To prevent [Issue #402](https://github.com/Kotlin/dataframe/issues/402):
 *
 * Creates new temp directory instead of the default `/tmp/poifiles` which would
 * cause permission issues for multiple users.
 */
private fun setWorkbookTempDirectory() {
    val tempDir = try {
        Files.createTempDirectory(readExcelTempFolderPrefix)
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

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE]
 */
public fun DataFrame.Companion.readExcel(
    url: URL,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame {
    setWorkbookTempDirectory()
    val wb = WorkbookFactory.create(url.openStream())
    return wb.use { readExcel(wb, sheetName, skipRows, columns, rowsCount, nameRepairStrategy) }
}

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE]
 */
public fun DataFrame.Companion.readExcel(
    file: File,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame {
    setWorkbookTempDirectory()
    val wb = WorkbookFactory.create(file)
    return wb.use { readExcel(it, sheetName, skipRows, columns, rowsCount, nameRepairStrategy) }
}

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE]
 */
public fun DataFrame.Companion.readExcel(
    fileOrUrl: String,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame = readExcel(asURL(fileOrUrl), sheetName, skipRows, columns, rowsCount, nameRepairStrategy)

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE]
 */
public fun DataFrame.Companion.readExcel(
    inputStream: InputStream,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame {
    setWorkbookTempDirectory()
    val wb = WorkbookFactory.create(inputStream)
    return wb.use { readExcel(it, sheetName, skipRows, columns, rowsCount, nameRepairStrategy) }
}

/**
 * @param sheetName sheet to read. By default, the first sheet in the document
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE]
 */
public fun DataFrame.Companion.readExcel(
    wb: Workbook,
    sheetName: String? = null,
    skipRows: Int = 0,
    columns: String? = null,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame {
    val sheet: Sheet = sheetName
        ?.let { wb.getSheet(it) ?: error("Sheet with name $sheetName not found") }
        ?: wb.getSheetAt(0)
    return readExcel(sheet, columns, skipRows, rowsCount, nameRepairStrategy)
}

/**
 * @param sheet sheet to read.
 * @param columns comma separated list of Excel column letters and column ranges (e.g. “A:E” or “A,C,E:F”)
 * @param skipRows number of rows before header
 * @param rowsCount number of rows to read.
 * @param nameRepairStrategy handling of column names.
 * The default behavior is [NameRepairStrategy.CHECK_UNIQUE]
 */
public fun DataFrame.Companion.readExcel(
    sheet: Sheet,
    columns: String? = null,
    skipRows: Int = 0,
    rowsCount: Int? = null,
    nameRepairStrategy: NameRepairStrategy = NameRepairStrategy.CHECK_UNIQUE,
): AnyFrame {
    val columnIndexes: Iterable<Int> = if (columns != null) {
        columns.split(",").flatMap {
            if (it.contains(":")) {
                val (start, end) = it.split(":").map { CellReference.convertColStringToIndex(it) }
                start..end
            } else {
                listOf(CellReference.convertColStringToIndex(it))
            }
        }
    } else {
        val headerRow = checkNotNull(sheet.getRow(skipRows)) {
            "Row number ${skipRows + 1} (1-based index) is not defined on the sheet ${sheet.sheetName}"
        }
        val firstCellNum = headerRow.firstCellNum
        check(firstCellNum != (-1).toShort()) {
            "There are no defined cells on header row number ${skipRows + 1} (1-based index). Pass `columns` argument to specify what columns to read or make sure the index is correct"
        }
        headerRow.firstCellNum until headerRow.lastCellNum
    }

    val headerRow: Row? = sheet.getRow(skipRows)
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

        val name = repairNameIfRequired(nameFromCell, columnNameCounters, nameRepairStrategy)
        columnNameCounters[nameFromCell] =
            columnNameCounters.getOrDefault(nameFromCell, 0) + 1 // increase the counter for specific column name

        val values: List<Any?> = valueRowsRange.map {
            val row: Row? = sheet.getRow(it)
            val cell: Cell? = row?.getCell(index)
            cell.cellValue(sheet.sheetName)
        }
        DataColumn.createWithTypeInference(name, values)
    }
    return dataFrameOf(columns)
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
): String = when (nameRepairStrategy) {
    NameRepairStrategy.DO_NOTHING -> nameFromCell

    NameRepairStrategy.CHECK_UNIQUE -> if (columnNameCounters.contains(nameFromCell)) {
        throw DuplicateColumnNamesException(
            columnNameCounters.keys.toList(),
        )
    } else {
        nameFromCell
    }

    NameRepairStrategy.MAKE_UNIQUE -> if (nameFromCell.isEmpty()) { // probably it's never empty because of filling empty column names earlier
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
    fun getValueFromType(type: CellType?): Any? = when (type) {
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

public fun <T> DataFrame<T>.writeExcel(
    path: String,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true,
    workBookType: WorkBookType = WorkBookType.XLSX,
    keepFile: Boolean = false,
) = writeExcel(File(path), columnsSelector, sheetName, writeHeader, workBookType, keepFile)

public enum class WorkBookType {
    XLS,
    XLSX,
}

public fun <T> DataFrame<T>.writeExcel(
    file: File,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true,
    workBookType: WorkBookType = WorkBookType.XLSX,
    keepFile: Boolean = false,
) {
    val factory =
        if (keepFile) {
            when (workBookType) {
                WorkBookType.XLS -> HSSFWorkbook(file.inputStream())
                WorkBookType.XLSX -> XSSFWorkbook(file.inputStream())
            }
        } else {
            when (workBookType) {
                WorkBookType.XLS -> HSSFWorkbook()
                WorkBookType.XLSX -> XSSFWorkbook()
            }
        }
    return file.outputStream().use {
        writeExcel(it, columnsSelector, sheetName, writeHeader, factory)
    }
}

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
                    is LocalDate, is kotlinx.datetime.LocalDate -> {
                        cell.cellStyle = cellStyleDate
                    }

                    is Calendar, is Date -> {
                        cell.cellStyle = cellStyleDateTime
                    }

                    is LocalDateTime -> {
                        if (any.year < 1900) {
                            cell.cellStyle = cellStyleTime
                        } else {
                            cell.cellStyle = cellStyleDateTime
                        }
                    }

                    is kotlinx.datetime.LocalDateTime -> {
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

private fun Cell.setCellValueByGuessedType(any: Any) = when (any) {
    is AnyRow -> {
        this.setCellValue(any.toJson())
    }

    is AnyFrame -> {
        this.setCellValue(any.toJson())
    }

    is Number -> {
        this.setCellValue(any.toDouble())
    }

    is LocalDate -> {
        this.setCellValue(any)
    }

    is LocalDateTime -> {
        this.setTime(any)
    }

    is Boolean -> {
        this.setCellValue(any)
    }

    is Calendar -> {
        this.setDate(any.time)
    }

    is Date -> {
        this.setDate(any)
    }

    is RichTextString -> {
        this.setCellValue(any)
    }

    is String -> {
        this.setCellValue(any)
    }

    is kotlinx.datetime.LocalDate -> {
        this.setCellValue(any.toJavaLocalDate())
    }

    is kotlinx.datetime.LocalDateTime -> {
        this.setTime(any.toJavaLocalDateTime())
    }

    // Another option would be to serialize everything else to string,
    // but people can convert columns to string with any serialization framework they want
    // so i think toString should do until more use cases arise.
    else -> {
        this.setCellValue(any.toString())
    }
}

/**
 * Set LocalDateTime value correctly also if date have zero value in Excel.
 * Zero dates are usually used for storing a time component only,
 * are displayed as 00.01.1900 in Excel and as 30.12.1899 in LibreOffice Calc and also in POI.
 * POI can not set 1899 year directly.
 */
private fun Cell.setTime(localDateTime: LocalDateTime) {
    this.setCellValue(DateUtil.getExcelDate(localDateTime.plusDays(1)) - 1.0)
}

/**
 * Set Date value correctly also if date has zero value in Excel.
 * Zero dates are usually used for storing a time component only,
 * are displayed as 00.01.1900 in Excel and as 30.12.1899 in LibreOffice Calc and also in POI.
 * POI can not set 1899 year directly.
 */
private fun Cell.setDate(date: Date) {
    val calStart = LocaleUtil.getLocaleCalendar()
    calStart.time = date
    this.setTime(calStart.toInstant().atZone(getUserTimeZone().toZoneId()).toLocalDateTime())
}
