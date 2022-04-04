package org.jetbrains.kotlinx.dataframe.io

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.forEachRow
import org.jetbrains.kotlinx.dataframe.api.select
import java.io.File
import java.io.InputStream
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

public fun DataFrame.Companion.readExcel(
    url: URL,
    sheetName: String? = null,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame = readExcel(url.openStream(), sheetName, columns, rowsCount)

public fun DataFrame.Companion.readExcel(
    file: File,
    sheetName: String? = null,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame = readExcel(file.inputStream(), sheetName, columns, rowsCount)

public fun DataFrame.Companion.readExcel(
    fileOrUrl: String,
    sheetName: String? = null,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame = readExcel(asURL(fileOrUrl), sheetName, columns, rowsCount)

public fun DataFrame.Companion.readExcel(
    inputStream: InputStream,
    sheetName: String? = null,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame {
    return inputStream.use {
        val wb = WorkbookFactory.create(inputStream)
        val sheet: Sheet = sheetName
            ?.let { wb.getSheet(it) ?: error("Sheet with name $sheetName not found") }
            ?: wb.getSheetAt(0)

        val columnIndexes = if (columns != null) {
            columns.split(",").flatMap {
                if (it.contains(":")) {
                    val (start, end) = it.split(":").map { CellReference.convertColStringToIndex(it) }
                    start..end
                } else {
                    listOf(CellReference.convertColStringToIndex(it))
                }
            }
        } else {
            sheet.getRow(0).map { it.columnIndex }
        }

        val headerRow = sheet.getRow(0)
        val valueRows = sheet.drop(1).let { if (rowsCount != null) it.take(rowsCount) else it }
        val columns = columnIndexes.map { index ->
            val name = headerRow.getCell(index)?.stringCellValue ?: CellReference.convertNumToColString(index)
            val values = valueRows.map {
                val cell: Cell? = it.getCell(index)
                when (cell?.cellType) {
                    CellType._NONE -> error("Cell ${cell.address} of sheet ${sheet.sheetName} has a CellType that should only be used internally. This is a bug, please report https://github.com/Kotlin/dataframe/issues")
                    CellType.NUMERIC -> cell.numericCellValue
                    CellType.STRING -> cell.stringCellValue
                    CellType.FORMULA -> cell.numericCellValue
                    CellType.BLANK -> cell.stringCellValue
                    CellType.BOOLEAN -> cell.booleanCellValue
                    CellType.ERROR -> cell.errorCellValue
                    null -> null
                }
            }
            DataColumn.createWithTypeInference(name, values)
        }
        dataFrameOf(columns)
    }
}

public fun <T> DataFrame<T>.writeExcel(
    path: String,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true
) {
    return writeExcel(File(path), columnsSelector, sheetName, writeHeader)
}

public fun <T> DataFrame<T>.writeExcel(
    file: File,
    columnsSelector: ColumnsSelector<T, *> = { all() },
    sheetName: String? = null,
    writeHeader: Boolean = true
) {
    val wb = HSSFWorkbook()
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

    columns.forEachRow {
        val row = sheet.createRow(i)
        it.values().forEachIndexed { index, any ->
            val cell = row.createCell(index)
            when (any) {
                is AnyRow -> {
                    cell.setCellValue(any.toJson())
                }
                is AnyFrame ->  {
                    cell.setCellValue(any.toJson())
                }
                is Number -> {
                    cell.setCellValue(any.toDouble())
                }
                is LocalDate -> {
                    cell.setCellValue(any)
                }
                is LocalDateTime -> {
                    cell.setCellValue(any)
                }
                is Boolean -> {
                    cell.setCellValue(any)
                }
                is Calendar -> {
                    cell.setCellValue(any.time)
                }
                is Date -> {
                    cell.setCellValue(any)
                }
                is RichTextString -> {
                    cell.setCellValue(any)
                }
                is String -> {
                    cell.setCellValue(any)
                }
                is kotlinx.datetime.LocalDate -> {
                    cell.setCellValue(any.toJavaLocalDate())
                }
                is kotlinx.datetime.LocalDateTime -> {
                    cell.setCellValue(any.toJavaLocalDateTime())
                }
            }
        }
        i++
    }

    file.outputStream().use {
        wb.write(it)
    }
}
