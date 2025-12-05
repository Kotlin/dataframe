package org.jetbrains.kotlinx.dataframe.examples.springboot.web

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*

data class TableView(
    val headers: List<String>,
    val rows: List<List<String>>
)

fun DataFrame<*>.toTableView(): TableView {
    val headers = this.columnNames()
    val rows = this.rows().map { row -> headers.map { h -> row[h].toString() } }
    return TableView(headers, rows)
}
