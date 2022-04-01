package org.jetbrains.kotlinx.dataframe.io

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.util.CellReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import java.io.File
import java.io.InputStream
import java.net.URL

public fun DataFrame.Companion.readExcel(
    url: URL,
    sheetName: String,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame = readExcel(url.openStream(), sheetName, columns, rowsCount)

public fun DataFrame.Companion.readExcel(
    file: File,
    sheetName: String,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame = readExcel(file.inputStream(), sheetName, columns, rowsCount)

public fun DataFrame.Companion.readExcel(
    fileOrUrl: String,
    sheetName: String,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame = readExcel(asURL(fileOrUrl), sheetName, columns, rowsCount)

public fun DataFrame.Companion.readExcel(
    inputStream: InputStream,
    sheetName: String,
    columns: String? = null,
    rowsCount: Int? = null
): AnyFrame {
    return inputStream.use {
        val sheet = WorkbookFactory.create(inputStream).getSheet(sheetName)

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
